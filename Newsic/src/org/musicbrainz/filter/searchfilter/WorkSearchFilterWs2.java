package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * <p>A filter for the work collection.</p>
 * 
 * <p>Note that the <code>name</code> and <code>query</code> properties
 * may not be used together.</p>
 */
public class WorkSearchFilterWs2 extends SearchFilterWs2 {

    /**
    * The name of the artist
    */
    private String workTitle = null;

    public WorkSearchFilterWs2() {
       super();
    }

    @Override
    public Map<String, String> createParameters() 
    {
        Map<String, String> map = super.createParameters();
        if (this.workTitle != null) 
        {
                if (map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("The title and query properties may not be used together!");
                }

                map.put(QUERY, this.workTitle);
        } 
        else {
                if (!map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("This filter must specify a query or a work title!");
                }
        }

        return map;
    }


    /**
    * @return the workTitle
    */
    public String getWorkTitle() {
        return workTitle;
    }

    /**
    * @param workTitle the workTitle to set
    */
    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

}