package edu.utdallas.aos.message;

public class WriteMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}	
	
	public WriteMessage(){
		this.type = "WRITE";
	}

}
