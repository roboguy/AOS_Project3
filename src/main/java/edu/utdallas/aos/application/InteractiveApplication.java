package edu.utdallas.aos.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import edu.utdallas.aos.core.Context;
import edu.utdallas.aos.core.OpContainer;
import edu.utdallas.aos.core.ReplicationClient;
import edu.utdallas.aos.p3.comm.Server;
import edu.utdallas.aos.p3.filesystem.FileInfo;

public class InteractiveApplication implements Application {

	static Scanner inputScanner;
	Integer numberOfRequests = 1;
	Integer readPercent = 100;
	private ReplicationClient replicationClient;
	
	@Override
	public void runApplication() {
		printWelcomeMessage();
		inputScanner = new Scanner(System.in);
		String command = "";
		while (!(command = commandPrompt(inputScanner)).contains("exit")) {
			processCommand(command);
		}
		inputScanner.close();
		System.out.println("Exited");
	}

	private void processCommand(String command) {
		String[] split 		= command.split("\\s");
		String operation 	= "";
		String fileName		= "";
		
		if(split.length == 1){
			operation = split[0];
		} else {
			operation = split[0];
			fileName = split[1];
		}
		
		switch (operation) {
		case "read":
			processRead(fileName);
			break;
		case "write":
			processWrite(fileName, inputScanner);
			break;
		case "list":
			System.out.println("List of files");
			processList();
			break;
		case "exit":
			System.out.println("Exit");
			break;
		case "fail":
			System.out.println("Processing Node failure, Node will be down for 60 seconds.");
			processFail();
			break;
		default:
			System.out.println("Invalid command entered, please try again.");
			break;
		}
	}

	private void processFail() {
		Thread fail	= new Thread(new Runnable() {
			
			@Override
			public void run() {
				Server.isFailed = true;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Server.isFailed = false;
				System.out.println("Server Recovered");
			}
		});
		fail.start();
		try {
			fail.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processList() {
		Set<String> files = Context.fsHandler.getReplicatedFiles().keySet();
		for(String name : files){
			System.out.println(name);
		}
	}

	private void processWrite(String fileName, Scanner inputScanner2) {
		System.out.println("> Enter Content");
		System.out.print("$ ");
		String newContent = inputScanner2.nextLine();
		OpContainer container = null;
		try {
			container = replicationClient.writeFile(fileName, newContent);
			
//			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("testClocks/" + fileName +".clock", true)));
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(Context.myInfo.getId() + "::");
//			sb.append(VectorClock.serializeClock(Context.clock) +"::");
//			out.print(sb.toString());
//			out.close();
		} catch (IOException e) {
			System.out.println("Unable to write content to file.");
		} finally {
			if(container.isQuorumObtained()){
				replicationClient.writeUnlockFile(fileName);
			}
			
		}
	}

	private void processRead(String fileName) {
		//System.out.println("Reading filename " + fileName);
		OpContainer container = null;
		try {
			container = replicationClient.readFile(fileName);
			System.out.println(container.getContent());
//			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("testClocks/" + fileName +".clock", true)));
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(Context.myInfo.getId() + "::");
//			sb.append(VectorClock.serializeClock(Context.clock) +"::");
//			out.print(sb.toString());
//			out.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found, Please try again");
		} catch (NoSuchElementException e) {
			System.out.println("EMPTY");
		} finally {
			if(container.isQuorumObtained()){
				replicationClient.readUnlockFile(fileName);
			}
			
		}
	}

	private String commandPrompt(Scanner inputScanner) {
		System.out.print("> ");
		String command = inputScanner.nextLine();
		return command.trim();
	}

	private void printWelcomeMessage() {
		System.out.println("Welcome to Awesome File System - AwesomeFS");
		System.out
				.println("Distributed & Replicated Fault Tolerant File System");
		System.out.println("Running in interactive mode");
		System.out.println("List of Files Available:");
		Iterator<Entry<String, FileInfo>> iter = Context.fsHandler.getReplicatedFiles().entrySet().iterator();
		while(iter.hasNext()){
			System.out.println(iter.next().getKey());
		}
		System.out.println("Available Operations: read / write");
		System.out.println("Example Command: read 1.txt");
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
