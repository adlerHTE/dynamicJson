package com.adler.dynamicJson.exception;

import java.io.IOException;

public class CouchDocumentConflictException extends IOException {


	public CouchDocumentConflictException(String body) {
		System.err.println("Body:"+body);
		// is this an unmarshable object?
	}

	private static final long serialVersionUID = 3074486429966335885L;



}
