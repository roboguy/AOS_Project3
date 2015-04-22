package edu.utdallas.aos.p3.config;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ContainsLock {
	ReentrantReadWriteLock rwLock = null;
	boolean lockAcquired = false;
	
	public ReentrantReadWriteLock getRwLock() {
		return rwLock;
	}
	public void setRwLock(ReentrantReadWriteLock rwLock) {
		this.rwLock = rwLock;
	}
	public boolean isLockAcquired() {
		return lockAcquired;
	}
	public void setLockAcquired(boolean lockAcquired) {
		this.lockAcquired = lockAcquired;
	}
	
	
	
}
