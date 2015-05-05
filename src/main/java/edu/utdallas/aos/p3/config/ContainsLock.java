package edu.utdallas.aos.p3.config;

import java.util.concurrent.Semaphore;

public class ContainsLock {
	Semaphore fileSemaphore = null;
	boolean lockAcquired = false;
	
	public Semaphore getRwLock() {
		return fileSemaphore;
	}
	public void setRwLock(Semaphore rwLock) {
		this.fileSemaphore = rwLock;
	}
	public boolean isLockAcquired() {
		return lockAcquired;
	}
	public void setLockAcquired(boolean lockAcquired) {
		this.lockAcquired = lockAcquired;
	}
	
	
	
}
