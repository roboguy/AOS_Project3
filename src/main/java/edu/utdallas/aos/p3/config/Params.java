package edu.utdallas.aos.p3.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

	@SerializedName("key_distribution")
	@Expose
	private String keyDistribution;
	@SerializedName("necessary_arg1")
	@Expose
	private String necessaryArg1;
	@SerializedName("necessary_arg2")
	@Expose
	private String necessaryArg2;
	@SerializedName("optional_arg1")
	@Expose
	private String optionalArg1;

	/**
	 * 
	 * @return The keyDistribution
	 */
	public String getKeyDistribution() {
		return keyDistribution;
	}

	/**
	 * 
	 * @param keyDistribution
	 *            The key_distribution
	 */
	public void setKeyDistribution(String keyDistribution) {
		this.keyDistribution = keyDistribution;
	}

	/**
	 * 
	 * @return The necessaryArg1
	 */
	public String getNecessaryArg1() {
		return necessaryArg1;
	}

	/**
	 * 
	 * @param necessaryArg1
	 *            The necessary_arg1
	 */
	public void setNecessaryArg1(String necessaryArg1) {
		this.necessaryArg1 = necessaryArg1;
	}

	/**
	 * 
	 * @return The necessaryArg2
	 */
	public String getNecessaryArg2() {
		return necessaryArg2;
	}

	/**
	 * 
	 * @param necessaryArg2
	 *            The necessary_arg2
	 */
	public void setNecessaryArg2(String necessaryArg2) {
		this.necessaryArg2 = necessaryArg2;
	}

	/**
	 * 
	 * @return The optionalArg1
	 */
	public String getOptionalArg1() {
		return optionalArg1;
	}

	/**
	 * 
	 * @param optionalArg1
	 *            The optional_arg1
	 */
	public void setOptionalArg1(String optionalArg1) {
		this.optionalArg1 = optionalArg1;
	}

}