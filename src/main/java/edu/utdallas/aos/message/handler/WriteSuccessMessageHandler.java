package edu.utdallas.aos.message.handler;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.p3.filesystem.FileInfo;
import edu.utdallas.aos.p3.filesystem.P;

public class WriteSuccessMessageHandler implements MessageHandler<Message> {
	
	static final Logger logger = LogManager.getLogger(WriteSuccessMessageHandler.class);


	@Override
	public void handleMessage(Message message) {
		String toNodeId	= message.getNodeID();
		logger.debug("In WRITESUCCESS Message Handler from "+ toNodeId);
		
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
