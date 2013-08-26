package org.musicbrainz.includes;

import java.util.List;

/**
 * <p>A specification on how much data to return with a label.</p>
 * 
 */
public class LabelIncludesWs2 extends IncludesWs2
{
	
    // Misc inc= arguments 

    private boolean aliases = false;

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
    private boolean puids = false;
    private boolean isrcs = false;

    /**
    * Default constructor
    */
    public LabelIncludesWs2()
    {
        super();
    }

    @Override
    public List<String> createIncludeTags() 
    {
        List<String> includeTags = super.createIncludeTags();

        // not that elegant but straight forward :)
        if (aliases) includeTags.add(ALIASES_INC);

        if (releases) includeTags.add(RELEASES_INC);

        if (discids) includeTags.add(DISCIDS_INC);
        if (media) includeTags.add(MEDIA_INC);
        if (puids) includeTags.add(PUIDS_INC);
        if (isrcs) includeTags.add(ISRCS_INC);

        return includeTags;
    }

    /**
    * @return the aliases
    */
    public boolean isAliases() {
        return aliases;
    }

    /**
    * @param aliases the aliases to set
    */
    public void setAliases(boolean aliases) {
        this.aliases = aliases;
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
        setAliases(false);
        setReleases(false);
        setMedia(false);
        setDiscids(false);
        //setPuids(false);
        //setIsrcs(false);
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setAliases(true);
        setReleases(true);
        setMedia(true);
        setDiscids(true);
        //setPuids(true);
        //setIsrcs(true);
    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public LabelIncludesWs2 clone(){
        
        LabelIncludesWs2 target = new LabelIncludesWs2();
        copyTo(target);
        target.setAliases(isAliases());
        target.setReleases(isReleases());
        target.setMedia(isMedia());
        target.setDiscids(isDiscids());
        //target.setPuids(isPuids());
        //target.setIsrcs(isIsrcs());
        return target;
    }
    /*
    public boolean isPuids() {
        return puids;
    }
    public void setPuids(boolean puids) {
        this.puids = puids;
    }
    public boolean isIsrcs() {
        return isrcs;
    }
    public void setIsrcs(boolean isrcs) {
        this.isrcs = isrcs;
    {
*/

}
