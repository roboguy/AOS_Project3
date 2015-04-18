package edu.utdallas.aos.p3.comm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.message.Message;

public class TCPClient {

	private static Logger logger = LogManager.getLogger(TCPClient.class);

	public static void sendMessage(Message message, String hostName, Integer port,
			String toNodeID) throws UnknownHostException, IOException {
		//TODO: Update vector clock with nodeID
		//TODO: Create GSON object to serealize message  and send Message string
		logger.debug("sending request to host: " + hostName);
		Socket clientSocket = new Socket(hostName, port);
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		logger.debug(message);
		//writer.println(message);
		writer.close();
		clientSocket.close();
	}

}
