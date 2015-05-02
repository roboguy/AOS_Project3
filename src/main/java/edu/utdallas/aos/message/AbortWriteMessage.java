package edu.utdallas.aos.message;

public class AbortWriteMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public AbortWriteMessage(){
		this.type = "ABORTWRITE";
	}

}
