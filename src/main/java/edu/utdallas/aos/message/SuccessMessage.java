package edu.utdallas.aos.message;

public class SuccessMessage extends Message {


	@Override
	public String getType() {
		return this.type;
	}
	
	public SuccessMessage(){
		this.type = "SUCCESS";
	}

}
