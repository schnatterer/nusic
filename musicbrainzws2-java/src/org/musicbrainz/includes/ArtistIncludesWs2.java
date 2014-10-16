package org.musicbrainz.includes;

import java.util.List;

/**
 * <p>A specification on how much data to return with an artist.</p>
 *
 */
public class ArtistIncludesWs2 extends IncludesWs2 
{

    // Misc inc= arguments 
      private boolean aliases = false;

     /* Subqueries
    * The inc= is parameter allows you to request more information to be 
    * included about the entity. Any of the entities directly linked to 
    * the entity can be included. 
   */
       private boolean recordings = false;
       private boolean releases = false;
       private boolean releaseGroups = false;
       private boolean works = false;
       //private boolean labels = false; Is in mmd but not allowed in ws

       /* inc= arguments which affect subqueries
     * Some additional inc= parameters are supported to specify how 
     * much of the data about the linked entities should be included: 
    */
      private boolean discids = false;
      private boolean media = false;
      private boolean puids = false;
      private boolean isrcs = false;
      private boolean variousArtists = false;
          /*include only those releases where the artist appears on one 
       * of the tracks, but not in the artist credit for the release
       * itself (this is only valid on a /ws/2/artist?inc=releases 
       * request)

    /**
     * Default constructor
     */
    public ArtistIncludesWs2()
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
            if (aliases) includeTags.add(ALIASES_INC);

            if (recordings) includeTags.add(RECORDINGS_INC);
            if (releases) includeTags.add(RELEASES_INC);
            if (releaseGroups) includeTags.add(RELEASEGROUPS_INC);
            if (works) includeTags.add(WORKS_INC);

            if (discids) includeTags.add(DISCIDS_INC);
            if (media) includeTags.add(MEDIA_INC);
            if (puids) includeTags.add(PUIDS_INC);
            if (isrcs) includeTags.add(ISRCS_INC);
            if (variousArtists) includeTags.add(VARIOUSARTISTS_INC);

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
     * @return the works
    */
    public boolean isWorks() {
            return works;
    }
    /**
     * @param works the works to set
     */
    public void setWorks(boolean works) {
            this.works = works;
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
     * @return the variousArtists
     */
    public boolean isVariousArtists() {
            return variousArtists;
    }
    /**
     * @param variousArtists the variousArtists to set
     */
    public void setVariousArtists(boolean variousArtists) {
            this.variousArtists = variousArtists;
    }
    /**
   * set all the parameters to false.
   */
    @Override
    public void excludeAll(){
        
        super.excludeAll();
        setAliases(false);
        setReleaseGroups(false);
        setReleases(false);
        setVariousArtists(false);
        setRecordings(false);
        setWorks(false);
        setMedia(false);
        setDiscids(false);
        setPuids(false);
        setIsrcs(false);
    }
    /**
   * set all the parameters to true.
   */
    @Override
    public void includeAll(){
        
       super.includeAll();
        setAliases(true);
        setReleaseGroups(true);
        setReleases(true);
        setVariousArtists(true);
        setRecordings(true);
        setWorks(true);
        setMedia(true);
        setDiscids(true);
        setPuids(true);
        setIsrcs(true);
    }
    /**
   *  clone the current status of IncludesWs2 to a new one.
   */
    @Override
    public ArtistIncludesWs2 clone(){
        
        ArtistIncludesWs2 target = new ArtistIncludesWs2();
        copyTo(target);
        target.setAliases(isAliases());
        target.setReleaseGroups(isReleaseGroups());
        target.setReleases(isReleases());
        target.setVariousArtists(isVariousArtists());
        target.setRecordings(isRecordings());
        target.setWorks(isWorks());
        target.setMedia(isMedia());
        target.setDiscids(isDiscids());
        target.setPuids(isPuids());
        target.setIsrcs(isIsrcs());
        return target;
    }
}
