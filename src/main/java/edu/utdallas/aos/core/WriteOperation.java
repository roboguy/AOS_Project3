package edu.utdallas.aos.core;

import java.util.concurrent.Semaphore;

import edu.utdallas.aos.message.AbortWriteMessage;
import edu.utdallas.aos.message.DoneWriteMessage;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.WriteMessage;
import edu.utdallas.aos.p3.config.ContainsLock;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class WriteOperation extends Operation {

	@Override
	protected Message getMessage() {
		return new WriteMessage();
	}

	@Override
	protected FileInfo setFlags(FileInfo fInfo) {
		//System.out.println("LOCAL WRITE LOCK ACQUIRED");
		fInfo.setIsWriteLocked(true);
		return fInfo;
	}

	@Override
	protected ContainsLock getLock(Semaphore fileSemaphore) {
		boolean localLockAcquired = fileSemaphore.tryAcquire(10);
		ContainsLock container = new ContainsLock();
		container.setLockAcquired(localLockAcquired);
		container.setRwLock(fileSemaphore);
		return container;
	}

	@Override
	protected FileInfo updateVersion(FileInfo fInfo) {
		// Update Version Number
		Integer versionNumber = fInfo.getVersionNumber();
		versionNumber += 1;
		fInfo.setVersionNumber(versionNumber);
		return fInfo;
	}

	@Override
	protected FileInfo resetFlags(FileInfo fInfo) {
		fInfo.setIsWriteLocked(false);
		fInfo.resetQuorumCondition();
		return fInfo;
	}

	@Override
	protected Semaphore unlockLock(Semaphore fileSemaphore) {
		fileSemaphore.release(10);
		return fileSemaphore;
	}

	@Override
	protected Message getDoneMessage() {
		return new DoneWriteMessage();
	}

	@Override
	protected String getOperation() {
		return "WRITE";
	}

	@Override
	protected Message getAbortMessage() {
		return new AbortWriteMessage();
	}

	@Override
	protected boolean isLocked(String fileName) {
		FileInfo fInfo = Context.fsHandler.getReplicatedFiles().get(fileName);
		boolean locked = fInfo.getIsWriteLocked();
		return locked;
	}

}
