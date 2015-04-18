package edu.utdallas.aos.message;

import info.siyer.aos.utils.IEncapsulateClock;

import com.google.gson.annotations.Expose;

/*
 * Abstract class that encapsulates an application Message.
 */

public abstract class Message implements IEncapsulateClock {

	@Expose
	protected String type;
	@Expose
	protected String clock;
	@Expose
	protected String fileName;
	@Expose
	protected Integer VN;
	@Expose
	protected Integer RU;
	@Expose
	protected String content;

	/**
	 * 
	 * @return The type
	 */
	public abstract String getType();

	/**
	 * 
	 * @param type
	 *            The type
	 */
	public void setType(String type){
		this.type = type;
	}

	/**
	 * 
	 * @return The clock
	 */
	public String getClock(){
		return this.clock;
	}

	/**
	 * 
	 * @param clock
	 *            The clock
	 */
	public void setClock(String clock){
		this.clock = clock;
	}

	/**
	 * 
	 * @return The fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @param fileName
	 *            The fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 
	 * @return The VN
	 */
	public Integer getVN() {
		return VN;
	}

	/**
	 * 
	 * @param VN
	 *            The VN
	 */
	public void setVN(Integer VN) {
		this.VN = VN;
	}

	/**
	 * 
	 * @return The RU
	 */
	public Integer getRU() {
		return RU;
	}

	/**
	 * 
	 * @param RU
	 *            The RU
	 */
	public void setRU(Integer RU) {
		this.RU = RU;
	}

	/**
	 * 
	 * @return The content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 
	 * @param content
	 *            The content
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
