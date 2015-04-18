package edu.utdallas.aos.message;

public class DoneMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public DoneMessage(){
		this.type = "DONE";
	}

}
