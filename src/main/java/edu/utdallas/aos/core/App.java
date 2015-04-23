package edu.utdallas.aos.core;

import info.siyer.aos.clock.VectorClock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.google.api.client.util.ExponentialBackOff;
import com.google.gson.Gson;

import edu.utdallas.aos.application.Application;
import edu.utdallas.aos.application.ApplicationFactory;
import edu.utdallas.aos.p3.comm.Server;
import edu.utdallas.aos.p3.config.Config;
import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.FileSystemHandler;

/*
 * Initializer class for the Dynamic Voting Protocol Application
 */

public class App {

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Please enter input params.");
			System.exit(0);
		}

		Arguments arguments = parseArgs(args);
		Integer nodeID = Integer.parseInt(arguments.getMyID());
		
		System.setProperty("logFilename", "app" + nodeID + ".log");

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();

		Logger logger = LogManager.getLogger(App.class);
		logger.debug("Reading Configs");

		
		Config conf = readConfig();
		if (conf != null) {
			logger.debug("Configuration Read Successfully");
		}
		
		File testClocks = new File("testClocks");
		if(testClocks.exists()){
			try {
				FileUtils.cleanDirectory(testClocks);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Double meanConseqReqDelay = conf.getMeanConsecutiveRequestDelay()
				.doubleValue();
		
		Context.requestDelay = new ExponentialDistribution(meanConseqReqDelay);
		
		logger.debug("Initializing FileSystem");
		
		/*
		 * Init FS Handler with default FileInfo in each file
		 * Default RU = Number of Nodes (assumed that every node starts off equally)
		 * Default VN = 0
		 */

		
		Integer defaultRU = conf.getN();
		FileInfo defaultInformation = FileInfo.getDefaultInformation(defaultRU, 0);
		String rootPath = "root" + nodeID.toString();
		try {
			Context.fsHandler = new FileSystemHandler(rootPath, defaultInformation);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error(e1.getMessage());
			System.exit(0);
		}

		Context.myInfo = conf.getNodes().get(nodeID);
		
		Context.clock = new VectorClock();
		/*
		 * Initialize nodeInfos hasmap
		 */
		for (Node node : conf.getNodes()) {
			Integer id = node.getId();
			Context.clock.put(id.toString(), 0);
			Context.nodeInfos.put(id, node);
		}
		
		
		String port = Context.myInfo.getPort();
		Integer portNum = Integer.parseInt(port);
		Server TCPServerThread = Server.getInstance();
		TCPServerThread.setPort(portNum);
		
		
		Application application = ApplicationFactory.getApplication(arguments.getMode());
		application.setNumberOfRequests(conf.getTotalNumberOfRequests());
		application.setReadPercent(conf.getParams().getReadOperationPercent());
		application.setReplicationClient(new ReplicationClient());
		Integer minBackoff = conf.getParams().getExpBackoffMin();
		Integer maxBackoff = conf.getParams().getExpBackoffMax();
		
		Context.backoff = new ExponentialBackOff.Builder() 
						.setInitialIntervalMillis(minBackoff)
						.setMaxIntervalMillis(maxBackoff)
						.setMultiplier(2)
						.setRandomizationFactor(0.5)
						.build();
		
		logger.debug("Starting TCP Server Thread");
		
		TCPServerThread.start();

		// Waiting 3 seconds for server to initialize.
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.debug("Starting Applcation Thread");
		Thread appThread = new Thread(application);
		appThread.setName("Application");
		appThread.start();
		
	}

	private static Arguments parseArgs(String[] args) {
		Arguments arguments = new Arguments();
		String mode = "";
		String myID = "";
		for (String arg : args) {
			if (arg.contains("-") && (arg.contains("I") || arg.contains("D"))) {
				mode = arg.replace("-", "");
			} else {
				myID = arg;
			}
		}
		arguments.setMode(mode);
		arguments.setMyID(myID);
		return arguments;
	}
	
	private static Config readConfig() {

		// Logger logger = LogManager.getLogger(App.class);
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("AOS_P3_CONF.json"));
			while (scanner.hasNext()) {
				sb.append(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		String confJson = sb.toString();
		Config conf = gson.fromJson(confJson, Config.class);
		return conf;
	}

}
