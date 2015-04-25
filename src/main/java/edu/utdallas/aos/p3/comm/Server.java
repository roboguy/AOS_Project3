package edu.utdallas.aos.p3.comm;

import info.siyer.aos.clock.VectorClock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.DoneReadMessage;
import edu.utdallas.aos.message.DoneWriteMessage;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadMessage;
import edu.utdallas.aos.message.ReadSuccessMessage;
import edu.utdallas.aos.message.WriteMessage;
import edu.utdallas.aos.message.WriteSuccessMessage;
import edu.utdallas.aos.message.handler.DoneReadMessageHandler;
import edu.utdallas.aos.message.handler.DoneWriteMessageHandler;
import edu.utdallas.aos.message.handler.ReadMessageHandler;
import edu.utdallas.aos.message.handler.ReadSuccessMessageHandler;
import edu.utdallas.aos.message.handler.WriteMessageHandler;
import edu.utdallas.aos.message.handler.WriteSuccessMessageHandler;


/*
 * This class implements a singleton TCP server.
 */

public class Server extends Thread {

	private static Server server = null;
	public static volatile Boolean isRunning = true;
	public static volatile Boolean isFailed = false;
	public static ServerSocket serverSock = null;
	private static Integer port = 100;
	private static Logger logger = null;
	private static Gson serverGson = new Gson();
	public void setPort(Integer por) {
		port = por;
	}
	
	//Private constructor
	private Server() {

	}

	//Get singleton instance if it exists, otherwise create it and return instance.
	public static Server getInstance() {
		logger = LogManager.getLogger(Server.class);
		
		if (server == null) {
			server = new Server();
			server.setName("Server");
		}
		return server;
	}

	@Override
	public void run() {
		Logger logger = LogManager.getLogger(Server.class);
		logger.debug("Starting TCP Server to listen for CS Requests");
		go();
		
	}
	
	
	public static void go()
	{
		try
		{
			//Create a server socket at port 5000
			serverSock = new ServerSocket(Server.port);
			logger.info("Server listening on port:" + Server.port);
			//Server goes into a permanent loop accepting connections from clients			
			while(isRunning)
			{
				logger.debug("Accpeting Requests now..");
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket sock = serverSock.accept();
				BufferedReader inFromClient =
			               new BufferedReader(new InputStreamReader(sock.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = inFromClient.readLine();
				while(line != null){
					sb.append(line);
					line = inFromClient.readLine();
				}
				logger.debug("Started Request Handler to handle request.");

				//Handle Message String
				String messageStr 		= sb.toString();
				Message message 		= null;
				if(messageStr.contains("\"READ\"")){
					message = serverGson.fromJson(messageStr, ReadMessage.class);
				} else if(messageStr.contains("\"READSUCCESS\"")){
					message = serverGson.fromJson(messageStr, ReadSuccessMessage.class);
				}else if(messageStr.contains("\"DONEREAD\"")){
					message = serverGson.fromJson(messageStr, DoneReadMessage.class);
				}else if(messageStr.contains("\"WRITE\"")){
					message = serverGson.fromJson(messageStr, WriteMessage.class);
				}else if(messageStr.contains("\"WRITESUCCESS\"")){
					message = serverGson.fromJson(messageStr, WriteSuccessMessage.class);
				}else if(messageStr.contains("\"DONEWRITE\"")){
					message = serverGson.fromJson(messageStr, DoneWriteMessage.class);
				}
				
				String messageType = message.getType();
				VectorClock msgClk	= VectorClock.deserializeClock(message);
				logger.debug(messageStr);
				
				synchronized (Context.lock) {
					Context.clock = Context.clock.merge(msgClk);
					Context.clock.increment(message.getNodeID());
				}
				
				if(isFailed){
					logger.debug("SERVER DOWN. IGNORING REQUEST.");
					continue;
				}
				if(messageType.equals("READ")){
					new ReadMessageHandler().handleMessage(message);
				} else if (messageType.equals("WRITE")){
					new WriteMessageHandler().handleMessage(message);
				} else if (messageType.equals("READSUCCESS")){
					new ReadSuccessMessageHandler().handleMessage(message);
				} else if (messageType.equals("WRITESUCCESS")){
					new WriteSuccessMessageHandler().handleMessage(message);
				} else if (messageType.equals("DONEREAD")){
					new DoneReadMessageHandler().handleMessage(message);
				} else if (messageType.equals("DONEWRITE")){
					new DoneWriteMessageHandler().handleMessage(message);
				} else {
					logger.error("Unable to handle unkown message type");
				}
				
			}//While Server is Running ENDS

		}//Try Block ENDS
		catch(IOException ex)
		{
			if(isRunning == true){
				ex.printStackTrace();
				logger.error(ex.getMessage());
			}
			else {
				logger.info("Server Shut Down");
			}

		}
		finally{
			try {
				serverSock.close();
			} catch (IOException e) {
				if(isRunning == true){
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				else {
					logger.info("Server Shut Down");
				}
			}
		}//Finally ShutDown Server gracefully
	}
	
}
