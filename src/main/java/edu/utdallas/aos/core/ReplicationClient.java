package edu.utdallas.aos.core;

import info.siyer.aos.clock.VectorClock;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utdallas.aos.p3.filesystem.FileInfo;

public class ReplicationClient {

	static final Logger logger = LogManager.getLogger(ReplicationClient.class);

	public OpContainer readFile(String fileName) throws FileNotFoundException,
			NoSuchElementException {

		Operation readOperation = new ReadOperation();
		boolean readQObtained = readOperation.processOperation(fileName);

		String content = "";
		if (readQObtained) {
			FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(
					fileName);
			if (fInfo.getVersionNumber() < fInfo.getLatestVN()) {
				content = fInfo.getLatestContent();
			} else {
				content = Context.fsHandler.getFilesystem().read(fileName);
			}

			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter("testClocks/" + fileName + ".clock",
								true)));
				StringBuilder sb = new StringBuilder();
				sb.append(Context.myInfo.getId() + "::");
				sb.append(VectorClock.serializeClock(Context.clock) + "::");
				out.print(sb.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			logger.debug("OPERATION ABORTED");
		}
		OpContainer container = new OpContainer(fileName, readQObtained, content);
		return container;

	}// ReadFile ENDS

	public void readUnlockFile(String fileName) {
		Operation readUnlock = new ReadOperation();
		readUnlock.unlockFile(fileName);
	}// Read Unlock ENDS

	public OpContainer writeFile(String fileName, String content)
			throws FileNotFoundException {
		Operation writeOperation = new WriteOperation();

		// Blocking call, will not return till quorum Obtained
		boolean writeQObtained = writeOperation.processOperation(fileName);

		if (writeQObtained) {
			try {
				Context.fsHandler.getFilesystem().write(fileName, content);
				//Write test clock information to file
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("testClocks/" + fileName + ".clock", true)));

				StringBuilder sb = new StringBuilder();
				sb.append(Context.myInfo.getId() + "::");
				sb.append(VectorClock.serializeClock(Context.clock) + "::");
				out.print(sb.toString());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			logger.debug("OPERATION ABORTED");
		}
		return new OpContainer(fileName, writeQObtained, content);
	}

	public void writeUnlockFile(String fileName) {
		Operation writeUnlock = new WriteOperation();
		writeUnlock.unlockFile(fileName);
	}// Write Unlock ENDS

}
