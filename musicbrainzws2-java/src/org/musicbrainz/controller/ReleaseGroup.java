/*
 * A controller for the ReleaseGroup Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.ReleaseBrowseFilterWs2;
import org.musicbrainz.filter.searchfilter.ReleaseGroupSearchFilterWs2;
import org.musicbrainz.includes.ReleaseGroupIncludesWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseGroupResultWs2;
import org.musicbrainz.query.browse.ReleaseBrowseWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.ReleaseGroupSearchWs2;

public class ReleaseGroup extends Controller {

    private int browseLimit = 100;
    private ReleaseBrowseWs2 releaseBrowse;
    private ReleaseIncludesWs2 releaseIncludes;
    private ReleaseBrowseFilterWs2 releaseBrowseFilter;
    
    public ReleaseGroup(){
        super();
        setIncluded(new ReleaseGroupIncludesWs2());
    }
    
    // -------------- Search  -------------------------------------------------//
    
    @Override
    public ReleaseGroupSearchFilterWs2 getSearchFilter(){
        
        return (ReleaseGroupSearchFilterWs2)super.getSearchFilter();
    }
        
    @Override
    protected ReleaseGroupSearchWs2 getSearch(){
        
        return (ReleaseGroupSearchWs2)super.getSearch();
    }
    @Override
    protected ReleaseGroupSearchFilterWs2 getDefaultSearchFilter(){
        
        ReleaseGroupSearchFilterWs2 f = new ReleaseGroupSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;

    }
    
    @Override
    public void search(String searchText){

        initSearch(searchText);
        setSearch(new ReleaseGroupSearchWs2(getQueryWs(),getSearchFilter()));
       
    }
    public List <ReleaseGroupResultWs2> getFullSearchResultList() {

        return getSearch().getFullList();

    }
    public List <ReleaseGroupResultWs2> getFirstSearchResultPage() {

        return getSearch().getFirstPage();
    }
    public List <ReleaseGroupResultWs2> getNextSearchResultPage() {

        return getSearch().getNextPage();
    }
    
   // -------------- LookUp -------------------------------------------------//
    
    /**
     * @return the releaseGroupIncludes
     */
    @Override
    public ReleaseGroupIncludesWs2 getIncludes() {
        return (ReleaseGroupIncludesWs2)super.getIncludes();
    }
    @Override
    protected  ReleaseGroupIncludesWs2 getDefaultIncludes(){
        
        ReleaseGroupIncludesWs2 inc =new ReleaseGroupIncludesWs2();
        
        inc.setUrlRelations(true);
        inc.setLabelRelations(true);
        inc.setArtistRelations(true);
        inc.setReleaseGroupRelations(true);
        inc.setReleaseRelations(true);
        inc.setRecordingRelations(true);
        inc.setWorkRelations(true);
        
        inc.setTags(true);
        inc.setRatings(true);
        inc.setUserTags(false);
        inc.setUserRatings(false);
        
        inc.setAnnotation(true);
        
        inc.setArtistCredits(true);
        inc.setReleases(true);

        return inc;
    }
    @Override
    protected  ReleaseGroupIncludesWs2 getIncluded() {
        return (ReleaseGroupIncludesWs2)super.getIncluded();
    }
    private ReleaseGroupWs2 getReleaseGroup() {
        return (ReleaseGroupWs2)getEntity();
    }

    public ReleaseGroupWs2 getComplete(ReleaseGroupWs2 releaseGroup) throws MBWS2Exception {
        if (releaseGroup == null) return null;
        if (releaseGroup.getId() == null) return releaseGroup;
                                
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(releaseGroup);
        
        return getComplete(releaseGroup.getId());
    }
    public ReleaseGroupWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        if (getIncludes().isReleases()) getFullReleaseList();
        
        return getReleaseGroup();
    }
    
    public ReleaseGroupWs2 lookUp(ReleaseGroupWs2 releaseGroup) throws MBWS2Exception{

        if (releaseGroup == null) return null;
        if (releaseGroup.getId() == null) return releaseGroup;
                                        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(releaseGroup);
        
        return lookUp(releaseGroup.getId());
    }
    protected ReleaseGroupIncludesWs2 getIncrementalInc(ReleaseGroupIncludesWs2 inc){

        inc = (ReleaseGroupIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isArtistCredits() && !getIncluded().isArtistCredits()) inc.setArtistCredits(true);

        return inc;
    }
    private boolean needsLookUp(ReleaseGroupIncludesWs2 inc){
        
        return (  getReleaseGroup() == null ||
                     super.needsLookUp(inc) ||
                     inc.isArtistCredits());
    }
    public ReleaseGroupWs2 lookUp(String id) throws MBWS2Exception{

        ReleaseGroupIncludesWs2 inc = getIncrementalInc(new ReleaseGroupIncludesWs2());

        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting releaseGroupInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse).
        
        inc.setReleases(false); // handled via a browse.
        
        // the following inc params are meaningless if not inc.isRelease() or
        // inc.isRecordings().
        
        //inc.setMedia(false);
        //inc.setDiscids(false);
        //inc.setIsrcs(false);
       // inc.setPuids(false);
        
        // Sanity check.
        inc.setRecordingLevelRelations(false); // invalid request
        inc.setWorkLevelRelations(false);// invalid request

        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            ReleaseGroupWs2 transit = null;
            transit = getLookUp().getReleaseGroupById(id, inc);

            if (transit ==null) return null;
            if (getReleaseGroup() == null || !getReleaseGroup().equals(transit)) // releaseGroup is changed.
            {
                setEntity(transit);
                setIncluded(inc);
                releaseBrowse = null;
            }
            else 
            {
                updateEntity(getReleaseGroup(),transit,inc);
                if (inc.isArtistCredits()) 
                {
                    getReleaseGroup().setArtistCredit(transit.getArtistCredit());
                    getIncluded().setArtistCredits(true);
                }
            }
        }
        if (inc.isAnnotation()) loadAnnotation(getReleaseGroup());
        
        initBrowses();

        return getReleaseGroup();
    }
    // ------------- Browse -------------------------------------------------//
    
    private void initBrowses(){
                
        if (getIncludes().isReleases() && releaseBrowse == null)
        {
            ReleaseIncludesWs2 relInc = getReleaseIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request
            
            ReleaseBrowseFilterWs2 f = getReleaseBrowseFilter();

            f.setRelatedEntity(RELEASEGROUP);
            f.setRelatedId(getReleaseGroup().getId());

            releaseBrowse = new ReleaseBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setReleases(true);
        }
    }
     public List <ReleaseWs2> getFullReleaseList() {
        
         if (getReleaseGroup() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         if (!hasMoreReleases()) return getReleaseGroup().getReleases();
         
         List <ReleaseWs2> list = releaseBrowse.getFullList();
        
        for (ReleaseWs2 rel : list)
         {
             getReleaseGroup().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getFirstReleaseListPage() {
        
        if (getReleaseGroup() == null) return null;
        getIncludes().setReleases(true);
        if (releaseBrowse == null ) initBrowses();
        if (releaseBrowse == null ) return null;
         
         List <ReleaseWs2> list = releaseBrowse.getFirstPage();
        
        for (ReleaseWs2 rel : list)
         {
             getReleaseGroup().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getNextReleaseListPage() {

        if (getReleaseGroup() == null) return null;
        getIncludes().setReleases(true);
        if (releaseBrowse == null ) initBrowses();
        if (releaseBrowse == null ) return null;
        if (!hasMoreReleases()) return new ArrayList<ReleaseWs2> ();
         
        List <ReleaseWs2> list = releaseBrowse.getNextPage();
        
        for (ReleaseWs2 rel : list)
         {
             getReleaseGroup().addRelease(rel);
         }
        
        return list;
    }
    public boolean hasMoreReleases(){
        if (getReleaseGroup() == null) return true;
        if (releaseBrowse == null ) return true;
        return releaseBrowse.hasMore();
    }
    
    
    private ReleaseIncludesWs2 getDefaultReleaseInclude(ReleaseGroupIncludesWs2 releaseGroupinc){
        
        ReleaseIncludesWs2 inc =new ReleaseIncludesWs2();
        
        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setReleaseGroups(true);
        inc.setMedia(true);
        
        inc.setRecordings(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);

        if (releaseGroupinc == null) return inc;

        //if (recordinginc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (recordinginc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);

        return inc;
    }
    private ReleaseBrowseFilterWs2 getDefaultReleaseBrowseFilter(){
        
        ReleaseBrowseFilterWs2 f = new ReleaseBrowseFilterWs2();
        
        f.getReleaseTypeFilter().setTypeAll(true);
        f.getReleaseStatusFilter().setStatusAll(true);

        f.setLimit((long)browseLimit);
        f.setOffset((long)0);
        
        return f;

    }
    
    /**
     * @return the releaseIncludes
     */
    public ReleaseIncludesWs2 getReleaseIncludes() {
        if (releaseIncludes == null)
            releaseIncludes = getDefaultReleaseInclude(getIncludes());
        return releaseIncludes;
    }

    /**
     * @param releaseIncludes the releaseIncludes to set
     */
    public void setReleaseIncludes(ReleaseIncludesWs2 releaseIncludes) {
        this.releaseIncludes = releaseIncludes;
    }

    /**
     * @return the releaseBrowseFilter
     */
    public ReleaseBrowseFilterWs2 getReleaseBrowseFilter() {
         if (releaseBrowseFilter == null)
            releaseBrowseFilter = getDefaultReleaseBrowseFilter();
         return releaseBrowseFilter;
    }

    /**
     * @param releaseBrowseFilter the releaseBrowseFilter to set
     */
    public void setReleaseBrowseFilter(ReleaseBrowseFilterWs2 releaseBrowseFilter) {
        this.releaseBrowseFilter = releaseBrowseFilter;
    }

}
