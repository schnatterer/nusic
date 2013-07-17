package org.musicbrainz.includes;

import java.util.List;


/**
 * A specification on how much data to return with a release group.
 */
public class ReleaseGroupIncludesWs2 extends IncludesWs2
    {

    // The following Includes are allowed ONLY for LookUps
    // and NOT for Browse.
    
    private boolean artists = false;
    private boolean releases = false;
   

    /**
     * Default constructor
     */
    public ReleaseGroupIncludesWs2()
    {
           super();
    }

    @Override
    public List<String> createIncludeTags() 
    {
            List<String> includeTags = super.createIncludeTags();


            // not that elegant but straight forward :)
             if (isReleases()) includeTags.add(RELEASES_INC);
          
            return includeTags;
    }

    /**
     * @return the releases
     */
    public boolean isReleases() {
        return releases;
    }

    /**
     * @param releases the releases to set
     */
    public void setReleases(boolean releases) {
        this.releases = releases;
    }
/**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setReleases(false);
        
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setReleases(true);
       
    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public ReleaseGroupIncludesWs2 clone(){
        
        ReleaseGroupIncludesWs2 target = new ReleaseGroupIncludesWs2();
        copyTo(target);
        target.setReleases(isReleases());
       
        return target;
    }
}
