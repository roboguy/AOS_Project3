package edu.utdallas.aos.message.handler;

import edu.utdallas.aos.message.Message;

public interface MessageHandler<T extends Message> {
	
	public void handleMessage(T message);
	
}
