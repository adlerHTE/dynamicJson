package com.adler.dynamicJson.base;

/**
 * Interface to be implemented from Beans which need to be retrieved from a couchDB 
 * 
 * @author Davide Zambon
 *
 */
public interface CouchObject {

	public String get_id();
	public void set_id(String a);
}
