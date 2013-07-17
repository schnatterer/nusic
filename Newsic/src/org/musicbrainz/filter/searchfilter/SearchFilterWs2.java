package org.musicbrainz.filter.searchfilter;

import java.util.HashMap;
import java.util.Map;
import org.musicbrainz.filter.FilterWs2;
import org.musicbrainz.DomainsWs2;


/**
 * This abstract class implemets a {@link Filter} and provides
 * some common properties and functions.
 * 
 */
public abstract class SearchFilterWs2 implements FilterWs2 
{
    public static final String LIMIT = DomainsWs2.LIMIT_FILTER;
    public static final String OFFSET = DomainsWs2.OFFSET_FILTER;
    public static final String QUERY = DomainsWs2.QUERY_FILTER;
            /**
	 * The maximum number of entities to return
	 */
	private Long limit = null;
	
	/**
	 * Start results at this zero-based offset 
	 */
	private Long offset = null;
	 
	/**
	 * The minimum score of the query results.
         * Only affect the execution of a next page fetch
         * if the last result of previous page was < minScore.
	 */
           private Long minScore=null;
	 
	/**
	 * A string containing a query in Lucene syntax
	 */
        
	private String query = null;
	
	/**
	 * Default constructor
	 */
	protected SearchFilterWs2()
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
	
          protected SearchFilterWs2(long limit, long offset, String query, long minScore)
	{
                this.limit = limit;
                this.offset = offset;
                this.query = query;
                this.minScore=minScore;
	}
	
	/* (non-Javadoc)
	 * @see org.musicbrainz.webservice.Filter#createParameters()
	 */
	public Map<String, String> createParameters()
	{
                Map<String, String> map = new HashMap<String, String>();

                if (this.limit != null) map.put(LIMIT, this.limit.toString());
                if (this.offset != null) map.put(OFFSET, this.offset.toString());
                if (this.query != null) map.put(QUERY, this.query);

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
	 * @return the offset
	 */
	public Long getMinScore() {
		return minScore;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setMinScore(Long minScore) {
		this.minScore = minScore;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
}
