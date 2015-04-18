package edu.utdallas.aos.p3.config;

import com.google.gson.annotations.Expose;

public class Node {

	@Expose
	private Integer id;
	@Expose
	private String host;
	@Expose
	private String port;

	/**
	 * 
	 * @return The id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @return The host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 
	 * @param host
	 *            The host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * 
	 * @return The port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * 
	 * @param port
	 *            The port
	 */
	public void setPort(String port) {
		this.port = port;
	}

}