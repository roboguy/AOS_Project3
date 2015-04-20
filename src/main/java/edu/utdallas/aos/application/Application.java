package edu.utdallas.aos.application;

import edu.utdallas.aos.core.ReplicationClient;

public interface Application extends Runnable {
	
	public void runApplication();
	public void setNumberOfRequests(Integer number);
	public void setReadPercent(Integer percentReads);
	public void setReplicationClient(ReplicationClient client);
}
