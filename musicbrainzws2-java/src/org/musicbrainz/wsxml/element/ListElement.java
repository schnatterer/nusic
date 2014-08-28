package org.musicbrainz.wsxml.element;

/**
 * A ListElement holds metadata about a list returned
 * from the web service. This can, for example, be a list
 * of releases or artists (see classes that extending this
 * class). Extending classes are responsible for the actual
 * <code>List</code>.
 * 
 * @author Patrick Ruhkopf
 */
public abstract class ListElement {
	
	/**
	 * The actual number of list items,
	 * see {@link #getCount()}
	 */
    protected Long count;
    
    /**
     * The offset of the list items
     * see {@link #getOffset()}
     */
    protected Long offset;
    
    /**
     * Returns the offset of the list.
     * 
     * This is used if the <code>List</code> is incomplete 
     * (ie. the web service only returned part of all list items).
     * Note that the offset value is zero-based, which means
     * item 0 is the first item.
     * 
	 * @return the offset or null
	 */
	public Long getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(Long offset) {
		this.offset = offset;
	}


	/**
	 * Returns the actual number of existing list items.
	 * 
     * This may or may not match with the number of elements that
	 * the <code>List</code> holds. If the count is higher than the
	 * <code>List</code>, it indicates that the list is incomplete.
	 *
	 * @return the count or null
	 */
	public Long getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}

}
