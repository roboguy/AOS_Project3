package edu.utdallas.aos.core;

import info.siyer.aos.clock.VectorClock;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.comm.TCPClient;
import edu.utdallas.aos.p3.config.ContainsLock;
import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.P;

public abstract class Operation {
	protected final Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * Abstract Methods
	 */
	
	protected abstract Message getMessage(); 
	
	protected abstract String getOperation();
	
	protected abstract FileInfo setFlags(FileInfo fInfo);

	protected abstract ContainsLock getLock(Semaphore rwLock);
	
	protected abstract FileInfo updateVersion(FileInfo fInfo);
	
	protected abstract FileInfo resetFlags(FileInfo fInfo);

	protected abstract Semaphore unlockLock(Semaphore rwLock);

	protected abstract Message getDoneMessage();
	
	protected abstract boolean isLocked(String fileName);
	
	
	/*
	 * Concrete Methods
	 */
	
	public boolean processOperation(String fileName) throws FileNotFoundException{
		
		FileInfo fExists = Context.fsHandler.getReplicatedFiles().get(fileName);

		if (fExists == null) {
			throw new FileNotFoundException();
		}
		boolean quorumObtained = false;
		synchronized (Context.lock) {
			// Increment my entry in the vector clock to signal my readEvent.
			String myID = Context.myInfo.getId().toString();
			Context.clock.increment(myID);
			quorumObtained = requestQuorum(fileName);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(!quorumObtained){
			synchronized (Context.lock) {
				FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(fileName);
				if (fInfo.quorumObtained(Context.DU)) {
					quorumObtained = true;
					//TODO add logging here
					break;
				} else {
					//unlock my lock and try again
					if(isLocked(fileName)){
						Semaphore fileSemaphore = fInfo.getFileSemaphore();
						fileSemaphore = unlockLock(fileSemaphore);
						fInfo.setFileSemaphore(fileSemaphore);
						Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
					}
					abortRequest(fileName);
				}
			} // SYNC Block ENDS
			exponentialBackOff();
			quorumObtained = requestQuorum(fileName);
			
		}//While Quorum not obtained
		
		return quorumObtained;
		
	}//Process Operation
	
	public boolean requestQuorum(String fileName){
		synchronized (Context.lock) {

			String myID = Context.myInfo.getId().toString();

			FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(fileName);
			//ReentrantReadWriteLock rwLock = fInfo.getReadWriteLock();
			Semaphore fileSemaphore = fInfo.getFileSemaphore();
			String content = "";
			try {
				content = Context.fsHandler.getFilesystem().read(fileName);
			} catch (FileNotFoundException | NoSuchElementException e1) {
				e1.printStackTrace();
			}
			
			ContainsLock container 	= getLock(fileSemaphore);
			boolean lockAcquired 	= container.isLockAcquired();
			fileSemaphore			= container.getRwLock();
			if(lockAcquired){
				P myP = new P(myID, fInfo.getVersionNumber(), fInfo.getReplicasUpdated(), content);
				fInfo.getP().put(myID, myP);
				fInfo = setFlags(fInfo);
				String clock = VectorClock.serializeClock(Context.clock);
				Integer VN = fInfo.getVersionNumber();
				Integer RU = fInfo.getReplicasUpdated();
				// String content = "";
				try {
					content = Context.fsHandler.getFilesystem().read(
							fileName);
				} catch (FileNotFoundException | NoSuchElementException e) {
					e.printStackTrace();
				}
				Message lockMessage = getMessage();
				
				lockMessage.setClock(clock);
				lockMessage.setContent(content);
				lockMessage.setFileName(fileName);
				lockMessage.setNodeID(myID);
				lockMessage.setRU(RU);
				lockMessage.setVN(VN);
				Iterator<Entry<Integer, Node>> iter = Context.nodeInfos.entrySet().iterator();
				
				while(iter.hasNext()){

					Entry<Integer, Node> entry = iter.next();
					Integer toNodeID = entry.getKey();
					Node node = entry.getValue();
					String hostName = node.getHost();
					Integer port = Integer.parseInt(node.getPort());
					/*
					 * TCPClient.sendMessage(message, hostName, port,
					 * toNodeID);
					 */
					if (toNodeID != Context.myInfo.getId()) {
						try {
							TCPClient.sendMessage(lockMessage, hostName,port, toNodeID.toString());
						} catch (IOException e) {
							logger.error("Unable to send Message to node: "	+ toNodeID);
							e.printStackTrace();
						}
					}			
				}//While Sending to all nodes ENDS
				
			}//If lock Acquired ENDS
			else {
				return false;
			}
			fInfo.setFileSemaphore(fileSemaphore);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}//Sync Block ENDS

		return false;
	}

	public void unlockFile(String fileName){
		
		synchronized (Context.lock) {
			FileInfo fExists = Context.fsHandler.getReplicatedFiles().get(
					fileName);

			if (fExists == null) {
				return;
			}

			String myID = Context.myInfo.getId().toString();
			// Increment Vector Clock to indicate my send event;
			Context.clock.increment(myID);
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter("testClocks/" + fileName + ".clock",	true)));
				StringBuilder sb = new StringBuilder();
				sb.append(VectorClock.serializeClock(Context.clock) + "::");
				sb.append(getOperation());
				out.println(sb.toString());
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fileName);
			fInfo 			= updateVersion(fInfo);
			Integer replicasUpdated = fInfo.getP().size();
			fInfo.setReplicasUpdated(replicasUpdated);
			Message doneMsg = getDoneMessage(); 
			
			String content = "";
			Integer VN = fInfo.getVersionNumber();
			Integer RU = fInfo.getReplicasUpdated();
			String clock = VectorClock.serializeClock(Context.clock);

			try {
				content = Context.fsHandler.getFilesystem().read(fileName);
			} catch (FileNotFoundException | NoSuchElementException e) {
				e.printStackTrace();
			}
			doneMsg.setClock(clock);
			doneMsg.setContent(content); 
			doneMsg.setFileName(fileName);
			doneMsg.setNodeID(myID);
			doneMsg.setRU(RU);
			doneMsg.setVN(VN);

			for (Entry<String, P> entry : fInfo.getP().entrySet()) {
				String key = entry.getKey();

				// Dont send done message to myself
				if (key.equals(myID)) {
					continue;
				}
				P pi = entry.getValue();
				String ID = pi.getNodeID();
				Node node = Context.nodeInfos.get(Integer.parseInt(ID));
				String hostName = node.getHost();
				Integer port = Integer.parseInt(node.getPort());
				Integer count = pi.getCount();

				for (int i = 1; i <= count; i++) {
					try {
						TCPClient.sendMessage(doneMsg, hostName, port, ID);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}// For every time we received lock message from this node ends

			}// For each Pi in P we send done read message to unlock
			Semaphore fSemaphore = fInfo.getFileSemaphore();
			fSemaphore = unlockLock(fSemaphore);
			fInfo = resetFlags(fInfo);
			fInfo.setFileSemaphore(fSemaphore);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}//Sync Block ENDS
	}

	public void abortRequest(String fileName){
		
		synchronized (Context.lock) {
			
			FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(fileName);
			String myID = Context.myInfo.getId().toString();
			
			Message abortMsg = getAbortMessage();
			abortMsg.setFileName(fileName);
			abortMsg.setNodeID(myID);
			
			for (Entry<String, P> entry : fInfo.getP().entrySet()) {
				String key = entry.getKey();

				// Dont send done message to myself
				if (key.equals(myID)) {
					continue;
				}
				P pi = entry.getValue();
				String ID = pi.getNodeID();
				Node node = Context.nodeInfos.get(Integer.parseInt(ID));
				String hostName = node.getHost();
				Integer port = Integer.parseInt(node.getPort());
				Integer count = pi.getCount();

				for (int i = 1; i <= count; i++) {
					try {
						TCPClient.sendMessage(abortMsg, hostName, port, ID);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}// For every time we received lock message from this node ends

			}// For each Pi in P we send done read message to unlock
			
			fInfo = resetFlags(fInfo);
			//fInfo.setReadWriteLock(rwLock);
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}
		
		
	}

	protected abstract Message getAbortMessage();
	
 	private void exponentialBackOff() {
		
		// Exponential Backoff
		long backoffDuration = 50;
		try {
			backoffDuration = Context.backoff.nextBackOffMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("Backoff for " + backoffDuration);
		if (backoffDuration > Context.backoff.getMaxIntervalMillis()) {
			Context.backoff.reset();
		}

		try {
			Thread.sleep(backoffDuration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
