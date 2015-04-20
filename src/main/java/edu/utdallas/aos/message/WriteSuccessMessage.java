package edu.utdallas.aos.message;


public class WriteSuccessMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	
	public WriteSuccessMessage() {
		this.type = "WRITESUCCESS";
	}
}
