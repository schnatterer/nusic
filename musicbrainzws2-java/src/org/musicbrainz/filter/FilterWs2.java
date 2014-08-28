package org.musicbrainz.filter;

import java.util.Map;

/**
 * <p>A filter for collections.</p>
 * 
 * <p>This is the interface all filters have to implement. Filter classes
 * are initialized with a set of criteria and are then applied to
 * collections of items. The criteria are usually strings or integer
 * values, depending on the filter.</p>
 * 
 * @author Patrick Ruhkopf
 */
public interface FilterWs2 {
	
	/**
	 * Create a map of query parameters.
	 * 
	 * This method creates a list of (<code>parameter</code>, <code>value</code>)
	 * tuples, based on the contents of the implementing subclass.
	 * <code>parameter</code> is a string containing a parameter name
	 * and <code>value</code> an arbitrary string. No escaping of those
	 * strings is required. 
	 * 
	 * @return A map containing (key, value) pairs.
	 */
	public Map<String, String> createParameters();

}

