package edu.utdallas.aos.message;

/*
 * 
 */

public class ReadMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public ReadMessage(){
		this.type = "READ";
	}
	
}
