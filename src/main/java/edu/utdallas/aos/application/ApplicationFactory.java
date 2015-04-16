package edu.utdallas.aos.application;

public class ApplicationFactory {

	
	public static Application getApplication(String type){
		
		if(type.equalsIgnoreCase("I")){
			return new InteractiveApplication();
		} else if(type.equalsIgnoreCase("D")){
			return new DaemonApplication();
		} else {
			return null;
		}
	}
}
