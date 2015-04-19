package edu.utdallas.aos.message;

public class ReadSuccessMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public ReadSuccessMessage(){
		this.type = "READSUCCESS";
	}
	

}
