package com.adler.dynamicJson.base;

public class NegativeResponse {

	private String error;
	private String reason;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Override
	public String toString() {
		return "NegativeResponse [error=" + error + ", reason=" + reason + "]";
	}
	
	
}
