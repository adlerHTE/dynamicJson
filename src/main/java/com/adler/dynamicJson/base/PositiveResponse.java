package com.adler.dynamicJson.base;

public class PositiveResponse {

	private String ok;
	
	private String id;
	
	private String rev;

	public String getOk() {
		return ok;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	@Override
	public String toString() {
		return "PositiveResponse [ok=" + ok + ", id=" + id + ", rev=" + rev
				+ "]";
	}
	
	
}
