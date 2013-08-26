package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * <p>A filter for the release collection.</p>
 * 
 * <p><em>Note that these filter properties properties and <code>query</code>
 * may not be used together.</em></p>

 */
public class ReleaseSearchFilterWs2 extends SearchFilterWs2 
{
	/**
	 * A String containing the release's title
	 */
	private String title;

	/* (non-Javadoc)
	 * @see org.musicbrainz.webservice.Filter()
	 */
	public ReleaseSearchFilterWs2() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.musicbrainz.webservice.Filter#createParameters()
	 */
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
                            throw new IllegalArgumentException("This filter must specify a query or a release title!");
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