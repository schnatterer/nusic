/*
 * A controller for the Work Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.searchfilter.WorkSearchFilterWs2;
import org.musicbrainz.includes.WorkIncludesWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.searchresult.WorkResultWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.search.WorkSearchWs2;

public class Work extends Controller{
    
    public Work(){
    
        super();
        setIncluded(new WorkIncludesWs2());
    }

    
// -------------- Search  -------------------------------------------------//
    
    @Override
    public WorkSearchFilterWs2 getSearchFilter(){
        
        return (WorkSearchFilterWs2)super.getSearchFilter();
    }
        
    @Override
    protected WorkSearchWs2 getSearch(){
        
        return (WorkSearchWs2)super.getSearch();
    }
    @Override
    protected WorkSearchFilterWs2 getDefaultSearchFilter(){
        
        WorkSearchFilterWs2 f = new WorkSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
        
        return f;

    }
    @Override
    public void search(String searchText){

        initSearch(searchText);
        setSearch(new WorkSearchWs2(getQueryWs(),getSearchFilter()));
    }
    
    public List <WorkResultWs2> getFullSearchResultList() {

        return getSearch().getFullList();

    }
    public List <WorkResultWs2> getFirstSearchResultPage() {

        return getSearch().getFirstPage();
    }
    public List <WorkResultWs2> getNextSearchResultPage() {

        return getSearch().getNextPage();
    }
    
   // -------------- LookUp -------------------------------------------------//
    
    @Override
    public WorkIncludesWs2 getIncludes(){
        return (WorkIncludesWs2)super.getIncludes();
    }
    
    @Override
    protected WorkIncludesWs2 getDefaultIncludes(){
        
        WorkIncludesWs2 inc =new WorkIncludesWs2();
        
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
        
        inc.setAliases(true);

        return inc;
    }
    /**
     * @return the Included
   */
    @Override
    protected WorkIncludesWs2 getIncluded() {
        return (WorkIncludesWs2)super.getIncluded();
    }
    private WorkWs2 getWork() {
        return (WorkWs2)getEntity();
    }
    
    public WorkWs2 getComplete(WorkWs2 work) throws MBWS2Exception {
        if (work == null) return null;
        if (work.getId() == null) return work;
                                                
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(work);
        
        return getComplete(work.getId());
    }
    public WorkWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        
        return getWork();
    }
    
    public WorkWs2 lookUp(WorkWs2 work) throws MBWS2Exception{

        if (work == null) return null;
        if (work.getId() == null) return work;
                                                        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(work);
        
        return lookUp(work.getId());
    }


    protected WorkIncludesWs2 getIncrementalInc(WorkIncludesWs2 inc){

        inc = (WorkIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isAliases() && !getIncluded().isAliases()) inc.setAliases(true);
        
        return inc;
    }
    private boolean needsLookUp(WorkIncludesWs2 inc){
        
        return (  getWork() == null ||
                     super.needsLookUp(inc) ||
                     inc.isAliases());
    }

    public WorkWs2 lookUp(String id) throws MBWS2Exception{
 
        WorkIncludesWs2 inc = getIncrementalInc(new WorkIncludesWs2());

        // LookUp is limited by 25 linked entities, to be sure
        // is better perform a Browse (you could also get first 25
        // at lookUp time just hiitting workInclude.setReleases(true), 
        // check if there could be  more releases left and in case perform 
        // the Browse... worry about sort order...).
        
        // Sanity check.
        inc.setArtistCredits(false); // invalid request
        inc.setRecordingLevelRelations(false);// invalid request
        inc.setWorkLevelRelations(false);// invalid request
  
        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            WorkWs2 transit = null;
            transit = getLookUp().getWorkById(id, inc);
             

            if (transit ==null) return null;
            
            if (getWork() == null || !getWork().equals(transit)) // work is changed.
            {
                setEntity(transit);
                setIncluded(inc);
            }
            else 
            {
                updateEntity(getWork(),transit,inc);
                if (inc.isAliases()) {
                    getWork().setAliases(transit.getAliases());
                    getIncluded().setAliases(true);
                }
            }
        }
        if (inc.isAnnotation()) loadAnnotation(getWork());

        return getWork();
    }

}
