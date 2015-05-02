package edu.utdallas.aos.message.handler;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class AbortWriteMessageHandler implements MessageHandler<Message>{

	static final Logger logger = LogManager.getLogger(AbortWriteMessageHandler.class);
	
	@Override
	public void handleMessage(Message message) {
		logger.debug("In ABORT WRITE Message Handler from "+ message.getNodeID());
		synchronized (Context.lock) {
			String fileName	= message.getFileName();
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fileName);
			
			ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			rwLock.writeLock().unlock();
			fInfo.setIsWriteLocked(false);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}
	}

}
