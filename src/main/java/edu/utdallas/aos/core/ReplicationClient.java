package edu.utdallas.aos.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplicationClient {

	static final Logger logger = LogManager.getLogger(ReplicationClient.class);

	public String readFile(String fileName) throws FileNotFoundException, NoSuchElementException {
		
		Operation readOperation = new ReadOperation();
		boolean readQObtained 	= readOperation.processOperation(fileName);
		
		String content = "";
		if (readQObtained) {
			content = Context.fsHandler.getFilesystem().read(fileName);
		}
		return content;	

	}//ReadFile ENDS

	public void readUnlockFile(String fileName) {
		Operation readUnlock = new ReadOperation();
		readUnlock.unlockFile(fileName);
	}//Read Unlock ENDS

	public void writeFile(String fileName, String content)throws FileNotFoundException {
		Operation writeOperation = new WriteOperation();
		
		//Blocking call, will not return till quorum Obtained
		boolean writeQObtained 	= writeOperation.processOperation(fileName);
		
		if (writeQObtained) {
			try {
				Context.fsHandler.getFilesystem().write(fileName, content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writeUnlockFile(String fileName){
		Operation writeUnlock = new ReadOperation();
		writeUnlock.unlockFile(fileName);
	}//Write Unlock ENDS

	
}
