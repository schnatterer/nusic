package org.musicbrainz.filter.browsefilter;

import java.util.HashMap;
import java.util.Map;

import org.musicbrainz.filter.ReleaseStatusFilterWs2;
import org.musicbrainz.filter.ReleaseTypeFilterWs2;

public class ReleaseBrowseFilterWs2 extends BrowseFilterWs2 
{
    private ReleaseTypeFilterWs2 releaseTypeFilter =new ReleaseTypeFilterWs2();
    private ReleaseStatusFilterWs2 releaseStatusFilter =new ReleaseStatusFilterWs2();
    
    private Boolean variousArtists= false;
           
        
    public ReleaseBrowseFilterWs2() {
            super();
    }

    @Override
    public Map<String, String> createParameters() 
    {
            Map<String, String> map = super.createParameters();
            
            Map<String, String> localmap = new HashMap<String, String>();
            //if (isVariousArtists()) 
            //    map.put("track-artists", "");
            // set related entity to "track_artists" instead. 
            
            //Combine default filter's parameters with release filter's
            map.putAll(getReleaseTypeFilter().createParameters());
            map.putAll(getReleaseStatusFilter().createParameters());
            map.putAll(localmap);

            return map;
    }

    /**
     * @return the releaseTypeFilter
     */
    public ReleaseTypeFilterWs2 getReleaseTypeFilter() {
        return releaseTypeFilter;
    }

    /**
     * @param releaseTypeFilter the releaseTypeFilter to set
     */
    public void setReleaseTypeFilter(ReleaseTypeFilterWs2 releaseTypeFilter) {
        this.releaseTypeFilter = releaseTypeFilter;
    }

    /**
     * @return the releaseStatusFilter
     */
    public ReleaseStatusFilterWs2 getReleaseStatusFilter() {
        return releaseStatusFilter;
    }

    /**
     * @param releaseStatusFilter the releaseStatusFilter to set
     */
    public void setReleaseStatusFilter(ReleaseStatusFilterWs2 releaseStatusFilter) {
        this.releaseStatusFilter = releaseStatusFilter;
    }

    /**
     * @return the variousArtists
     */
    public Boolean isVariousArtists() {
        return variousArtists;
    }

    /**
     * @param variousArtists the variousArtists to set
     */
    public void setVariousArtists(Boolean variousArtists) {
        this.variousArtists = variousArtists;
    }

}