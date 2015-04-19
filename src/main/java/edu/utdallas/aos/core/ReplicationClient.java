package edu.utdallas.aos.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.utdallas.aos.p3.filesystem.FileInfo;

public class ReplicationClient {

	public String readFile(String fileName) throws FileNotFoundException, NoSuchElementException {

		boolean qObtained = test(fileName);

		while (!qObtained) {
			long backoffDuration = 50;
			try {
				backoffDuration = Context.backoff.nextBackOffMillis();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(backoffDuration > Context.backoff.getMaxIntervalMillis()){
				Context.backoff.reset();
			}
			
			try {
				Thread.sleep(backoffDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			synchronized (Context.lock) {
				FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(
						fileName);
				if (fInfo.quorumObtained()) {
					break;
				}
			} // SYNC Block ENDS

			test(fileName);
		}//While Quorum Not obtained keep trying;
		
		String content = Context.fsHandler.getFilesystem().read(fileName);
		return content;
	}

	private boolean test(String fileName) {

		synchronized (Context.lock) {

			// Exponential Backoff

			FileInfo filesInformation = Context.fsHandler.getReplicatedFiles()
					.get(fileName);
			ReentrantReadWriteLock rwLock = filesInformation.getReadWriteLock();
			filesInformation.setReadWriteLock(rwLock);

			boolean localLockAcquired = rwLock.readLock().tryLock();
			if (!filesInformation.quorumObtained()) {
				if (localLockAcquired) {
					System.out.println("LOCAL READ LOCK ACQUIRED");
					filesInformation.setIsReadLocked(true);
					/*
					 * Send READ MESSAGE to all know0n nodes
					 * TCPClient.sendMessage(message, hostName, port, toNodeID);
					 */
				} else {
					return false;
				}
			} else {
				return true;
			}

		}// SYNC Block ENDS
		
		//Start Timer and wait till 500 ms expires
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@SuppressWarnings("unused")
	private void unlockFIle(String fileName){
		/*
		 * Unlock all read/write local and remote in P
		 * SEND DONE MESSAGEs
		 */
	}

}


