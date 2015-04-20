package edu.utdallas.aos.message.handler;

import edu.utdallas.aos.message.Message;

public class DoneReadMessageHandler implements MessageHandler<Message> {

	@Override
	public void handleMessage(Message message) {
		// TODO Auto-generated method stub
		/*
		 * If message's VN > my VersionNumber
		 * 		then write to my file with message's content
		 * Unlock my file's readLock
		 * 
		 */
	}

}
