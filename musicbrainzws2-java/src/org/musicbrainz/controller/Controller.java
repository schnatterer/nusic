/*
 * An abstract controller.
 */
package org.musicbrainz.controller;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.searchfilter.SearchFilterWs2;
import org.musicbrainz.includes.IncludesWs2;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.SearchWs2;
import org.musicbrainz.webservice.DefaultWebServiceWs2;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.musicbrainz.DomainsWs2;
import org.musicbrainz.model.RatingsWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.query.submission.UserRatingSubmissionWs2;
import org.musicbrainz.query.submission.UserTagSubmissionWs2;
import org.musicbrainz.wsxml.element.Metadata;


public abstract class Controller extends DomainsWs2{
    
    
    private WebService queryWs;
    private WebService annotationWs;
    private int browseLimit = 100;
    
    private SearchFilterWs2 searchFilter;
    private SearchWs2 search;
    
    private IncludesWs2 includes;
    private IncludesWs2 Included;
    private LookUpWs2 lookUp;
    private EntityWs2 entity;
    
    private EntityWs2 incoming;
    
    private String applicationContact;
    private String applicationVersion;
    private String applicationName;

    
    /**
     * Creates a Controller with a default user agent string.
     */
    public Controller(){
        this(null, null, null);
    }
    
    /**
     * Creates a Controller with a custom user agent string.
     * 
     * @param applicationName
     *            custom application name used in user agent string. If
     *            <code>null</code>, the default user agent string is used
     * @param applicationVersion
     *            custom application version used in user agent string
     * @param applicationContact
     *            contact URL or author email used in user agent string
     */
    public Controller(String applicationName, String applicationVersion,
            String applicationContact) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationContact = applicationContact;
    }
    
    // -------------- Search  -------------------------------------------------//
    
    // To be Overridden
    protected void search(String searchText){}
    
    protected void initSearch(String searchText){
    
        setQueryWs(getQueryWs());
        setSearchFilter(getSearchFilter());
        
        if (getSearchFilter()== null) return;
        getSearchFilter().setQuery(searchText);
    }
    //To be Overridden
    protected SearchFilterWs2 getDefaultSearchFilter(){
        return null;
    }
    /**
     * @return the filter
     */
    protected SearchFilterWs2 getSearchFilter() {
        if (searchFilter == null)
            searchFilter = getDefaultSearchFilter();
        return searchFilter;
    }
    /**
     * @param filter the filter to set
     */
    protected void setSearchFilter(SearchFilterWs2 filter) {
        this.searchFilter = filter;
    }
    
    public boolean hasMore(){
        
        if (getSearch() == null) return true;
        return getSearch().hasMore();
    }
    /**
     * @return the search
     */
    protected SearchWs2 getSearch() {
        return search;
    }
    /**
     * @param search the search to set
     */
    protected void setSearch(SearchWs2 search) {
        this.search = search;
    }
    // -------------- LookUp  -------------------------------------------------//
    
    //To be Overridden
    protected IncludesWs2 getDefaultIncludes(){
        return null;
    }
    /**
     * @return the includes
     */
    public IncludesWs2 getIncludes() {
        if (includes == null)
            includes = getDefaultIncludes();
        return includes;
    }
    /**
     * @param includes the includes to set
     */
    public void setIncludes(IncludesWs2 includes) {
        this.includes = includes;
    }
    
    /**
     * @return the lookUp
   */
    protected LookUpWs2 getLookUp() {
        return lookUp;
    }

    /**
     * @param lookUp the lookUp to set
     */
    protected void setLookUp(LookUpWs2 lookUp) {
        this.lookUp = lookUp;
    }

    
    /**
     * @return the Included
     */
    protected IncludesWs2 getIncluded() {
        return Included;
    }

    /**
     * @param Included the Included to set
     */
    protected void setIncluded(IncludesWs2 Included) {
        this.Included = Included;
    }

    protected IncludesWs2 getIncrementalInc(IncludesWs2 inc){
        
        if (getIncludes().isArtistRelations() && !getIncluded().isArtistRelations()) inc.setArtistRelations(true);
        if (getIncludes().isLabelRelations() && !getIncluded().isLabelRelations()) inc.setLabelRelations(true);
        if (getIncludes().isReleaseRelations() && !getIncluded().isReleaseRelations()) inc.setReleaseRelations(true);
        if (getIncludes().isReleaseGroupRelations() && !getIncluded().isReleaseGroupRelations()) inc.setReleaseGroupRelations(true);
        if (getIncludes().isRecordingRelations() && !getIncluded().isRecordingRelations()) inc.setRecordingRelations(true);
        if (getIncludes().isWorkRelations() && !getIncluded().isWorkRelations()) inc.setWorkRelations(true);
        if (getIncludes().isUrlRelations() && !getIncluded().isUrlRelations()) inc.setUrlRelations(true);
        
        if (getIncludes().isRecordingLevelRelations() && !getIncluded().isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        if (getIncludes().isWorkLevelRelations() && !getIncluded().isWorkLevelRelations()) inc.setWorkLevelRelations(true);
        
        if (getIncludes().isArtistCredits() && !getIncluded().isArtistCredits()) inc.setArtistCredits(true);
        if (getIncludes().isAnnotation() && !getIncluded().isAnnotation()) inc.setAnnotation(true);
        if (getIncludes().isTags() && !getIncluded().isTags()) inc.setTags(true);
        if (getIncludes().isRatings() && !getIncluded().isRatings()) inc.setRatings(true);
        if (getIncludes().isUserTags() && !getIncluded().isUserTags()) inc.setUserTags(true);
        if (getIncludes().isUserRatings() && !getIncluded().isUserRatings()) inc.setUserRatings(true);
        
        return inc;
    }
    
    protected boolean needsLookUp(IncludesWs2 inc){
        
        return ( inc.isArtistRelations()||
                    inc.isLabelRelations()||
                    inc.isReleaseRelations()||
                    inc.isReleaseGroupRelations()||
                    inc.isRecordingRelations()||
                    inc.isWorkRelations()||
                    inc.isUrlRelations()||
                    inc.isTags()||
                    inc.isRatings()||
                    inc.isUserTags()||
                    inc.isUserRatings());
    }
    protected void updateEntity(EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc) throws MBWS2Exception{
    
        updateTags(entity,transit,inc);
        updateRatings(entity,transit,inc);
        updateUserTags(entity,transit,inc);
        updateUserRatings(entity,transit,inc);
        updateRelations(entity,transit,inc);
        
    }
    
    private void updateTags(EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc){
        
        if (!inc.isTags()) return;

        entity.setTags(transit.getTags());
        getIncluded().setTags(true);
    }
     private void updateUserTags(EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc){
        
        if (!inc.isUserTags()) return;

        entity.setUserTags(transit.getUserTags());
        getIncluded().setUserTags(true);
    }
    private void updateRatings(EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc){
        
        if (!inc.isRatings()) return;
        
        entity.setRating(transit.getRating());
        getIncluded().setRatings(true);
    }
    private void updateUserRatings(EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc){
        
        if (!inc.isRatings()) return;
        
        entity.setUserRating(transit.getUserRating());
        getIncluded().setUserRatings(true);
    }
    private void updateRelations (EntityWs2 entity, EntityWs2 transit, IncludesWs2 inc) throws MBWS2Exception{
            
        for (RelationWs2 rel : transit.getRelationList().getRelations())
        {
            entity.getRelationList().addRelation(rel);

            /* MB don't accept Artist Credits requests for works 
          * at the moment,so we have to complete the relations.
          * 
          * Time consuming, but no other way. To avoid it, set 
          * getIncludes().setArtistCredits(false) when asking for
          * relations involving a Work.
          */
            
            if (!getIncludes().isArtistCredits()) continue;

            if (inc.isWorkRelations() &&
                          rel.getTargetType().equals(RelationWs2.TO_WORK)){

                WorkWs2 wkWs2 = (WorkWs2)rel.getTarget();

                if (wkWs2.getArtistCredit() != null ||
                    !wkWs2.getWritersString().isEmpty()) continue;

                Work work = new Work();
                work.setQueryWs(getQueryWs());
                
                work.getIncludes().excludeAll();
                work.getIncludes().setArtistRelations(true);
                work.getIncludes().setArtistCredits(true);
                
                wkWs2 = work.lookUp(wkWs2);

                rel.setTarget(wkWs2);
            }
            else if (getEntity()instanceof WorkWs2 &&
                        inc.isRecordingRelations() &&
                        rel.getTargetType().equals(RelationWs2.TO_RECORDING)) {
            
                RecordingWs2 recWs2 = (RecordingWs2)rel.getTarget();

                if (recWs2.getArtistCredit() != null) continue;

                Recording rec = new Recording();
                rec.setQueryWs(getQueryWs());

                rec.getIncludes().excludeAll();
                rec.getIncludes().setArtistCredits(true);

                recWs2 = rec.lookUp(recWs2);
                rel.setTarget(recWs2);
            }
            else if (getEntity()instanceof WorkWs2 &&
                        inc.isReleaseRelations() &&
                        rel.getTargetType().equals(RelationWs2.TO_RELEASE)){
                
                ReleaseWs2 relWs2 = (ReleaseWs2)rel.getTarget();

                if (relWs2.getArtistCredit() != null) continue;

                Release rls = new Release();
                
                rls.setQueryWs(getQueryWs());
                
                rls.getIncludes().excludeAll();
                rls.getIncludes().setArtistCredits(true);
            
            }
            else if (getEntity()instanceof WorkWs2 &&
                        inc.isReleaseGroupRelations() &&
                        rel.getTargetType().equals(RelationWs2.TO_RELEASE_GROUP)){
                
                 ReleaseGroupWs2 relWs2 = (ReleaseGroupWs2)rel.getTarget();

                if (relWs2.getArtistCredit() != null) continue;

                ReleaseGroup rg = new ReleaseGroup();
                
                rg.setQueryWs(getQueryWs());
                
                rg.getIncludes().excludeAll();
                rg.getIncludes().setArtistCredits(true);
            }
        }
        updateIncludedRelations(inc);
    }
    private void updateIncludedRelations (IncludesWs2 inc)
    {
            if (inc.isArtistRelations()) getIncluded().setArtistRelations(true);   
            if (inc.isLabelRelations()) getIncluded().setLabelRelations(true);
            if (inc.isReleaseRelations()) getIncluded().setReleaseRelations(true);
            if (inc.isReleaseGroupRelations()) getIncluded().setReleaseGroupRelations(true);
            if (inc.isRecordingRelations()) getIncluded().setRecordingRelations(true);
            if (inc.isWorkRelations()) getIncluded().setWorkRelations(true);
            if (inc.isUrlRelations()) getIncluded().setUrlRelations(true);
            
            if (inc.isRecordingLevelRelations()) getIncluded().setRecordingLevelRelations(true);
            if (inc.isWorkLevelRelations()) getIncluded().setWorkLevelRelations(true);
    }
   
    protected void loadAnnotation(EntityWs2 entity){
    
            entity.getAnnotation(getAnnotationWs());
            getIncluded().setAnnotation(true);
    }
    /**
     * @return the entity
     */
    protected EntityWs2 getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    protected void setEntity(EntityWs2 entity) {
        this.entity = entity;
    }
    // ------------- Browse -------------------------------------------------//
    
    // ------------- Submission --------------------------------------------//
       
    public final void AddTags(String[] userTags) throws MBWS2Exception{
        
        for (String tag : userTags)
        {
            TagWs2 t = new TagWs2();
            t.setName(tag);
            entity.addUserTag(t);
        }
        postUserTags();
    }
    
    public final void postUserTags() throws MBWS2Exception{
         
         UserTagSubmissionWs2 query = new UserTagSubmissionWs2(getQueryWs());
         query.addEntity(entity);
         Metadata md = query.submit();
         // You could also test the metadata.message if is OK or throw an exception
         getIncluded().setTags(false);
         getIncluded().setUserTags(false);
         // in order to refresh the data at next Lookup.
         
     }
     public final void rate(float rating) throws MBWS2Exception{

        RatingsWs2 r = new RatingsWs2();
        r.setAverageRating(rating);
        entity.setUserRating(r);
        postUserRatings();
    }
     public final void postUserRatings() throws MBWS2Exception{
         
         UserRatingSubmissionWs2 query = new UserRatingSubmissionWs2(getQueryWs());
         query.addEntity(entity);
         Metadata md = query.submit();
         // You could also test the metadata.message if is OK or throw an exception
         getIncluded().setRatings(false);
         getIncluded().setUserRatings(false);
         // in order to refresh the data at next Lookup.
     }

    // ----------------------------------------------------------------//
    
    private WebService getDefaultQueryWs(){
        
        return createWebService(applicationName, applicationVersion,
                applicationContact);
    }
    private WebService getDefaultAnnotationWs(){
        
        WebService adws = createWebService(applicationName, applicationVersion,
                applicationContact);

        ((DefaultWebServiceWs2)adws).setHost(ANNOTATIONHOST);
        
        return adws;
    }

    /**
     * Creates an instance of the web service implementation.
     * 
     * @param userAgentName
     *            custom application name used in user agent string. If
     *            <code>null</code>, the default user agent string is used.
     * @param userAgentVersion
     *            custom application version used in user agent string
     * @param userAgentContact
     *            contact URL or author email used in user agent string
     * 
     * @return a new instance of the web service implementation.
     */
    private WebService createWebService(String userAgentName,
            String userAgentVersion, String userAgentContact) {
        if (userAgentName != null) {
            return new HttpClientWebServiceWs2(userAgentName, userAgentVersion,
                    userAgentContact);
        } else {
            return new HttpClientWebServiceWs2();
        }
    }

    /**
     * @return the queryWs
     */
    public WebService getQueryWs() {
        
        if (queryWs == null)
            queryWs = getDefaultQueryWs();
        return queryWs;
    }
    /**
     * @param ws the queryWs to set
     */
    public void setQueryWs(WebService ws) {
        this.queryWs = ws;
    }
    /**
     * @param ws the annotationWs to set
     */
    public void setAnnotationWs(WebService ws) {
        this.annotationWs = ws;
    }
        /**
     * @return the annotationWs
     */
    public WebService getAnnotationWs() {
        
        if (annotationWs == null)
            annotationWs = getDefaultAnnotationWs();
        return annotationWs;
    }

    /**
     * @return the browseLimit
     */
    protected int getBrowseLimit() {
        return browseLimit;
    }

    /**
     * @param browseLimit the browseLimit to set
     */
    protected void setBrowseLimit(int browseLimit) {
        this.browseLimit = browseLimit;
    }

    
    /**
     * @return the incoming Entity if any.
     */
    public EntityWs2 getIncoming() {
        return incoming;
    }

    /**
     * @param incoming the incoming Entity to set
     */
    public void setIncoming(EntityWs2 incoming) {
        this.incoming = incoming;
    }
}
