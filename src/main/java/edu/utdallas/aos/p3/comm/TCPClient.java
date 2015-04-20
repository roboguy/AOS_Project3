package edu.utdallas.aos.p3.comm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.utdallas.aos.message.Message;

public class TCPClient {

	private static Logger logger = LogManager.getLogger(TCPClient.class);
	private static Gson tcpGson = new Gson();
	public static void sendMessage(Message message, String hostName, Integer port,
			String toNodeID) throws UnknownHostException, IOException {
		
		//GSON object to serealize message  and send Message string
		String messageString = tcpGson.toJson(message);
		logger.debug("sending request to host: " + hostName);
		
		Socket clientSocket = new Socket(hostName, port);
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		logger.debug(messageString);
		
		writer.println(messageString);
		writer.close();
		
		clientSocket.close();
	}

}
