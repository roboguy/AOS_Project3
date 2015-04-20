package edu.utdallas.aos.message.handler;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class DoneReadMessageHandler implements MessageHandler<Message> {

	
	static final Logger logger = LogManager.getLogger(DoneReadMessageHandler.class);
	@Override
	public void handleMessage(Message message) {
		
		/*
		 * If message's VN > my VersionNumber
		 * 		then write to my file with message's content
		 * Unlock my file's readLock
		 * 
		 */
		
		logger.debug("In DONEREAD Message Handler from "+ message.getNodeID());
		synchronized (Context.lock) {
			String fileName	= message.getFileName();
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fileName);
			Integer versionNumber = message.getVN();
			String content 	= message.getContent();
			
			if(versionNumber > fInfo.getVersionNumber()){
				//This code shoud not run
				try {
					Context.fsHandler.getFilesystem().write(fileName, content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			rwLock.readLock().unlock();
			fInfo.setIsReadLocked(false);
			
			fInfo.setReadWriteLock(rwLock);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}
		
	}

}
