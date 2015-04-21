package edu.utdallas.aos.message.handler;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.P;

public class ReadSuccessMessageHandler implements MessageHandler<Message> {
	
	static final Logger logger = LogManager.getLogger(ReadSuccessMessageHandler.class);
	
	@Override
	public void handleMessage(Message message) {

		String toNodeId	= message.getNodeID();
		logger.debug("In READSUCCESS Message Handler from "+ toNodeId);
		
		/*
		 * Extract critical parameters from message and enter them 
		 * into the corresponding file's P set.
		 */
		synchronized (Context.lock) {
			
			String fileName		= message.getFileName(); 
			FileInfo fInfo 		= Context.fsHandler.getReplicatedFiles().get(fileName);
			String id 			= message.getNodeID();
			Integer VN			= message.getVN();
			Integer RU 			= message.getRU();
			String content		= message.getContent();
			
			if(message.getVN() > fInfo.getVersionNumber()){
				try {
					Context.fsHandler.getFilesystem().write(fileName, content);
					fInfo.setVersionNumber(VN);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			P Pi				= new P(id, VN, RU, content);
			Pi.setCount(1);
			
			boolean contains 	= fInfo.getP().containsKey(id);
			
			if(contains){
				P pExists 		= fInfo.getP().get(id);
				Integer count	= pExists.getCount();
				count++;
				pExists.setCount(count);
				fInfo.getP().put(id, pExists);
			} else {
				fInfo.getP().put(id, Pi);
			}
			
			Context.fsHandler.getReplicatedFiles().put(fileName, fInfo);
		}//SYNC Block ENDS
		
	}//handleMessage ENDS

}
