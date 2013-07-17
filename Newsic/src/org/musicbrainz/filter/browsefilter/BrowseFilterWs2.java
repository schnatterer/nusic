package org.musicbrainz.filter.browsefilter;

import java.util.HashMap;
import java.util.Map;
import org.musicbrainz.filter.FilterWs2;
import org.musicbrainz.DomainsWs2;


/**
 * This abstract class implemets a {@link Filter} and provides
 * some common properties and functions.

 */
public abstract class BrowseFilterWs2 implements FilterWs2 
{
    public static final String LIMIT = DomainsWs2.LIMIT_FILTER;
    public static final String OFFSET = DomainsWs2.OFFSET_FILTER;
            /**
	 * The maximum number of entities to return
	 */
	private Long limit = null;
	
	/**
	 * Start results at this zero-based offset 
	 */
	private Long offset = null;
	
	/**
	 * A string containing the related entity from whom start browsing
	 */
        
	private String relatedEntity = null;
        
           /**
	 *The Id of the related entity.
	 */
        
	private String relatedId = null;
	
	/**
	 * Default constructor
	 */
	protected BrowseFilterWs2()
	{
		
	}
	/**
	 * Full constructor
	 * 
	 * The <code>query</code> parameter may contain a query in 
	 * <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">
	 * Lucene syntax</a>.
	 * 
	 * @param limit The maximum number of artists to return
	 * @param offset Start results at this zero-based offset
	 * @param query A string containing a query in Lucene syntax
	  * @param minScore The minimum score of the query results.
         */
	
          protected BrowseFilterWs2(long limit, long offset, 
                                             String relatedEntity, String relatedId)
	{
                this.limit = limit;
                this.offset = offset;
                this.relatedEntity = relatedEntity;
                this.relatedId=relatedId;
	}
	
	/* (non-Javadoc)
	 * @see org.musicbrainz.webservice.Filter#createParameters()
	 */
	public Map<String, String> createParameters()
	{
                Map<String, String> map = new HashMap<String, String>();

                if (this.relatedEntity != null && this.relatedId != null )
                {
                    map.put(relatedEntity, this.relatedId);
                }
                
                if (this.limit != null) map.put(LIMIT, this.limit.toString());
                if (this.offset != null) map.put(OFFSET, this.offset.toString());

                return map;
	}
	
	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}
	/**
	 * @return the offset
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
	 * @return the relatedEntity
	 */
	public String getRelatedEntity() {
		return relatedEntity;
	}
	/**
	 * @param relatedEntity the relatedEntity to set
	 */
	public void setRelatedEntity(String relatedEntity) {
		this.relatedEntity = relatedEntity;
	}
	/**
	 * @return the relatedId
	 */
	public String getRelatedId() {
		return relatedId;
	}
	/**
	 * @param relatedId the relatedId to set
	 */
	public void setRelatedId(String relatedId) {
		this.relatedId = relatedId;
	}
}
