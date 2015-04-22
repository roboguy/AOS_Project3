package edu.utdallas.aos.application.test;

import org.junit.Test;

import edu.utdallas.aos.application.Application;
import edu.utdallas.aos.application.ApplicationFactory;
import edu.utdallas.aos.core.ReplicationClient;

public class DaemonApplicationTest {
	
	@Test
	public void fileTest(){
		Application daemon = ApplicationFactory.getApplication("D");
		daemon.setNumberOfRequests(100);
		daemon.setReadPercent(50);
		daemon.setReplicationClient(new ReplicationClient());
		Thread appThread = new Thread(daemon);
		appThread.start();
		try {
			appThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
