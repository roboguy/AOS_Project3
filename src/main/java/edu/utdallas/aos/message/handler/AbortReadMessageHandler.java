package edu.utdallas.aos.message.handler;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class AbortReadMessageHandler implements MessageHandler<Message>{

	static final Logger logger = LogManager.getLogger(AbortReadMessageHandler.class);
	
	@Override
	public void handleMessage(Message message) {
		
		logger.debug("In ABORT READ Message Handler from "+ message.getNodeID());
		synchronized (Context.lock) {
			String fileName	= message.getFileName();
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fileName);
			
			Semaphore fileSemaphore = fInfo.getFileSemaphore();
			fileSemaphore.release();
			fInfo.setIsReadLocked(false);
			
			fInfo.setFileSemaphore(fileSemaphore);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}
	}

}
