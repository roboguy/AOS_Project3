package edu.utdallas.aos.application;

public class DaemonApplication implements Application {
	
	Integer numberOfRequests = 1;
	Integer readPercent = 100;
	
	@Override
	public void runApplication() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNumberOfRequests(Integer number) {
		this.numberOfRequests = number;
	}

	@Override
	public void setReadPercent(Integer percentReads) {
		this.readPercent = percentReads;
	}

}
