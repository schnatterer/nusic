package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * <p>A filter for the release group collection.</p>
 * 
 * <p><em>Note that these filter properties properties and <code>query</code>
 * may not be used together.</em></p>
 * 
 */
public class ReleaseGroupSearchFilterWs2 extends SearchFilterWs2 
{
	/**
	 * A String containing the release's title
	 */
	private String title;
	
	
	public ReleaseGroupSearchFilterWs2() {
		super();
	}
	
	@Override
	public Map<String, String> createParameters() 
	{
                Map<String, String> map = super.createParameters();
               
                if (this.title != null) 
                {
                    if (map.containsKey(QUERY)) {
                            throw new IllegalArgumentException("The title and query properties may not be used together!");
                    }

                    map.put(QUERY, this.title);
                } 
                else {
                    if (!map.containsKey(QUERY)) {
                            throw new IllegalArgumentException("This filter must specify a query or an artist name!");
                    }
                }
                
                return map;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}