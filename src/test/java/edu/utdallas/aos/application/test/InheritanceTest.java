package edu.utdallas.aos.application.test;

import edu.utdallas.aos.core.Context;
import org.junit.Test;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadMessage;
import edu.utdallas.aos.message.handler.MessageHandler;
import edu.utdallas.aos.message.handler.ReadMessageHandler;

public class InheritanceTest {

	@Test
	public void inheritTest(){
		Message read = new ReadMessage();
		MessageHandler<Message> msgHandler = new ReadMessageHandler();
		msgHandler.handleMessage(read);
	}
	
	@Test
	public void lockTest(){
		synchronized (Context.lock) {
			System.out.println("In shared lock");
		}
	}
}
