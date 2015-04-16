package edu.utdallas.aos.message;

import info.siyer.aos.utils.IEncapsulateClock;

import java.io.Serializable;

/*
 * Abstract class that encapsulates an application Message.
 */

public abstract class Message implements IEncapsulateClock, Serializable {

	/**
	 * 
	 */
	static final long serialVersionUID = 7381018483490748252L;

	public abstract String getClock();

	public abstract void setClock(String clock);

}
