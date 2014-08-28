/*
 * A controller for the Release Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.List;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.searchfilter.ReleaseSearchFilterWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.ReleaseSearchWs2;

public class Release extends Controller{

   public Release(){
    
        this(null, null, null);
    }
    
    /**
     * Creates a Release with a custom user agent string.
     * 
     * @param applicationName
     *            custom application name used in user agent string. If
     *            <code>null</code>, the default user agent string is used
     * @param applicationVersion
     *            custom application version used in user agent string
     * @param applicationContact
     *            contact URL or author email used in user agent string
     */
    public Release(String applicationName, String applicationVersion,
            String applicationContact) {
        super(applicationName, applicationVersion, applicationContact);
        setIncluded(new ReleaseIncludesWs2());
    }
    
    // -------------- Search  -------------------------------------------------//
    
    @Override
    public ReleaseSearchFilterWs2 getSearchFilter(){
        
        return (ReleaseSearchFilterWs2)super.getSearchFilter();
    }
        
    @Override
    protected ReleaseSearchWs2 getSearch(){
        
        return (ReleaseSearchWs2)super.getSearch();
    }
    @Override
    protected ReleaseSearchFilterWs2 getDefaultSearchFilter(){
        
        ReleaseSearchFilterWs2 f = new ReleaseSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;

    }
    @Override
    public void search(String searchText){

        initSearch(searchText);
        setSearch(new ReleaseSearchWs2(getQueryWs(),getSearchFilter()));
        
    }
    public List <ReleaseResultWs2> getFullSearchResultList() throws MBWS2Exception {

        return getSearch().getFullList();

    }
    public List <ReleaseResultWs2> getFirstSearchResultPage() throws MBWS2Exception {

        return getSearch().getFirstPage();
    }
    public List <ReleaseResultWs2> getNextSearchResultPage() throws MBWS2Exception {

        return getSearch().getNextPage();
    }
    
    // -------------- LookUp -------------------------------------------------//
    /**
     * @return the releaseIncludes
     */
    @Override
    public ReleaseIncludesWs2 getIncludes() {
        return (ReleaseIncludesWs2)super.getIncludes();
    }
    @Override
    protected  ReleaseIncludesWs2 getDefaultIncludes(){
        
        ReleaseIncludesWs2 inc =new ReleaseIncludesWs2();
        
        inc.setUrlRelations(true);
        inc.setLabelRelations(true);
        inc.setArtistRelations(true);
        inc.setReleaseRelations(true);
        inc.setReleaseRelations(true);
        inc.setRecordingRelations(true);
        inc.setWorkRelations(true);
        
        inc.setTags(true);
        inc.setRatings(true);
        inc.setUserTags(false);
        inc.setUserRatings(false);
        
        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setReleaseGroups(true);
        inc.setMedia(true);
        inc.setRecordings(true);
        inc.setDiscids(true);
         
        inc.setWorkLevelRelations(true);
        inc.setRecordingLevelRelations(true);

        return inc;
    }
    @Override
    protected ReleaseIncludesWs2 getIncluded() {
        return (ReleaseIncludesWs2)super.getIncluded();
    }
    private ReleaseWs2 getRelease() {
        return (ReleaseWs2)getEntity();
    }

    public ReleaseWs2 getComplete(ReleaseWs2 release) throws MBWS2Exception {
        if (release == null) return null;
        if (release.getId() == null) return release;
                
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(release);
        
        return getComplete(release.getId());
    }
    public ReleaseWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        
        return getRelease();
    }
    
    public ReleaseWs2 lookUp(ReleaseWs2 release) throws MBWS2Exception{

        if (release == null) return null;
        if (release.getId() == null) return release;
                        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(release);
        
        return lookUp(release.getId());
    }
    protected ReleaseIncludesWs2 getIncrementalInc(ReleaseIncludesWs2 inc){

        inc = (ReleaseIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isLabel() && !getIncluded().isLabel()) inc.setLabel(true);
        if (getIncludes().isReleaseGroups() && !getIncluded().isReleaseGroups()) inc.setReleaseGroups(true);
        if (getIncludes().isMedia() && !getIncluded().isMedia()) inc.setMedia(true);
        if (getIncludes().isRecordings() && !getIncluded().isRecordings()) inc.setRecordings(true);
        if (getIncludes().isDiscids() && !getIncluded().isDiscids()) inc.setDiscids(true);

        return inc;
    }
    private boolean needsLookUp(ReleaseIncludesWs2 inc){
        
        return (  getRelease() == null ||
                     super.needsLookUp(inc) ||
                     inc.isLabel()||
                     inc.isReleaseGroups()||
                     inc.isArtistCredits()||
                     inc.isMedia()||
                     inc.isRecordings()||
                     inc.isDiscids());
    }
    public ReleaseWs2 lookUp(String id) throws MBWS2Exception{

        ReleaseIncludesWs2 inc = getIncrementalInc(new ReleaseIncludesWs2());
        
        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting releaseInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse).
        
        // To avoid multiples API calls.
        
        if (inc.isMedia() || inc.isDiscids() ||inc.isRecordings())
        {
            inc.setMedia(true);
            inc.setDiscids(true);
            inc.setRecordings(true);
        }
        
        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            ReleaseWs2 transit = null;
            transit = getLookUp().getReleaseById(id, inc);

            if (transit ==null) return null;
            if (getRelease() == null || !getRelease().equals(transit)) // release is changed.
            {
                setEntity(transit);
                setIncluded(inc);
            }
            else
            {
                updateEntity(getRelease(),transit, inc);
                if (inc.isArtistCredits()) 
                {
                    getRelease().setArtistCredit(transit.getArtistCredit());
                    getIncluded().setArtistCredits(true);
                }
                if (inc.isMedia()) 
                {
                    getRelease().setMediumList(transit.getMediumList());
                    getIncluded().setMedia(true);
                    getIncluded().setDiscids(true);
                    getIncluded().setRecordings(true);
                }
                 if (inc.isLabel()) 
                {
                    getRelease().setLabelInfoList(transit.getLabelInfoList());
                    getIncluded().setLabel(true);
                }
                  if (inc.isReleaseGroups()) 
                {
                    getRelease().setReleaseGroup(transit.getReleaseGroup());
                    getIncluded().setReleaseGroups(true);
                }
            }
        }
        if (inc.isAnnotation()) loadAnnotation(getRelease());
       
        return getRelease();
    }
}
