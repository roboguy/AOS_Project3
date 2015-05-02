package edu.utdallas.aos.core;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.utdallas.aos.message.AbortReadMessage;
import edu.utdallas.aos.message.DoneReadMessage;
import edu.utdallas.aos.message.Message;
import edu.utdallas.aos.message.ReadMessage;
import edu.utdallas.aos.p3.config.ContainsLock;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class ReadOperation extends Operation {

	@Override
	protected ContainsLock getLock(ReentrantReadWriteLock rwLock) {
		boolean localLockAcquired = rwLock.readLock().tryLock();
		ContainsLock container = new ContainsLock();
		container.setLockAcquired(localLockAcquired);
		container.setRwLock(rwLock);
		return container;
	}

	@Override
	protected FileInfo setFlags(FileInfo fInfo) {
		//System.out.println("LOCAL READ LOCK ACQUIRED");
		fInfo.setIsReadLocked(true);
		return fInfo;
	}

	@Override
	protected Message getMessage() {
		return new ReadMessage();
	}

	@Override
	protected FileInfo updateVersion(FileInfo fInfo) {
		return fInfo;
	}

	@Override
	protected Message getDoneMessage() {
		return new DoneReadMessage();
	}

	@Override
	protected ReentrantReadWriteLock unlockLock(ReentrantReadWriteLock rwLock) {
		rwLock.readLock().unlock();
		return rwLock;
	}

	@Override
	protected FileInfo resetFlags(FileInfo fInfo) {
		fInfo.setIsReadLocked(false);
		fInfo.resetQuorumCondition();
		return fInfo;
	}

	@Override
	protected String getOperation() {
		return "READ";
	}

	@Override
	protected Message getAbortMessage() {
		return new AbortReadMessage();
	}

}
