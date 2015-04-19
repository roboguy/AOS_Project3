package edu.utdallas.aos.application;

import java.util.Scanner;

public class InteractiveApplication implements Application {

	static Scanner inputScanner;
	Integer numberOfRequests = 1;
	Integer readPercent = 100;
	
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
			System.out.println("Reading filename " + fileName);
			break;
		case "write":
			System.out.println("Writing filename " + fileName);
			break;
		case "list":
			System.out.println("List of files");
			break;
		case "exit":
			System.out.println("Exit");
			break;
		case "fail":
			System.out.println("Processing Node failure");
			break;
		default:
			System.out.println("Invalid command entered, please try again.");
			break;
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
		System.out.println("1.txt");
		System.out.println("2.txt");
		System.out.println("3.txt");
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

}
