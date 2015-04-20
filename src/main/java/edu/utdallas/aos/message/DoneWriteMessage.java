package edu.utdallas.aos.message;

public class DoneWriteMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	public DoneWriteMessage(){
		this.type = "DONEWRITE";
	}
}
