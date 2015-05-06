package edu.utdallas.aos.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.core.OpContainer;
import edu.utdallas.aos.core.ReplicationClient;

public class DaemonApplication implements Application {
	
	Integer numberOfRequests = 1;
	Integer readPercent = 100;

	private ReplicationClient replicationClient;
	private long requestDelay = (long) 500.0;
	private int abortedCount		= 0;
	@Override
	public void runApplication() {
		
		boolean doRead			= true;
		int numFiles			= Context.fsHandler.getReplicatedFiles().size();
		String[] fileNames		= Context.fsHandler.getReplicatedFiles().keySet().toArray(new String[numFiles]);
		
		Random rand				= new Random();
		int min 				= 1;
		int max					= 100;
		
		double[] fileProbablity	= populateFileProbablity(numFiles);
		
		for(int count = 1; count <= numberOfRequests; count++){
			makeRequests(fileNames, rand, min, max, fileProbablity, count);
		} // For each operation ENDS
		
		if(abortedCount > 0){
			for(int abortCount = 0; abortCount < abortedCount; abortCount++){
				makeRequests(fileNames, rand, min, max, fileProbablity, abortCount);
			}
		}
	}

	private void makeRequests(String[] fileNames, Random rand, int min,
			int max, double[] fileProbablity, int count) {
		boolean doRead;
		System.out.print("Request " + count + ": ");
		String content = "";
		int randomOp = rand.nextInt((max - min) + 1) + min;
		if(randomOp < readPercent){
			doRead = true;
		} else {
			doRead = false;
			content = RandomStringUtils.randomAlphanumeric(10);
			
		}
		String fileName = getFileName(rand, fileNames, fileProbablity);
		
		if(doRead && fileName != null){
			OpContainer container = null;
			try{
				System.out.print("Reading file " + fileName + " contains: ");
				container = replicationClient.readFile(fileName);
				String output = container.getContent();
				System.out.println(output);

			} catch (FileNotFoundException e) {
				System.out.println("File Not Found, Please try again");
			} catch (NoSuchElementException e) {
				System.out.println("EMPTY");
			}finally {
				if(container.isQuorumObtained()){
					replicationClient.readUnlockFile(fileName);
				} else {
					abortedCount++;
				}
			}
			
		} else {
			System.out.println("Writing to File " + fileName + " with content " + content);
			OpContainer container = null;
			try{
				container = replicationClient.writeFile(fileName, content);
			}catch(IOException e) {
				
			} finally {
				if(container.isQuorumObtained()){
					replicationClient.writeUnlockFile(fileName);
				} else {
					abortedCount++;
				}
			}
		}
		
		try {
			Thread.sleep(requestDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String getFileName(Random rand, String[] fileNames, double[] fileProbablity) {
		double value 	= rand.nextDouble();
		double sum		= 0.0;
		for(int count = 0; count < fileProbablity.length; count++){
			sum += fileProbablity[count];
			if(value <= sum){
				return fileNames[count];
			}
			
		}
		return null;
	}

	private double[] populateFileProbablity(int numberFiles) {
		double[] fileProbablity = new double[numberFiles];
		//Generate a Uniform probablity
		for(int count = 0; count < numberFiles; count++){
			fileProbablity[count] = (double) 1.0/numberFiles;
		}
		
		return fileProbablity;
	}

	@Override
	public void run() {
		this.runApplication();
	}

	@Override
	public void setNumberOfRequests(Integer number) {
		this.numberOfRequests = number;
	}

	@Override
	public void setReadPercent(Integer percentReads) {
		this.readPercent = percentReads;
	}

	@Override
	public void setReplicationClient(ReplicationClient client) {
		this.replicationClient = client;
		
	}

}
