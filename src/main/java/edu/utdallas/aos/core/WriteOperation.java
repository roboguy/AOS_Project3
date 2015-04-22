package edu.utdallas.aos.core;

import java.util.concurrent.locks.ReentrantReadWriteLock;

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
	protected ContainsLock getLock(ReentrantReadWriteLock rwLock) {
		boolean localLockAcquired = rwLock.writeLock().tryLock();
		ContainsLock container = new ContainsLock();
		container.setLockAcquired(localLockAcquired);
		container.setRwLock(rwLock);
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
		fInfo.setIsWriteLocked(true);
		fInfo.resetQuorumCondition();
		return fInfo;
	}

	@Override
	protected ReentrantReadWriteLock unlockLock(ReentrantReadWriteLock rwLock) {
		rwLock.writeLock().unlock();
		return rwLock;
	}

	@Override
	protected Message getDoneMessage() {
		return new DoneWriteMessage();
	}

	@Override
	protected String getOperation() {
		return "WRITE";
	}

}
