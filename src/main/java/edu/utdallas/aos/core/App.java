package edu.utdallas.aos.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.application.Application;
import edu.utdallas.aos.application.ApplicationFactory;
import edu.utdallas.aos.p3.comm.Server;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.FileSystemHandler;

/*
 * Initializer class for the Dynamic Voting Protocol Application
 */

public class App {

	private static Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Please enter input params.");
			System.exit(0);
		}

		Arguments arguments = parseArgs(args);
		
		//TODO: Init Node Info from Conig
		
		//Init FS Handler
		FileInfo defaultInformation = FileInfo.getDefaultInformation(5, 0);
		Context.fsHandler = new FileSystemHandler("root0", defaultInformation);

		System.out.println("My Node ID: " + arguments.getMyID());

		Application application = ApplicationFactory.getApplication(arguments
				.getMode());

		logger.debug("Starting TCP Server Thread");
		Server TCPServerThread = Server.getInstance();
		TCPServerThread.start();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("Starting Applcation Thread");
		Thread t1 = new Thread(application);
		t1.setName("Application");
		t1.start();
		
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

}
