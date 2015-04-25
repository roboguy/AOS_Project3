package edu.utdallas.aos.message.handler;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class DoneWriteMessageHandler implements MessageHandler<Message> {

	static final Logger logger = LogManager.getLogger(DoneWriteMessageHandler.class);

	
	@Override
	public void handleMessage(Message message) {
		logger.debug("In DONEWRITE Message Handler from "+ message.getNodeID());
		synchronized (Context.lock) {
			String fileName	= message.getFileName();
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fileName);
			Integer versionNumber = message.getVN();
			String content 	= message.getContent();
			
			if(versionNumber > fInfo.getVersionNumber()){
				try {
					logger.debug("Updating content");
					Context.fsHandler.getFilesystem().write(fileName, content);
					//Update Version number from message
					fInfo.setVersionNumber(versionNumber);
					logger.debug("Updated VN to " + versionNumber);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			rwLock.writeLock().unlock();
			fInfo.setIsWriteLocked(false);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}
		
	}

}
