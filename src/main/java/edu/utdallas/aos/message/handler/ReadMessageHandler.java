package edu.utdallas.aos.message.handler;

import info.siyer.aos.clock.VectorClock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadSuccessMessage;
import edu.utdallas.aos.p3.comm.TCPClient;
import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class ReadMessageHandler implements MessageHandler<Message>{

	static final Logger logger = LogManager.getLogger(ReadMessageHandler.class);

	@Override
	public void handleMessage(Message message) {
		
		String toNodeId	= message.getNodeID();
		logger.debug("In READ Message Handler from "+toNodeId);
		synchronized(Context.lock)
		{
			if(message.getType().equals("READ"))
			{
				String fName 	= message.getFileName();
				FileInfo fInfo	= Context.fsHandler.getReplicatedFiles().get(fName);
				
				ReentrantReadWriteLock  rwLock=fInfo.getReadWriteLock();
				if(rwLock.readLock().tryLock())
				{
					//1. Get Read Content
					//2. Get clockVN, RU from FInfo
					//3. Serialize clock form context
					//4. use TCP client to send
					//Preserve the state.
					String content="";
					try {
						content=Context.fsHandler.getFilesystem().read(fName);
					} catch (FileNotFoundException e) {
						logger.error("File Not found exception");
						e.printStackTrace();
					} catch (NoSuchElementException e) {
						
						logger.error("There is no element in the FileSystem");
						e.printStackTrace();
					}
					int RU=fInfo.getReplicasUpdated();
					int VN=fInfo.getVersionNumber();
					String clockStr=VectorClock.serializeClock(Context.clock);
					Message readSuccessMessage=new ReadSuccessMessage();
					readSuccessMessage.setClock(clockStr);
					readSuccessMessage.setContent(content);
					readSuccessMessage.setFileName(fName);
					readSuccessMessage.setRU(RU);
					readSuccessMessage.setVN(VN);
					readSuccessMessage.setNodeID(Context.myInfo.getId().toString());
					Node toNode=Context.nodeInfos.get(toNodeId);
					Integer port= Integer.parseInt(toNode.getPort());
					String hostName = toNode.getHost();
					try {
						TCPClient.sendMessage(readSuccessMessage, hostName, port, toNodeId);
					} catch (IOException e) {
						logger.error("Unable to send message to Node: "+toNodeId);
						e.printStackTrace();
					}
					fInfo.setReadWriteLock(rwLock);
					Context.fsHandler.getReplicatedFiles().put(fName, fInfo);
				}
				//No else was required since that is the case of abort.
			}
		}		
	}
	
}
