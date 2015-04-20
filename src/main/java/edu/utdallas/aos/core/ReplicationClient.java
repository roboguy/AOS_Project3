package edu.utdallas.aos.core;

import info.siyer.aos.clock.VectorClock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.message.DoneReadMessage;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadMessage;
import edu.utdallas.aos.p3.comm.TCPClient;
import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.P;

public class ReplicationClient {
	
	static final Logger logger = LogManager.getLogger(ReplicationClient.class);

	public String readFile(String fileName) throws FileNotFoundException, NoSuchElementException {

		boolean qObtained = requestReadQuorum(fileName);
		String content = "";
		while (!qObtained) {
			
			// Exponential Backoff
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
					qObtained = true;
					break;
				}
			} // SYNC Block ENDS

			qObtained = requestReadQuorum(fileName);
		}//While Quorum Not obtained keep trying;
		
		if(qObtained){
			content = Context.fsHandler.getFilesystem().read(fileName);
		}
		return content;
	}

	private boolean requestReadQuorum(String fileName) {

		synchronized (Context.lock) {

			//Increment my entry in the vector clock to signal my readEvent.
			Context.clock.increment(Context.myInfo.getId().toString());
			
			FileInfo fInfo = Context.fsHandler.getReplicatedFiles()
					.get(fileName);
			ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			fInfo.setReadWriteLock(rwLock);

			boolean localLockAcquired = rwLock.readLock().tryLock();
			if (!fInfo.quorumObtained()) {
				if (localLockAcquired) {
					System.out.println("LOCAL READ LOCK ACQUIRED");
					fInfo.setIsReadLocked(true);
					/*
					 * Send READ MESSAGE to all known nodes
					 
					 */
					/*
					 * Creating a READ Message
					 */
					String clock 		= VectorClock.serializeClock(Context.clock);
					Integer	VN 			= fInfo.getVersionNumber();
					Integer RU 			= fInfo.getReplicasUpdated();
					String content		= "";
					try {
						 content		= Context.fsHandler.getFilesystem().read(fileName);
					} catch (FileNotFoundException | NoSuchElementException e) {
						e.printStackTrace();
					}
					Message readMessage = new ReadMessage();
					
					readMessage.setClock(clock);
					readMessage.setContent(content);
					readMessage.setFileName(fileName);
					readMessage.setNodeID(Context.myInfo.getId().toString());
					readMessage.setRU(RU);
					readMessage.setVN(VN);
					
					Iterator<Entry<Integer, Node>> iter = Context.nodeInfos.entrySet().iterator();
					while(iter.hasNext()){
						Entry<Integer, Node> entry = iter.next();
						Integer toNodeID 	= entry.getKey(); 
						Node node 	= entry.getValue();
						String hostName = node.getHost();
						Integer port	= Integer.parseInt(node.getPort());
						/*
						 *  TCPClient.sendMessage(message, hostName, port, toNodeID);
						 */
						if(toNodeID != Context.myInfo.getId()){
							try {
								TCPClient.sendMessage(readMessage, hostName, port, toNodeID.toString());
							} catch (IOException e) {
								logger.error("Unable to send Message to node: " + toNodeID);
								e.printStackTrace();
							}
						}
					}//While loop ENDS
					
				} else {
					return false;
				}
			} else {
				//If we already have quorum then return true;
				return true;
			}
			fInfo.setReadWriteLock(rwLock);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}// SYNC Block ENDS
		
		//Start Timer and wait till 500 ms expires
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
		
	public void readUnlockFile(String fileName){
		/*
		 * Unlock all read/write local and remote in P
		 * SEND DONE MESSAGEs
		 */
		synchronized (Context.lock) {
			
			//Increment Vector Clock to indicate my send event;
			Context.clock.increment(Context.myInfo.getId().toString());
			
			FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(fileName);
			Message doneReadMsg = new DoneReadMessage();
			String content 	= "";
			Integer VN 		= fInfo.getVersionNumber();
			Integer RU 		= fInfo.getReplicasUpdated();
			String clock 	= VectorClock.serializeClock(Context.clock);
			String myID		= Context.myInfo.getId().toString();
			try {
				content = Context.fsHandler.getFilesystem().read(fileName);
			} catch (FileNotFoundException | NoSuchElementException e) {
				e.printStackTrace();
			}		
			doneReadMsg.setClock(clock);
			doneReadMsg.setContent(content);
			doneReadMsg.setFileName(fileName);
			doneReadMsg.setNodeID(myID);
			doneReadMsg.setRU(RU);
			doneReadMsg.setVN(VN);

			for(P pi : fInfo.getP()){
				
				String ID = pi.getNodeID();
				Node node = Context.nodeInfos.get(ID);
				String hostName = node.getHost();
				Integer port 	= Integer.parseInt(node.getPort());
				Integer count = pi.getCount();
				
				for(int i = 1; i <= count; i++){
					try {
						TCPClient.sendMessage(doneReadMsg, hostName, port, ID);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}//For every time we received lock message from this node ends
			}//For each Pi in P we send done read message to unlock
			
			ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			rwLock.readLock().unlock();
			fInfo.setIsReadLocked(false);
			fInfo.resetQuorumCondition();
			fInfo.setReadWriteLock(rwLock);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
			
		}//Sync block ends
		
	}

}


