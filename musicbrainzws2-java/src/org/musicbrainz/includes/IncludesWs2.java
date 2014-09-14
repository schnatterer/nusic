package org.musicbrainz.includes;

import java.util.ArrayList;
import java.util.List;
import org.musicbrainz.DomainsWs2;


/**
 * <p>A specification on how much data to return with an entity.</p>
 * 
 */
public class IncludesWs2 extends DomainsWs2 {

     //public static final String ANNOTATION_INC ="annotation";
      // Misc inc= arguments 

      //private boolean aliases = false;

     /* Subqueries
    * The inc= is parameter allows you to request more information to be 
    * included about the entity. Any of the entities directly linked to 
    * the entity can be included. 
   */
       //private boolean labels = false; 
       //private boolean artists = false;
       //private boolean releaseGroups = false;
       //private boolean releases = false;
       //private boolean works = false;
       //private boolean recordings = false;

       /* inc= arguments which affect subqueries
     * Some additional inc= parameters are supported to specify how 
     * much of the data about the linked entities should be included: 
    */
      //private boolean discids = false;
      //private boolean media = false;
      //private boolean puids = false;
      //private boolean isrcs = false;
      //private boolean artistCredits = false;
      //private boolean variousArtists = false;

          /*include only those releases where the artist appears on one 
       * of the tracks, but not in the artist credit for the release
       * itself (this is only valid on a /ws/2/artist?inc=releases 
       * request)

   /*Relationships
     * inc= arguments to include relationships work exactly like 
     * they do in /ws/1: 
    */
    private boolean annotation = false;
    private boolean tags = false;
    private boolean ratings = false;
    private boolean userTags = false;
    private boolean userRatings = false;
    
    private boolean artistRelations = false;
    private boolean labelRelations = false;
    private boolean recordingRelations = false;
    private boolean releaseRelations = false;
    private boolean releaseGroupRelations = false;
    private boolean urlRelations = false;
    private boolean workRelations = false;
    
    private boolean recordingLevelRelations = false;
    private boolean workLevelRelations = false;
    
    private boolean artistCredits = false;

    /**
     * Default constructor
     */
    public IncludesWs2()
    {

    }

    public List<String> createIncludeTags() 
    {
            List<String> includeTags = new ArrayList<String>();

            // not that elegant but straight forward :)

            if (artistRelations) includeTags.add(ARTISTRELS_INC);
            if (labelRelations) includeTags.add(LABELRELS_INC);
            if (recordingRelations)
            {
                includeTags.add(RECORDINGRELS_INC);
                if (isRecordingLevelRelations())includeTags.add(RECORDINGLEVELRELS_INC);
            }
            if (releaseRelations) includeTags.add(RELEASERELS_INC);
            if (releaseGroupRelations) includeTags.add(RELEASEGROUPRELS_INC);
            if (urlRelations) includeTags.add(URLRELS_INC);
            if (workRelations) 
            {
                includeTags.add(WORKRELS_INC);
                if (isWorkLevelRelations())includeTags.add(WORKLEVELRELS_INC);
            }
            if (artistCredits) includeTags.add(ARTISTCREDITS_INC);
            
            if (isTags()) includeTags.add(TAGS_INC);
            if (isRatings()) includeTags.add(RATINGS_INC);
            if (isUserTags()) includeTags.add(USERTAGS_INC);
            if (isUserRatings()) includeTags.add(USERRATINGS_INC);
            
            //if (annotation) includeTags.add(ANNOTATION_INC) // To be handled with a search.
             
            return includeTags;
    }


    /**
     * @return the artistRelations
     */
    public boolean isArtistRelations() {
            return artistRelations;
    }

    /**
     * @param artistRelations the artistRelations to set
     */
    public void setArtistRelations(boolean artistRelations) {
            this.artistRelations = artistRelations;
    }
    /**
     * @return the labelRelations
     */
    public boolean isLabelRelations() {
            return labelRelations;
    }

    /**
     * @param labelRelations the labelRelations to set
     */
    public void setLabelRelations(boolean labelRelations) {
            this.labelRelations = labelRelations;
    }
       /**
     * @return the recordingRelations
     */
    public boolean isRecordingRelations() {
            return recordingRelations;
    }
    /**
     * @param recordingRelations the recordingRelations to set
     */
    public void setRecordingRelations(boolean recordingRelations) {
            this.recordingRelations = recordingRelations;
    }
    /**
     * @return the releaseRelations
     */
    public boolean isReleaseRelations() {
            return releaseRelations;
    }
    /**
     * @param releaseRelations the releaseRelations to set
     */
    public void setReleaseRelations(boolean releaseRelations) {
            this.releaseRelations = releaseRelations;
    }
            /**
     * @return the releaseGroupRelations
     */
    public boolean isReleaseGroupRelations() {
            return releaseGroupRelations;
    }
    /**
     * @param releaseGroupRelations the releaseGroupRelations to set
     */
    public void setReleaseGroupRelations(boolean releaseGroupRelations) {
            this.releaseGroupRelations = releaseGroupRelations;
    }
    /**
     * @return the urlRelations
     */
    public boolean isUrlRelations() {
            return urlRelations;
    }
    /**
     * @param urlRelations the urlRelations to set
     */
    public void setUrlRelations(boolean urlRelations) {
            this.urlRelations = urlRelations;
    }
       /**
     * @return the workRelations
     */
    public boolean isWorkRelations() {
            return workRelations;
    }
    /**
     * @param workRelations the workRelations to set
     */
    public void setWorkRelations(boolean workRelations) {
            this.workRelations = workRelations;
    }

    /**
     * @return the recordingLevelRelations
     */
    public boolean isRecordingLevelRelations() {
        return recordingLevelRelations;
    }

    /**
     * @param recordingLevelRelations the recordingLevelRelations to set
     */
    public void setRecordingLevelRelations(boolean recordingLevelRelations) {
        this.recordingLevelRelations = recordingLevelRelations;
    }

    /**
     * @return the workLevelRelations
     */
    public boolean isWorkLevelRelations() {
        return workLevelRelations;
    }

    /**
     * @param workLevelRelations the workLevelRelations to set
     */
    public void setWorkLevelRelations(boolean workLevelRelations) {
        this.workLevelRelations = workLevelRelations;
    }
    /**
     * @return the artistCredits
     */
    public boolean isArtistCredits() {
        return artistCredits;
    }

    /**
     * @param artistCredits the artistCredits to set
     */
    public void setArtistCredits(boolean artistCredits) {
        this.artistCredits = artistCredits;
    }
    /**
     * @return the annotation
     */
    public boolean isAnnotation() {
        return annotation;
    }
    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }
    
    /**
     * @return the tags
     */
    public boolean isTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(boolean tags) {
        this.tags = tags;
    }

    /**
     * @return the ratings
     */
    public boolean isRatings() {
        return ratings;
    }

    /**
     * @param ratings the ratings to set
     */
    public void setRatings(boolean ratings) {
        this.ratings = ratings;
    }
    /**
     * @return the userTags
     */
    public boolean isUserTags() {
        return userTags;
    }

    /**
     * @param userTags the userTags to set
     */
    public void setUserTags(boolean userTags) {
        this.userTags = userTags;
    }

    /**
     * @return the userRatings
     */
    public boolean isUserRatings() {
        return userRatings;
    }

    /**
     * @param userRatings the userRatings to set
     */
    public void setUserRatings(boolean userRatings) {
        this.userRatings = userRatings;
    }
    /**
   * set all the parameters to false.
   */
    protected void excludeAll(){
        
        setArtistRelations(false);
        setLabelRelations(false);
        setReleaseGroupRelations(false);
        setReleaseRelations(false);
        setRecordingRelations(false);
        setWorkRelations(false);
        
        setUrlRelations(false);
        
        setRecordingLevelRelations(false);
        setWorkLevelRelations(false);
        
        setArtistCredits(false);
        setAnnotation(false);
        setTags(false);
        setRatings(false);
        setUserTags(false);
        setUserRatings(false);
        
    }
    /**
   * set all the parameters to true.
   */
    public void includeAll(){
        
        setArtistRelations(true);
        setLabelRelations(true);
        setReleaseGroupRelations(true);
        setReleaseRelations(true);
        setRecordingRelations(true);
        setWorkRelations(true);
        
        setUrlRelations(true);
        
        setRecordingLevelRelations(true);
        setWorkLevelRelations(true);
        
        setArtistCredits(true);
        setAnnotation(true);
        setTags(true);
        setRatings(true);
        setUserTags(true);
        setUserRatings(true);
    }
    /**
   *  @param target the target IncludesWs2
   */
    protected IncludesWs2 copyTo(IncludesWs2 target){
        
        target.setArtistRelations(isArtistRelations());
        target.setLabelRelations(isLabelRelations());
        target.setReleaseGroupRelations(isReleaseGroupRelations());
        target.setReleaseRelations(isReleaseRelations());
        target.setRecordingRelations(isRecordingRelations());
        target.setWorkRelations(isWorkRelations());
        
        target.setUrlRelations(isUrlRelations());
        
        target.setRecordingLevelRelations(isRecordingLevelRelations());
        target.setWorkLevelRelations(isWorkLevelRelations());
        
        target.setArtistCredits(isArtistCredits());
        target.setAnnotation(isAnnotation());
        target.setTags(isTags());
        target.setRatings(isRatings());
        target.setUserTags(isUserTags());
        target.setUserRatings(isUserRatings());
        
        return target;
    }
}
