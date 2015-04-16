package edu.utdallas.aos.core;

import edu.utdallas.aos.application.Application;
import edu.utdallas.aos.application.ApplicationFactory;

/*
 * Initializer class for the Dynamic Voting Protocol Application
 */

public class App {

	public static void main(String[] args) {
		
		if(args.length < 2){
			System.out.println("Please enter input params.");
			System.exit(0);
		}
		
		Arguments arguments = parseArgs(args);
		
		System.out.println("My Node ID: " + arguments.getMyID());
		
		Application application = ApplicationFactory.getApplication(arguments.getMode());

		Thread t1 = new Thread(application);
		t1.setName("Application");
		t1.start();
	}

	private static Arguments parseArgs(String[] args) {
		Arguments arguments = new Arguments();
		String mode = "";
		String myID = "";
		for(String arg : args){
			if(arg.contains("-") && (arg.contains("I") || arg.contains("D"))){
				mode = arg.replace("-", "");
			} else {
				myID = arg;
			}
		}
		arguments.setMode(mode);
		arguments.setMyID(myID);
		return arguments;
	}

}
