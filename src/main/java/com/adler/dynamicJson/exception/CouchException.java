package com.adler.dynamicJson.exception;

import java.io.IOException;

import com.adler.dynamicJson.base.NegativeResponse;


public class CouchException extends IOException {

	
	private static final long serialVersionUID = 8919868036683017014L;

	NegativeResponse nr;
	
	public CouchException(NegativeResponse e) {
		this.nr=e;
	}
	
	

	public NegativeResponse getNegativeResponse() {
		return nr;
	}



	@Override
	public String toString() {
		return "CouchException [nr=" + nr + "]";
	}

	
}
