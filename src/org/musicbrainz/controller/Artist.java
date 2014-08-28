/*
 * A controller for the Artist Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.RecordingBrowseFilterWs2;
import org.musicbrainz.filter.browsefilter.ReleaseBrowseFilterWs2;
import org.musicbrainz.filter.browsefilter.ReleaseGroupBrowseFilterWs2;
import org.musicbrainz.filter.browsefilter.WorkBrowseFilterWs2;
import org.musicbrainz.filter.searchfilter.ArtistSearchFilterWs2;
import org.musicbrainz.includes.ArtistIncludesWs2;
import org.musicbrainz.includes.RecordingIncludesWs2;
import org.musicbrainz.includes.ReleaseGroupIncludesWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.includes.WorkIncludesWs2;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.query.search.ArtistSearchWs2;
import org.musicbrainz.query.browse.RecordingBrowseWs2;
import org.musicbrainz.query.browse.ReleaseBrowseWs2;
import org.musicbrainz.query.browse.ReleaseGroupBrowseWs2;
import org.musicbrainz.query.browse.WorkBrowseWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;

public class Artist extends Controller{

    private ReleaseGroupBrowseWs2 releaseGroupBrowse;
    private ReleaseGroupIncludesWs2 releaseGroupIncludes;
    private ReleaseGroupBrowseFilterWs2 releaseGroupBrowseFilter;
    
    private ReleaseBrowseWs2 releaseBrowse;
    private ReleaseIncludesWs2 releaseIncludes;
    private ReleaseBrowseFilterWs2 releaseBrowseFilter;
    
    private ReleaseBrowseWs2 releaseVABrowse;
    private ReleaseIncludesWs2 releaseVAIncludes;
    private ReleaseBrowseFilterWs2 releaseVABrowseFilter;
    
    private RecordingBrowseWs2 recordingBrowse;
    private RecordingIncludesWs2 recordingIncludes;
    private RecordingBrowseFilterWs2 recordingBrowseFilter;
    
    private WorkBrowseWs2 workBrowse;
    private WorkIncludesWs2 workIncludes;
    private WorkBrowseFilterWs2 workBrowseFilter;
    
    public Artist(){
    
        super();
        setIncluded(new ArtistIncludesWs2());
    }
   
    
    // -------------- Search  -------------------------------------------------//
    
    @Override
    public ArtistSearchFilterWs2 getSearchFilter(){
        
        return (ArtistSearchFilterWs2)super.getSearchFilter();
    }
        
    @Override
    protected ArtistSearchWs2 getSearch(){
        
        return (ArtistSearchWs2)super.getSearch();
    }
    
    @Override
    protected ArtistSearchFilterWs2 getDefaultSearchFilter(){
        
        ArtistSearchFilterWs2 f = new ArtistSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;

    }
    @Override
    public void search(String searchText){

        initSearch(searchText);
        setSearch(new ArtistSearchWs2(getQueryWs(),getSearchFilter()));
    }

    public List <ArtistResultWs2> getFullSearchResultList() {

        return getSearch().getFullList();
    }
    public List <ArtistResultWs2> getFirstSearchResultPage() {

        return getSearch().getFirstPage();
    }
    public List <ArtistResultWs2> getNextSearchResultPage() {

        return getSearch().getNextPage();
    }
    
    // -------------- LookUp -------------------------------------------------//

   @Override
    public  ArtistIncludesWs2 getIncludes(){
        
        return (ArtistIncludesWs2)super.getIncludes();
    
    }
   @Override
    protected  ArtistIncludesWs2 getDefaultIncludes(){
        
        ArtistIncludesWs2 inc =new ArtistIncludesWs2();
        
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
        inc.setAliases(true);
        
        inc.setReleaseGroups(true);
        inc.setReleases(true);
        inc.setVariousArtists(true);
        inc.setRecordings(true);
        inc.setWorks(true);
        
        inc.setArtistCredits(true);
        inc.setDiscids(false);
        inc.setIsrcs(false);
        inc.setMedia(false);
        inc.setPuids(false);
        
        return inc;
    }
       /**
     * @return the artistIncluded
     */
    @Override
    protected ArtistIncludesWs2 getIncluded() {
        return (ArtistIncludesWs2)super.getIncluded();
    }
      
    private ArtistWs2 getArtist() {
        return (ArtistWs2)getEntity();
    }
   
    public ArtistWs2 getComplete(ArtistWs2 artist) throws MBWS2Exception {
        if (artist == null) return null;
        if (artist.getId() == null) return artist;
                
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(artist);
        
        return getComplete(artist.getId());
    }
    public ArtistWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        
        if (getIncludes().isReleaseGroups()) getFullReleaseGroupList();
        if (getIncludes().isReleases()) getFullReleaseList();
        if (getIncludes().isVariousArtists()) getFullReleaseVAList();
        if (getIncludes().isRecordings()) getFullRecordingList();
        if (getIncludes().isWorks()) getFullWorkList();
        
        return getArtist();
    }
    
    public final ArtistWs2 lookUp(ArtistWs2 artist) throws MBWS2Exception{

        if (artist == null) return null;
        if (artist.getId() == null) return artist;
        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(artist);
        
        return lookUp(artist.getId());
    }
    protected ArtistIncludesWs2 getIncrementalInc(ArtistIncludesWs2 inc){

        inc = (ArtistIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isAliases() && !getIncluded().isAliases()) inc.setAliases(true);
        
        return inc;
    }
    private boolean needsLookUp(ArtistIncludesWs2 inc){
        
        return (  getArtist() == null ||
                     super.needsLookUp(inc) ||
                     inc.isAliases());
    }
    public final ArtistWs2 lookUp(String id) throws MBWS2Exception{

        ArtistIncludesWs2 inc = getIncrementalInc(new ArtistIncludesWs2());
        
        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting artistInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse).
        
        inc.setReleaseGroups(false); // handled via a browse.
        inc.setReleases(false); // handled via a browse.
        inc.setVariousArtists(false); // handled via a browse.
        inc.setRecordings(false); // handled via a browse.
        inc.setWorks(false); // handled via a browse.
        
        // the following inc params are meaningless if not inc.isRelease() or
        // inc.isRecordings().
        
        inc.setMedia(false);
        inc.setDiscids(false);
        inc.setIsrcs(false);
        inc.setPuids(false);
        
        // Sanity check.
        if(inc.isArtistCredits()) inc.setReleases(true); // to avoid the artist credits exceptions.
        
        inc.setRecordingLevelRelations(false);// invalid request
        inc.setWorkLevelRelations(false);// invalid request
        
        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            ArtistWs2 transit = null;
            transit = getLookUp().getArtistById(id, inc);
             

            if (transit ==null) return null;
            if (getArtist() == null || !getArtist().equals(transit)) // artist is changed.
            {
                setEntity(transit);
                setIncluded(inc);
                releaseGroupBrowse = null;
                releaseBrowse = null;
                releaseVABrowse = null;
                recordingBrowse= null;
                workBrowse= null;
            }
            else 
            {
                updateEntity(getArtist(),transit, inc);
                
                if (inc.isAliases()) 
                {
                    getArtist().setAliases(transit.getAliases());
                    getIncluded().setAliases(true);
                }
            }
        }
        if (inc.isAnnotation()) loadAnnotation(getArtist());
        
        initBrowses();
        
        return getArtist();
    }
    // ------------- Browse -------------------------------------------------//
    
    private void initBrowses(){
        
        if (getIncludes().isReleaseGroups() && releaseGroupBrowse == null){
            
            ReleaseGroupIncludesWs2 relInc = getReleaseGroupIncludes();
            ReleaseGroupBrowseFilterWs2 f = getReleaseGroupBrowseFilter();
            f.setRelatedEntity(ARTIST);
            f.setRelatedId(getArtist().getId());

            releaseGroupBrowse = new ReleaseGroupBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setReleaseGroups(true);
        }
        if (getIncludes().isReleases() && releaseBrowse == null)
        {
            ReleaseIncludesWs2 relInc = getReleaseIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request

            ReleaseBrowseFilterWs2 f = getReleaseBrowseFilter();

            f.setRelatedEntity(ARTIST);
            f.setRelatedId(getArtist().getId());

            releaseBrowse = new ReleaseBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setReleases(true);
        }
        if (getIncludes().isVariousArtists() && releaseVABrowse == null)
        {
            ReleaseIncludesWs2 relInc = getReleaseVAIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request

            ReleaseBrowseFilterWs2 f = getReleaseVABrowseFilter();

            f.setRelatedEntity(TRACKARTIST);
            f.setRelatedId(getArtist().getId());

            releaseVABrowse = new ReleaseBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setVariousArtists(true);
        }
        if (getIncludes().isRecordings() && recordingBrowse == null)
        {
            RecordingIncludesWs2 relInc = getRecordingIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request

            RecordingBrowseFilterWs2 f = getRecordingBrowseFilter();

            f.setRelatedEntity(ARTIST);
            f.setRelatedId(getArtist().getId());

            recordingBrowse = new RecordingBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setRecordings(true);
        }
        if (getIncludes().isWorks() && workBrowse == null)
        {
            WorkIncludesWs2 relInc = getWorkIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request

            WorkBrowseFilterWs2 f = getWorkBrowseFilter();

            f.setRelatedEntity(ARTIST);
            f.setRelatedId(getArtist().getId());

            workBrowse = new WorkBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setWorks(true);
         }
    }

     public List <ReleaseGroupWs2> getFullReleaseGroupList() {
        
         if (getArtist() == null) return null;
         getIncludes().setReleaseGroups(true);
         if (releaseGroupBrowse == null ) initBrowses();
         if (releaseGroupBrowse == null ) return null;
         if (!hasMoreReleaseGroups()) return getArtist().getReleaseGroups();
         
         List <ReleaseGroupWs2> list = releaseGroupBrowse.getFullList();
        
        for (ReleaseGroupWs2 rel : list)
         {
             getArtist().addReleaseGroup(rel);
         }
        
        return list;
    }
    public List <ReleaseGroupWs2> getFirstReleaseGroupListPage() {
        
        if (getArtist() == null) return null;
        getIncludes().setReleaseGroups(true);
        if (releaseGroupBrowse == null ) initBrowses();
        if (releaseGroupBrowse == null ) return null;
         
         List <ReleaseGroupWs2> list = releaseGroupBrowse.getFirstPage();
        
        for (ReleaseGroupWs2 rel : list)
         {
             getArtist().addReleaseGroup(rel);
         }
        
        return list;
    }
    public List <ReleaseGroupWs2> getNextReleaseGroupListPage() {

        if (getArtist() == null) return null;
        getIncludes().setReleaseGroups(true);
        if (releaseGroupBrowse == null ) initBrowses();
        if (releaseGroupBrowse == null ) return null;
        if (!hasMoreReleaseGroups()) return new ArrayList<ReleaseGroupWs2> ();
         
        List <ReleaseGroupWs2> list = releaseGroupBrowse.getNextPage();
        
        for (ReleaseGroupWs2 rel : list)
         {
             getArtist().addReleaseGroup(rel);
         }
        
        return list;
    }
    public boolean hasMoreReleaseGroups(){
        if (getArtist() == null) return true;
        if (releaseGroupBrowse == null ) return true;
        return releaseGroupBrowse.hasMore();
    }

    public List <ReleaseWs2> getFullReleaseList() {
        
         if (getArtist() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         if (!hasMoreReleases()) return getArtist().getReleases();
         
         List <ReleaseWs2> list = releaseBrowse.getFullList();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getFirstReleaseListPage() {
        
        if (getArtist() == null) return null;
        getIncludes().setReleases(true);
        if (releaseBrowse == null ) initBrowses();
        if (releaseBrowse == null ) return null;
         
         List <ReleaseWs2> list = releaseBrowse.getFirstPage();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getNextReleaseListPage() {

        if (getArtist() == null) return null;
        getIncludes().setReleases(true);
        if (releaseBrowse == null ) initBrowses();
        if (releaseBrowse == null ) return null;
        if (!hasMoreReleases()) return new ArrayList<ReleaseWs2> ();
         
        List <ReleaseWs2> list = releaseBrowse.getNextPage();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addRelease(rel);
         }
        
        return list;
    }
    public boolean hasMoreReleases(){
        if (getArtist() == null) return true;
        if (releaseBrowse == null ) return true;
        return releaseBrowse.hasMore();
    }
    public List <ReleaseWs2> getFullReleaseVAList() {
        
         if (getArtist() == null) return null;
         getIncludes().setVariousArtists(true);
         if (releaseVABrowse == null ) initBrowses();
         if (releaseVABrowse == null ) return null;
         if (!hasMoreVAReleases()) return getArtist().getReleasesVA();
         
         List <ReleaseWs2> list = releaseVABrowse.getFullList();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addReleaseVA(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getFirstReleaseVAListPage() {
        
        if (getArtist() == null) return null;
        getIncludes().setVariousArtists(true);
        if (releaseVABrowse == null ) initBrowses();
        if (releaseVABrowse == null ) return null;
         
         List <ReleaseWs2> list = releaseVABrowse.getFirstPage();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addReleaseVA(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getNextReleaseVAListPage() {

        if (getArtist() == null) return null;
        getIncludes().setVariousArtists(true);
        if (releaseVABrowse == null ) initBrowses();
        if (releaseVABrowse == null ) return null;
        if (!hasMoreVAReleases()) return new ArrayList<ReleaseWs2> ();
         
        List <ReleaseWs2> list = releaseVABrowse.getNextPage();
        
        for (ReleaseWs2 rel : list)
         {
             getArtist().addReleaseVA(rel);
         }
        
        return list;
    }
    public boolean hasMoreVAReleases(){
        if (getArtist() == null) return true;
        if (releaseVABrowse == null ) return true;
        return releaseVABrowse.hasMore();
    }
    public List <RecordingWs2> getFullRecordingList() {
        
         if (getArtist() == null) return null;
         getIncludes().setRecordings(true);
         if (recordingBrowse == null ) initBrowses();
         if (recordingBrowse == null ) return null;
         if (!hasMoreRecordings()) return getArtist().getRecordings();
         
         List <RecordingWs2> list = recordingBrowse.getFullList();
        
        for (RecordingWs2 rel : list)
         {
             getArtist().addRecording(rel);
         }
        
        return list;
    }
    public List <RecordingWs2> getFirstRecordingListPage() {
        
        if (getArtist() == null) return null;
        getIncludes().setRecordings(true);
        if (recordingBrowse == null ) initBrowses();
        if (recordingBrowse == null ) return null;
         
         List <RecordingWs2> list = recordingBrowse.getFirstPage();
        
        for (RecordingWs2 rel : list)
         {
             getArtist().addRecording(rel);
         }
        
        return list;
    }
    public List <RecordingWs2> getNextRecordingListPage() {

        if (getArtist() == null) return null;
        getIncludes().setRecordings(true);
        if (recordingBrowse == null ) initBrowses();
        if (recordingBrowse == null ) return null;
        if (!hasMoreRecordings()) return new ArrayList<RecordingWs2> ();
         
        List <RecordingWs2> list = recordingBrowse.getNextPage();
        
        for (RecordingWs2 rel : list)
         {
             getArtist().addRecording(rel);
         }
        
        return list;
    }
    public boolean hasMoreRecordings(){
        if (getArtist() == null) return true;
        if (recordingBrowse == null ) return true;
        return recordingBrowse.hasMore();
    }
    public List <WorkWs2> getFullWorkList() {
        
         if (getArtist() == null) return null;
         getIncludes().setWorks(true);
         if (workBrowse == null ) initBrowses();
         if (workBrowse == null ) return null;
         if (!hasMoreWorks()) return getArtist().getWorks();
         
         List <WorkWs2> list = workBrowse.getFullList();
        
        for (WorkWs2 rel : list)
         {
             getArtist().addWork(rel);
         }
        
        return list;
    }
    public List <WorkWs2> getFirstWorkListPage() {
        
        if (getArtist() == null) return null;
        getIncludes().setWorks(true);
        if (workBrowse == null ) initBrowses();
        if (workBrowse == null ) return null;
         
        List <WorkWs2> list = workBrowse.getFirstPage();
        
        for (WorkWs2 rel : list)
         {
             getArtist().addWork(rel);
         }
        
        return list;
    }
    public List <WorkWs2> getNextWorkListPage() {

        if (getArtist() == null) return null;
        getIncludes().setWorks(true);
        if (workBrowse == null ) initBrowses();
        if (workBrowse == null ) return null;
        if (!hasMoreWorks()) return new ArrayList<WorkWs2> ();
         
        List <WorkWs2> list = workBrowse.getNextPage();
        
        for (WorkWs2 rel : list)
         {
             getArtist().addWork(rel);
         }
        
        return list;
    }
    public boolean hasMoreWorks(){
        if (getArtist() == null) return true;
        if (workBrowse == null ) return true;
        return workBrowse.hasMore();
    }

    private ReleaseGroupIncludesWs2 getDefaultReleaseGroupInclude(ArtistIncludesWs2 artistinc){
        
        ReleaseGroupIncludesWs2 inc =new ReleaseGroupIncludesWs2();
        
        inc.setArtistCredits(true);
        inc.setReleases(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);
        
        if (artistinc == null) return inc;
        
        //if (artistinc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (artistinc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);

        return inc;
    }
        private ReleaseIncludesWs2 getDefaultReleaseInclude(ArtistIncludesWs2 artistinc){
        
        ReleaseIncludesWs2 inc =new ReleaseIncludesWs2();
        
        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setMedia(true);
        inc.setReleaseGroups(true);

        inc.setRecordings(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);
        
        if (artistinc == null) return inc;
        
        if (artistinc.isDiscids()) inc.setDiscids(true);
        //if (artistinc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (artistinc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);

        return inc;
    }
    private ReleaseIncludesWs2 getDefaultReleaseVAInclude(ArtistIncludesWs2 artistinc){
        
        ReleaseIncludesWs2 inc =new ReleaseIncludesWs2();
        
        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setMedia(true);
        inc.setReleaseGroups(true);

        inc.setRecordings(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);
        
        if (artistinc == null) return inc;
        
        if (artistinc.isDiscids()) inc.setDiscids(true);
        //if (artistinc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (artistinc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);

        return inc;
    }
    private RecordingIncludesWs2 getDefaultRecordingInclude(ArtistIncludesWs2 artistinc){
        
        RecordingIncludesWs2 inc =new RecordingIncludesWs2();
        
        inc.setArtistCredits(true);
        inc.setIsrcs(true);
        
        inc.setPuids(false);

        inc.setReleases(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);
        
        if (artistinc == null) return inc;
      
        //if (artistinc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (artistinc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);


        return inc;
    }
     private WorkIncludesWs2 getDefaultWorkInclude(ArtistIncludesWs2 artistinc){
        
        WorkIncludesWs2 inc =new WorkIncludesWs2();
        
        inc.setArtistRelations(true);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);
        
        if (artistinc == null) return inc;
       
        //if (artistinc.isRecordingLevelRelations()) inc.setRecordingLevelRelations(true);
        //if (artistinc.isWorkLevelRelations()) inc.setWorkLevelRelations(true);

        return inc;
    }
      private ReleaseGroupBrowseFilterWs2 getDefaultReleaseGroupBrowseFilter(){
        
        ReleaseGroupBrowseFilterWs2 f = new ReleaseGroupBrowseFilterWs2();
        
        f.getReleaseTypeFilter().setTypeAll(true);

        f.setLimit((long)getBrowseLimit());
        f.setOffset((long)0);
        
        return f;

    }
    private ReleaseBrowseFilterWs2 getDefaultReleaseBrowseFilter(){
        
        ReleaseBrowseFilterWs2 f = new ReleaseBrowseFilterWs2();
        
        f.getReleaseTypeFilter().setTypeAll(true);
        f.getReleaseStatusFilter().setStatusAll(true);

        f.setLimit((long)getBrowseLimit());
        f.setOffset((long)0);
        
        return f;

    }
    private ReleaseBrowseFilterWs2 getDefaultReleaseVABrowseFilter(){
        
        ReleaseBrowseFilterWs2 f = new ReleaseBrowseFilterWs2();
        
        f.getReleaseTypeFilter().setTypeAll(true);
        f.getReleaseStatusFilter().setStatusAll(true);

        f.setLimit((long)getBrowseLimit());
        f.setOffset((long)0);
        
        return f;

    }
    private RecordingBrowseFilterWs2 getDefaultRecordingBrowseFilter(){
        
        RecordingBrowseFilterWs2 f = new RecordingBrowseFilterWs2();
        
        f.setLimit((long)getBrowseLimit());
        f.setOffset((long)0);
        
        return f;

    }
    private WorkBrowseFilterWs2 getDefaultWorkBrowseFilter(){
        
        WorkBrowseFilterWs2 f = new WorkBrowseFilterWs2();
        
        f.setLimit((long)getBrowseLimit());
        f.setOffset((long)0);
        
        return f;

    }
 

    /**
     * @return the releaseGroupIncludes
     */
    public ReleaseGroupIncludesWs2 getReleaseGroupIncludes() {
        if (releaseGroupIncludes == null)
            releaseGroupIncludes = getDefaultReleaseGroupInclude(getIncludes());
        return releaseGroupIncludes;
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
     * @return the releaseVAIncludes
     */
    public ReleaseIncludesWs2 getReleaseVAIncludes() {
        if (releaseVAIncludes == null)
            releaseVAIncludes = getDefaultReleaseVAInclude(getIncludes());
        return releaseVAIncludes;
    }
    /**
     * @return the recordingIncludes
     */
    public RecordingIncludesWs2 getRecordingIncludes() {
        if (recordingIncludes == null)
            recordingIncludes = getDefaultRecordingInclude(getIncludes());
        return recordingIncludes;
    }
    /**
     * @return the workIncludes
     */
    public WorkIncludesWs2 getWorkIncludes() {
        if (workIncludes == null)
            workIncludes = getDefaultWorkInclude(getIncludes());
        return workIncludes;
    }
     /**
     * @param releaseGroupIncludes the releaseGroupIncludes to set
     */
    public void setReleaseGroupIncludes(ReleaseGroupIncludesWs2 releaseGroupIncludes) {
        this.releaseGroupIncludes = releaseGroupIncludes;
    }
    /**
     * @param releaseIncludes the releaseIncludes to set
     */
    public void setReleaseIncludes(ReleaseIncludesWs2 releaseIncludes) {
        this.releaseIncludes = releaseIncludes;
    }
    /**
     * @param releaseIncludes the releaseIncludes to set
     */
    public void setReleaseVAIncludes(ReleaseIncludesWs2 releaseVAIncludes) {
        this.releaseVAIncludes = releaseVAIncludes;
    }
    /**
     * @param recordingIncludes the recordingIncludes to set
     */
    public void setRecordingIncludes(RecordingIncludesWs2 recordingIncludes) {
        this.recordingIncludes = recordingIncludes;
    }
    /**
     * @param workIncludes the workIncludes to set
     */
    public void setWorkIncludes(WorkIncludesWs2 workIncludes) {
        this.workIncludes = workIncludes;
    }
    
    /**
     * @return the releaseGroupBrowseFilter
     */
    public ReleaseGroupBrowseFilterWs2 getReleaseGroupBrowseFilter() {
        if (releaseGroupBrowseFilter == null)
            releaseGroupBrowseFilter = getDefaultReleaseGroupBrowseFilter();
        return releaseGroupBrowseFilter;
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
     * @return the releaseVABrowseFilter
     */
    public ReleaseBrowseFilterWs2 getReleaseVABrowseFilter() {
        if (releaseVABrowseFilter == null)
            releaseVABrowseFilter = getDefaultReleaseVABrowseFilter();
        return releaseVABrowseFilter;
    }

    /**
     * @return the recordingBrowseFilter
     */
    public RecordingBrowseFilterWs2 getRecordingBrowseFilter() {
         if (recordingBrowseFilter == null)
            recordingBrowseFilter = getDefaultRecordingBrowseFilter();
        return recordingBrowseFilter;
    }

    /**
     * @return the workBrowseFilter
     */
    public WorkBrowseFilterWs2 getWorkBrowseFilter() {
        if (workBrowseFilter == null)
            workBrowseFilter = getDefaultWorkBrowseFilter();
        return workBrowseFilter;
    }
    /**
     * @param releaseBrowseFilter the releaseBrowseFilter to set
     */
    public void setReleaseBrowseFilter(ReleaseBrowseFilterWs2 releaseBrowseFilter) {
        this.releaseBrowseFilter = releaseBrowseFilter;
    }

    /**
     * @param releaseGroupBrowseFilter the releaseGroupBrowseFilter to set
     */
    public void setReleaseGroupBrowseFilter(ReleaseGroupBrowseFilterWs2 releaseGroupBrowseFilter) {
        this.releaseGroupBrowseFilter = releaseGroupBrowseFilter;
    }

    /**
     * @param releaseVABrowseFilter the releaseVABrowseFilter to set
     */
    public void setReleaseVABrowseFilter(ReleaseBrowseFilterWs2 releaseVABrowseFilter) {
        this.releaseVABrowseFilter = releaseVABrowseFilter;
    }
}
