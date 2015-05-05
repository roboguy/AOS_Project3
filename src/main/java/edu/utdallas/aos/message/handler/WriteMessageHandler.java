package edu.utdallas.aos.message.handler;

import info.siyer.aos.clock.VectorClock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.WriteSuccessMessage;
import edu.utdallas.aos.p3.comm.TCPClient;
import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class WriteMessageHandler implements MessageHandler<Message>{

	static final Logger logger = LogManager.getLogger(WriteMessageHandler.class);
	
	@Override
	public void handleMessage(Message message) {
		String toNodeId	= message.getNodeID();
		logger.debug("In WRITE Message Handler from "+toNodeId);
		synchronized (Context.lock) {
			boolean gotWriteLock = false;
			String fName	= message.getFileName();
			FileInfo fInfo 	= Context.fsHandler.getReplicatedFiles().get(fName);
			Semaphore  rwLock	= fInfo.getFileSemaphore();

			gotWriteLock = rwLock.tryAcquire(10);

			if(gotWriteLock){
				String content="";
				try {
					content=Context.fsHandler.getFilesystem().read(fName);
				} catch (FileNotFoundException | NoSuchElementException e) {
					logger.error("File Not found exception");
					e.printStackTrace();
				}
				
				int RU	= fInfo.getReplicasUpdated();
				int VN	= fInfo.getVersionNumber();
				String clockStr	= VectorClock.serializeClock(Context.clock);
				Message writeSuccessMsg	= new WriteSuccessMessage();
				writeSuccessMsg.setClock(clockStr);
				writeSuccessMsg.setContent(content);
				writeSuccessMsg.setFileName(fName);
				writeSuccessMsg.setRU(RU);
				writeSuccessMsg.setVN(VN);
				writeSuccessMsg.setNodeID(Context.myInfo.getId().toString());
				Node toNode		= Context.nodeInfos.get(Integer.parseInt(toNodeId));
				Integer port	= Integer.parseInt(toNode.getPort());
				String hostName = toNode.getHost();
				
				try {
					TCPClient.sendMessage(writeSuccessMsg, hostName, port, toNodeId);
				} catch (IOException e) {
					logger.error("Unable to send message to Node: "+toNodeId);
					e.printStackTrace();
				}
				fInfo.setFileSemaphore(rwLock);
				Context.fsHandler.getReplicatedFiles().put(fName, fInfo);
				
			}//If gotWriteLock ENDS
			
		}//SYNC Block ENDS
	}

}
