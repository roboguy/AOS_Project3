package edu.utdallas.aos.message;

public class AbortReadMessage extends Message {

	@Override
	public String getType() {
		return this.type;
	}
	
	public AbortReadMessage(){
		this.type = "ABORTREAD";
	}

}
