package edu.utdallas.aos.message;

public class DoneReadMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public DoneReadMessage(){
		this.type = "DONEREAD";
	}
}
