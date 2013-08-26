package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * <p>A filter for the artist collection.</p>
 * 
 * <p>Note that the <code>name</code> and <code>query</code> properties
 * may not be used together.</p>
 * 
 */
public class ArtistSearchFilterWs2 extends SearchFilterWs2 {

    /**
    * The name of the artist
    */
    private String artistName = null;

    public ArtistSearchFilterWs2() {
       super();
    }

    @Override
    public Map<String, String> createParameters() 
    {
        Map<String, String> map = super.createParameters();
        if (this.artistName != null) 
        {
                if (map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("The name and query properties may not be used together!");
                }

                map.put(QUERY, this.artistName);
        } 
        else {
                if (!map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("This filter must specify a query or an artist name!");
                }
        }

        return map;
    }


    /**
    * @return the artistName
    */
    public String getArtistName() {
        return artistName;
    }

    /**
    * @param artistName the artistName to set
    */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

}