package org.musicbrainz.includes;

import java.util.List;

/**
 * A specification on how much data to return with a release.
 */
public class ReleaseIncludesWs2 extends IncludesWs2
    {

   private boolean label = false;
    
    // The following Includes are allowed ONLY for LookUps
    // and NOT for Browse, but they are currently working.
    
    private boolean discids = false;
    private boolean media = false;
    
    // The following Includes are allowed ONLY for LookUps
    // and NOT for Browse.

    private boolean releaseGroups = false;
    private boolean recordings = false;
    
  

    /**
     * Default constructor
     */
    public ReleaseIncludesWs2()
    {
        super();
    }


    /* (non-Javadoc)
     * @see org.musicbrainz.webservice.Includes#createIncludeTags()
     */
    @Override
    public List<String> createIncludeTags() 
    {
            List<String> includeTags = super.createIncludeTags();


            // not that elegant but straight forward :)
             
             if (isLabel()) includeTags.add(LABELS_INC);
                          
             if (isDiscids()) includeTags.add(DISCIDS_INC);
             if (isMedia()) includeTags.add(MEDIA_INC);
             
             if (isReleaseGroups()) includeTags.add(RELEASEGROUPS_INC);
             if (isRecordings()) includeTags.add(RECORDINGS_INC);

            return includeTags;
    }

    /**
     * @return the artistCredit
     */
    /**
     * @return the label
     */
    public boolean isLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(boolean label) {
        this.label = label;
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
     * @return the releaseGroups
     */
    public boolean isReleaseGroups() {
        return releaseGroups;
    }

    /**
     * @param releaseGroups the releaseGroups to set
     */
    public void setReleaseGroups(boolean releaseGroups) {
        this.releaseGroups = releaseGroups;
    }

    /**
     * @return the recordings
     */
    public boolean isRecordings() {
        return recordings;
    }

    /**
     * @param recordings the recordings to set
     */
    public void setRecordings(boolean recordings) {
        this.recordings = recordings;
    }
    /**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setLabel(false);
        setReleaseGroups(false);
        setRecordings(false);
        setMedia(false);
        setDiscids(false);
        
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setLabel(true);
        setReleaseGroups(true);
        setRecordings(true);
        setMedia(true);
        setDiscids(true);
    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public ReleaseIncludesWs2 clone(){
        
        ReleaseIncludesWs2 target = new ReleaseIncludesWs2();
        copyTo(target);
        target.setLabel(isLabel());
        target.setReleaseGroups(isReleaseGroups());
        target.setRecordings(isRecordings());
        target.setMedia(isMedia());
        target.setDiscids(isDiscids());
        return target;
    }
}
