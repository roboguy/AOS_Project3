package edu.utdallas.aos.message.handler;

import edu.utdallas.aos.message.Message;

public class ReadMessageHandler implements MessageHandler<Message>{

	@Override
	public void handleMessage(Message message) {
		// TODO Auto-generated method stub
		System.out.println("In READ Message Handler");
	}
	
}
