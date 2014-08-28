/*
 * A controller for the Recording Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.ReleaseBrowseFilterWs2;
import org.musicbrainz.filter.searchfilter.RecordingSearchFilterWs2;
import org.musicbrainz.includes.RecordingIncludesWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.query.browse.ReleaseBrowseWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.RecordingSearchWs2;

public class Recording extends Controller{

    private int browseLimit = 100;
    private ReleaseBrowseWs2 releaseBrowse;
    private ReleaseIncludesWs2 releaseIncludes;
    private ReleaseBrowseFilterWs2 releaseBrowseFilter;
    
    public Recording(){
        super();
        setIncluded(new RecordingIncludesWs2());
    }
    // -------------- Search  -------------------------------------------------//
    
    @Override
    public RecordingSearchFilterWs2 getSearchFilter(){
        
        return (RecordingSearchFilterWs2)super.getSearchFilter();
    }
    @Override
    protected RecordingSearchWs2 getSearch(){
        
        return (RecordingSearchWs2)super.getSearch();
    }
    @Override
    protected RecordingSearchFilterWs2 getDefaultSearchFilter(){
        
        RecordingSearchFilterWs2 f = new RecordingSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;
    }
    @Override
    public void search(String searchText){

             initSearch(searchText);
        setSearch(new RecordingSearchWs2(getQueryWs(),getSearchFilter()));
    }
    
    public List <RecordingResultWs2> getFullSearchResultList() {

        return getSearch().getFullList();

    }
    public List <RecordingResultWs2> getFirstSearchResultPage() {

        return getSearch().getFirstPage();
    }
    public List <RecordingResultWs2> getNextSearchResultPage() {

        return getSearch().getNextPage();
    }
    // -------------- LookUp -------------------------------------------------//
    
    @Override
    public RecordingIncludesWs2 getIncludes() {
        return (RecordingIncludesWs2)super.getIncludes();
    }
    @Override
    protected  RecordingIncludesWs2 getDefaultIncludes(){
        
        RecordingIncludesWs2 inc =new RecordingIncludesWs2();
        
        inc.setUrlRelations(true);
        inc.setLabelRelations(true);
        inc.setArtistRelations(true);
        inc.setReleaseGroupRelations(true);
        inc.setReleaseRelations(true);
        inc.setRecordingRelations(true);
        inc.setWorkRelations(true);
        
        inc.setAnnotation(true);
        inc.setTags(true);
        inc.setRatings(true);
        inc.setUserTags(false);
        inc.setUserRatings(false);
        inc.setArtistCredits(true);
        
        inc.setIsrcs(true);
        inc.setPuids(true);
        
        inc.setReleases(true);
        
        inc.setWorkLevelRelations(true);


        return inc;
    }
    @Override
    protected RecordingIncludesWs2 getIncluded() {
        return (RecordingIncludesWs2)super.getIncluded();
    }
    private RecordingWs2 getRecording() {
        return (RecordingWs2)getEntity();
    }
    
    
    public RecordingWs2 getComplete(RecordingWs2 recording) throws MBWS2Exception {
        if (recording == null) return null;
        if (recording.getId() == null) return recording;
        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(recording);
        
        return getComplete(recording.getId());
    }
    public RecordingWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        if (getIncludes().isReleases()) getFullReleaseList();
        
        return getRecording();
    }
    
    public RecordingWs2 lookUp(RecordingWs2 recording) throws MBWS2Exception{

        if (recording == null) return null;
        if (recording.getId() == null) return recording;
        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(recording);
        
        return lookUp(recording.getId());
    }
    protected RecordingIncludesWs2 getIncrementalInc(RecordingIncludesWs2 inc){

        inc = (RecordingIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isWorkLevelRelations() && !getIncluded().isWorkLevelRelations()) inc.setWorkLevelRelations(true);
        if (getIncludes().isArtistCredits() && !getIncluded().isArtistCredits()) inc.setArtistCredits(true);
        if (getIncludes().isIsrcs() && !getIncluded().isIsrcs()) inc.setIsrcs(true);
        if (getIncludes().isPuids() && !getIncluded().isPuids()) inc.setPuids(true);
        return inc;
    }
    private boolean needsLookUp(RecordingIncludesWs2 inc){
        
        return (  getRecording() == null ||
                     super.needsLookUp(inc) ||
                     inc.isArtistCredits() ||
                     inc.isPuids() ||
                     inc.isIsrcs());
    }
    public RecordingWs2 lookUp(String id) throws MBWS2Exception{
        
        RecordingIncludesWs2 inc = getIncrementalInc(new RecordingIncludesWs2());

        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting recordingInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse).
        
        inc.setRecordingLevelRelations(false); // invalid request
        
        inc.setReleases(false); // handled via a browse.
        
        
        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            RecordingWs2 transit = null;
            transit = getLookUp().getRecordingById(id, inc);
             
            if (transit ==null) return null;
            if (getRecording() == null || !getRecording().equals(transit)) // recording is changed.
            {
                setEntity(transit);
                setIncluded(inc);
                releaseBrowse = null;
            }
            else 
            {
                updateEntity(getRecording(),transit, inc);

                if (inc.isArtistCredits()) 
                {
                    getRecording().setArtistCredit(transit.getArtistCredit());
                    getIncluded().setArtistCredits(true);
                }
                if (inc.isIsrcs()) 
                {
                    getRecording().setIsrcs(transit.getIsrcs());
                    getIncluded().setIsrcs(true);
                }
                if (inc.isPuids()) 
                {
                    getRecording().setPuids(transit.getPuids());
                    getIncluded().setPuids(true);
                }
             }
        }
        if (inc.isAnnotation()) loadAnnotation(getRecording());
        
        initBrowses();

        return getRecording();
    }
    // ------------- Browse -------------------------------------------------//
    
    private void initBrowses(){
        
        if (getIncludes().isReleases() && releaseBrowse == null)
        {
            ReleaseIncludesWs2 relInc = getReleaseIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request
            
            ReleaseBrowseFilterWs2 f = getReleaseBrowseFilter();

            f.setRelatedEntity(RECORDING);
            f.setRelatedId(getRecording().getId());

            releaseBrowse = new ReleaseBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setReleases(true);
        }
    }
     public List <ReleaseWs2> getFullReleaseList() {
        
         if (getRecording() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         if (!hasMoreReleases()) return getRecording().getReleases();
         
         List <ReleaseWs2> list = releaseBrowse.getFullList();
        
        for (ReleaseWs2 rel : list)
         {
             getRecording().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getFirstReleaseListPage() {
        
         if (getRecording() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         
         List <ReleaseWs2> list = releaseBrowse.getFirstPage();
        
        for (ReleaseWs2 rel : list)
         {
             getRecording().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getNextReleaseListPage() {

        if (getRecording() == null) return null;
        getIncludes().setReleases(true);
        if (releaseBrowse == null ) initBrowses();
        if (releaseBrowse == null ) return null;
        if (!hasMoreReleases()) return new ArrayList<ReleaseWs2> ();
         
        List <ReleaseWs2> list = releaseBrowse.getNextPage();
        
        for (ReleaseWs2 rel : list)
         {
             getRecording().addRelease(rel);
         }
        
        return list;
    }
    public boolean hasMoreReleases(){
        if (getRecording() == null) return true;
        if (releaseBrowse == null ) return true;
        return releaseBrowse.hasMore();
    }

    private ReleaseIncludesWs2 getDefaultReleaseInclude(RecordingIncludesWs2 recordinginc){
        
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

        if (recordinginc == null) return inc;
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
