package com.adler.dynamicJson.base;

/**
 * Interface to be implemented from Beans which need to be updated into a couchDB
 * @author Davide Zambon
 *
 */
public interface CouchObjectWithRev  extends CouchObject {

	public String get_id();
	public String get_rev();
	public void set_id(String a);
	public void set_rev(String a);
}
