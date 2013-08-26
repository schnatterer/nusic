package org.musicbrainz.includes;

import java.util.List;

/**
 * <p>A specification on how much data to return with a collection.</p>
 * 
 */
public class CollectionIncludesWs2 extends IncludesWs2
{
    /* Subqueries
    * The inc= is parameter allows you to request more information to be 
    * included about the entity. Any of the entities directly linked to 
    * the entity can be included. 
    */

    private boolean releases = false;

    /* inc= arguments which affect subqueries
    * Some additional inc= parameters are supported to specify how 
    * much of the data about the linked entities should be included: 
    */
    private boolean discids = false;
    private boolean media = false;


    /**
    * Default constructor
    */
    public CollectionIncludesWs2()
    {
        super();
    }

    @Override
    public List<String> createIncludeTags() 
    {
        List<String> includeTags = super.createIncludeTags();

        // not that elegant but straight forward :)
        if (releases) includeTags.add(RELEASES_INC);

        if (discids) includeTags.add(DISCIDS_INC);
        if (media) includeTags.add(MEDIA_INC);

        return includeTags;
    }

    /**
    * @return the releases
    */
    public boolean isReleases() {
        return releases;
    }

    /**
    * @param aliases the aliases to set
    */
    public void setReleases(boolean releases) {
        this.releases = releases;
    }

    /**
    * @return the discids
    */
    public boolean isDiscids() {
        return discids;
    }
    /**
    * @param discids the discids to set
    */
    public void setDiscids(boolean discids) {
        this.discids = discids;
    }
    /**
    * @return the media
    */
    public boolean isMedia() {
        return media;
    }
    /**
    * @param media the media to set
    */
    public void setMedia(boolean media) {
        this.media = media;
    }
    
    /**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setReleases(false);
        setMedia(false);
        setDiscids(false);

    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
       setReleases(true);
       setMedia(true);
       setDiscids(true);

    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public CollectionIncludesWs2 clone(){
        
        CollectionIncludesWs2 target = new CollectionIncludesWs2();
        copyTo(target);
        target.setReleases(isReleases());
        target.setMedia(isMedia());
        target.setDiscids(isDiscids());

        return target;
    }
}
