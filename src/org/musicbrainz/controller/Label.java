/*
 * A controller for the Label Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.ReleaseBrowseFilterWs2;
import org.musicbrainz.filter.searchfilter.LabelSearchFilterWs2;
import org.musicbrainz.includes.LabelIncludesWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.LabelResultWs2;
import org.musicbrainz.query.browse.ReleaseBrowseWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.LabelSearchWs2;

public class Label extends Controller{

    private int browseLimit = 100;
    private ReleaseBrowseWs2 releaseBrowse;
    private ReleaseIncludesWs2 releaseIncludes;
    private ReleaseBrowseFilterWs2 releaseBrowseFilter;
    
    public Label(){
        super();
        setIncluded(new LabelIncludesWs2());
    }
       
    
    // -------------- Search  -------------------------------------------------//
    
    @Override
    public LabelSearchFilterWs2 getSearchFilter(){
        
        return (LabelSearchFilterWs2)super.getSearchFilter();
    }
        
    @Override
    protected LabelSearchWs2 getSearch(){
        
        return (LabelSearchWs2)super.getSearch();
    }
    
    @Override
    protected LabelSearchFilterWs2 getDefaultSearchFilter(){
        
        LabelSearchFilterWs2 f = new LabelSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;

    }
    @Override
    public void search(String searchText){

        initSearch(searchText);
        setSearch(new LabelSearchWs2(getQueryWs(),getSearchFilter()));
    }
    
    public List <LabelResultWs2> getFullSearchResultList() {

        return getSearch().getFullList();

    }
    public List <LabelResultWs2> getFirstSearchResultPage() {

        return getSearch().getFirstPage();
    }
    public List <LabelResultWs2> getNextSearchResultPage() {

        return getSearch().getNextPage();
    }
       
    // -------------- LookUp ------------------------------------------------//
    
        /**
     * @return the labelIncludes
     */
    @Override
    public LabelIncludesWs2 getIncludes() {
       return (LabelIncludesWs2)super.getIncludes();
    }

    @Override
    protected  LabelIncludesWs2 getDefaultIncludes(){
        
        LabelIncludesWs2 inc =new LabelIncludesWs2();
        
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
                
        inc.setReleases(true); 
        
        inc.setArtistCredits(true);

        inc.setMedia(false);
        inc.setDiscids(false);

        return inc;
    }
    @Override
    protected LabelIncludesWs2 getIncluded() {
        return (LabelIncludesWs2)super.getIncluded();
    }
    private LabelWs2 getLabel() {
        return (LabelWs2)getEntity();
    }
    protected LabelIncludesWs2 getIncrementalInc(LabelIncludesWs2 inc){

        inc = (LabelIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isAliases() && !getIncluded().isAliases()) inc.setAliases(true);
        
        return inc;
    }
    private boolean needsLookUp(LabelIncludesWs2 inc){
        
        return (  getLabel() == null ||
                     super.needsLookUp(inc) ||
                     inc.isAliases());
    }
    
    public LabelWs2 getComplete(LabelWs2 label) throws MBWS2Exception {
        if (label == null) return null;
        if (label.getId() == null) return label;

        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(label);
        
        return getComplete(label.getId());
    }
    public final LabelWs2 lookUp(LabelWs2 label) throws MBWS2Exception{
        if (label == null) return null;
        if (label.getId() == null) return label;
        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(label);
        
        return lookUp(label.getId());
    }
    public LabelWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        if (getIncludes().isReleases()) getFullReleaseList();
        
        return getLabel();
    }
    public final LabelWs2 lookUp(String id) throws MBWS2Exception{

        LabelIncludesWs2 inc = getIncrementalInc(new LabelIncludesWs2());
        
        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting labelInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse).

        inc.setRecordingLevelRelations(false);// invalid request
        inc.setWorkLevelRelations(false);// invalid request
        
        inc.setReleases(false); 
        // the following inc params are meaningless if not inc.isRelease().

        inc.setMedia(false);
        inc.setDiscids(false);

        
        // Sanity check.
        // to avoid the artist credits exceptions.
        if(inc.isArtistCredits()) inc.setReleases(true); 

        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            LabelWs2 transit = null;
            transit = getLookUp().getLabelById(id, inc);

            if (transit ==null) return null;
            if (getLabel() == null || !getLabel().equals(transit)) // label is changed.
            {
                if (getIncoming() != null) {
                    
                    // save some field that come from search, but is missing in
                   // lookUp http://tickets.musicbrainz.org/browse/MBS-3982

                   if (transit.getDisambiguation() == null ||
                        transit.getDisambiguation().isEmpty()) {
                           
                           String dis = ((LabelWs2)getIncoming()).getDisambiguation();
                           transit.setDisambiguation(dis);
                   }
                }
                
                setEntity(transit);
                setIncluded(inc);
                releaseBrowse = null;
            }
            else 
            {
                updateEntity(getLabel(),transit, inc);
                if (inc.isAliases()) 
                {
                    getLabel().setAliases(transit.getAliases());
                    getIncluded().setAliases(true);
                }
            }
        }
        if (inc.isAnnotation()) loadAnnotation(getLabel());
        
        initBrowses();

        return getLabel();
    }
    // ------------- Browse -------------------------------------------------//
    
    private void initBrowses(){
        
        if (getIncludes().isReleases() && releaseBrowse == null)
        {
            ReleaseIncludesWs2 relInc = getReleaseIncludes();
            relInc.setRecordingLevelRelations(false);// invalid request
            relInc.setWorkLevelRelations(false);// invalid request
            
            ReleaseBrowseFilterWs2 f = getReleaseBrowseFilter();

            f.setRelatedEntity(LABEL);
            f.setRelatedId(getLabel().getId());

            releaseBrowse = new ReleaseBrowseWs2(getQueryWs(),f,relInc);

            getIncluded().setReleases(true);
        }
    }
     public List <ReleaseWs2> getFullReleaseList() {
        
         if (getLabel() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         if (!hasMoreReleases()) return getLabel().getReleases();
         
         List <ReleaseWs2> list = releaseBrowse.getFullList();
        
        for (ReleaseWs2 rel : list)
         {
             getLabel().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getFirstReleaseListPage() {
        
         if (getLabel() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         
         List <ReleaseWs2> list = releaseBrowse.getFirstPage();
        
        for (ReleaseWs2 rel : list)
         {
             getLabel().addRelease(rel);
         }
        
        return list;
    }
    public List <ReleaseWs2> getNextReleaseListPage() {

         if (getLabel() == null) return null;
         getIncludes().setReleases(true);
         if (releaseBrowse == null ) initBrowses();
         if (releaseBrowse == null ) return null;
         if (!hasMoreReleases()) return new ArrayList<ReleaseWs2> ();
         
        List <ReleaseWs2> list = releaseBrowse.getNextPage();
        
        for (ReleaseWs2 rel : list)
         {
             getLabel().addRelease(rel);
         }
        
        return list;
    }
    public boolean hasMoreReleases(){
        if (getLabel() == null) return true;
        if (releaseBrowse == null ) return true;
        return releaseBrowse.hasMore();
    }

    private ReleaseIncludesWs2 getDefaultReleaseInclude(LabelIncludesWs2 labelinc){
        
        ReleaseIncludesWs2 inc =new ReleaseIncludesWs2();

        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setReleaseGroups(true);

        inc.setMedia(false);
        inc.setRecordings(false);
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);

        if (labelinc == null) return inc;
        
        if (labelinc.isDiscids()) inc.setDiscids(true);

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
