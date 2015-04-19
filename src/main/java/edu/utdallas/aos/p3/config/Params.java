package edu.utdallas.aos.p3.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

	@SerializedName("exp_Backoff_min")
	@Expose
	private Integer expBackoffMin;
	@SerializedName("exp_Backoff_max")
	@Expose
	private Integer expBackoffMax;
	@SerializedName("read_operation_percent")
	@Expose
	private Integer readOperationPercent;

	/**
	 * 
	 * @return The expBackoffMin
	 */
	public Integer getExpBackoffMin() {
		return expBackoffMin;
	}

	/**
	 * 
	 * @param expBackoffMin
	 *            The exp_Backoff_min
	 */
	public void setExpBackoffMin(Integer expBackoffMin) {
		this.expBackoffMin = expBackoffMin;
	}

	/**
	 * 
	 * @return The expBackoffMax
	 */
	public Integer getExpBackoffMax() {
		return expBackoffMax;
	}

	/**
	 * 
	 * @param expBackoffMax
	 *            The exp_Backoff_max
	 */
	public void setExpBackoffMax(Integer expBackoffMax) {
		this.expBackoffMax = expBackoffMax;
	}

	/**
	 * 
	 * @return The readOperationPercent
	 */
	public Integer getReadOperationPercent() {
		return readOperationPercent;
	}

	/**
	 * 
	 * @param readOperationPercent
	 *            The read_operation_percent
	 */
	public void setReadOperationPercent(Integer readOperationPercent) {
		this.readOperationPercent = readOperationPercent;
	}

}