package org.musicbrainz.includes;

import java.util.List;
/**
 * <p>A specification on how much data to return with a recording.</p>
 * 
 */
public class RecordingIncludesWs2 extends IncludesWs2
{
	
           private boolean puids = false;
           private boolean isrcs = false;

           // Allowed only in Lookup, not in Browse.

           private boolean releases = false;
           
	/**
	 * Default constructor
	 */
	public RecordingIncludesWs2()
	{
                super();
	}
	
           @Override
	public List<String> createIncludeTags() 
	{
                List<String> includeTags =  super.createIncludeTags();

                // not that elegant but straight forward :)

                if (isReleases()) includeTags.add(RELEASES_INC);
               
                if (isPuids()) includeTags.add(PUIDS_INC);
                if (isIsrcs()) includeTags.add(ISRCS_INC);
                
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
     * @return the puids
     */
    public boolean isPuids() {
        return puids;
    }

    /**
     * @param puids the puids to set
     */
    public void setPuids(boolean puids) {
        this.puids = puids;
    }

    /**
     * @return the isrcs
     */
    public boolean isIsrcs() {
        return isrcs;
    }

    /**
     * @param isrcs the isrcs to set
     */
    public void setIsrcs(boolean isrcs) {
        this.isrcs = isrcs;
    }
    /**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setReleases(false);
        setPuids(false);
        setIsrcs(false);
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setReleases(true);
        setPuids(true);
        setIsrcs(true);
    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public RecordingIncludesWs2 clone(){
        
        RecordingIncludesWs2 target = new RecordingIncludesWs2();
        copyTo(target);
        target.setReleases(isReleases());
        target.setPuids(isPuids());
        target.setIsrcs(isIsrcs());
        return target;
    }
}
