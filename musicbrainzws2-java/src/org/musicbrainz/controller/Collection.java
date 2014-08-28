/*
 * A controller for the Collection Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.includes.CollectionIncludesWs2;
import org.musicbrainz.model.entity.CollectionWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.CollectionResultWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.musicbrainz.query.submission.CollectionReleasesHandlerWs2;
import org.musicbrainz.query.search.CollectionSearchWs2;
import org.musicbrainz.query.submission.SubmissionException;
import org.musicbrainz.webservice.AuthorizationException;
import org.musicbrainz.wsxml.element.Metadata;

public class Collection extends Controller{

    public Collection(){
        super();
        setIncluded(new CollectionIncludesWs2());
        
    }
   
    // -------------- Search  -------------------------------------------------//

  
    @Override
    protected CollectionSearchWs2 getSearch(){
        
        return (CollectionSearchWs2)super.getSearch();
    }
    public List <CollectionResultWs2> search() throws MBWS2Exception, AuthorizationException, Exception{

        setQueryWs(getQueryWs());
        setSearch(new CollectionSearchWs2(getQueryWs()));
        return getSearch().getResults();
    }
    
    // -------------- LookUp ------------------------------------------------//
         /**
     * @return the collectionIncludes
     */
    @Override
    public CollectionIncludesWs2 getIncludes() {
       return (CollectionIncludesWs2)super.getIncludes();
    }
    @Override
    protected  CollectionIncludesWs2 getDefaultIncludes(){
        
        CollectionIncludesWs2 inc =new CollectionIncludesWs2();
        
        inc.setUrlRelations(false);
        inc.setLabelRelations(false);
        inc.setArtistRelations(false);
        inc.setReleaseGroupRelations(false);
        inc.setReleaseRelations(false);
        inc.setRecordingRelations(false);
        inc.setWorkRelations(false);

        inc.setAnnotation(false);
        inc.setTags(false);
        inc.setRatings(false);
        inc.setUserTags(false);
        inc.setUserRatings(false);
        
        inc.setArtistCredits(true);
              
        inc.setReleases(true); 

        inc.setMedia(true);
        inc.setDiscids(false);

        return inc;
    }
    @Override
    protected CollectionIncludesWs2 getIncluded() {
        return (CollectionIncludesWs2)super.getIncluded();
    }
    /**
   * @return the collection
   */
    public CollectionWs2 getCollection() {
        return (CollectionWs2)getEntity();
    }
    protected CollectionIncludesWs2 getIncrementalInc(CollectionIncludesWs2 inc){

        inc = (CollectionIncludesWs2)super.getIncrementalInc(inc);
        if (getIncludes().isDiscids() && !getIncluded().isDiscids()) inc.setDiscids(true);
        if (getIncludes().isMedia() && !getIncluded().isMedia()) inc.setMedia(true);
        if (getIncludes().isReleases() && !getIncluded().isReleases()) inc.setReleases(true);
        
        return inc;
    }
    private boolean needsLookUp(CollectionIncludesWs2 inc){
        
        return (getCollection() == null ||
                   super.needsLookUp(inc) ||
                   inc.isReleases()||
                   inc.isDiscids() ||
                   inc.isMedia());
    }    
   public CollectionWs2 getComplete(CollectionWs2 collection) throws MBWS2Exception {
        if (collection == null) return null;
        if (collection.getId() == null) return collection;

        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(collection);
        
        return getComplete(collection.getId());
    }
    public final CollectionWs2 lookUp(CollectionWs2 collection) throws MBWS2Exception{
        if (collection == null) return null;
        if (collection.getId() == null) return collection;
        
        // save some field that come from search, but is missing in
        // lookUp http://tickets.musicbrainz.org/browse/MBS-3982
        setIncoming(collection);
        
        return lookUp(collection.getId());
    }
    public CollectionWs2 getComplete(String id) throws MBWS2Exception{
        
        setEntity(lookUp(id));
        //if (getIncludes().isReleases()) getFullReleaseList(); 
        
        return getCollection();
    }
    public final CollectionWs2 lookUp(String id) throws MBWS2Exception{

        CollectionIncludesWs2 inc = getIncrementalInc(new CollectionIncludesWs2());
        
        inc.setArtistRelations(false);// invalid request
        inc.setLabelRelations(false);// invalid request
        inc.setReleaseGroupRelations(false);// invalid request
        inc.setReleaseRelations(false);// invalid request
        inc.setRecordingRelations(false);// invalid request
        inc.setWorkRelations(false);// invalid request
        
        inc.setUrlRelations(false);// invalid request
        
        inc.setRecordingLevelRelations(false);// invalid request
        inc.setWorkLevelRelations(false);// invalid request

        inc.setAnnotation(false);// non sense
        inc.setTags(false);// invalid request
        inc.setRatings(false);// invalid request

        // Sanity check.
        // to avoid  exceptions.
        if(inc.isArtistCredits()) inc.setReleases(true); 
        if(inc.isMedia()) inc.setReleases(true); 
        if(inc.isDiscids()) inc.setReleases(true); 
        
        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));

            setEntity(getLookUp().getCollectionById(id,inc));
             
            setIncluded(inc);
        }
        return getCollection();
    }
    public final void addRelease(String id)throws MBWS2Exception{
        
        if (id==null || id.isEmpty()) throw new SubmissionException("Empty submission not allowed");
        List<String> releases= new ArrayList<String>();
        releases.add(id);
        addReleasesById(releases);
        
    }
    public final void removeRelease(String id)throws MBWS2Exception{
        
        if (id==null|| id.isEmpty()) throw new SubmissionException("Empty submission not allowed");
        List<String> releases= new ArrayList<String>();
        releases.add(id);
        removeReleasesById(releases);
        
    }
    public final void addRelease(ReleaseWs2 release)throws MBWS2Exception{
        
        if (release==null) throw new SubmissionException("Empty submission not allowed");
        List<String> releases= new ArrayList<String>();
        releases.add(release.getId());
        addReleasesById(releases);
        
    }
    public final void removeRelease(ReleaseWs2 release)throws MBWS2Exception{
        
        if (release==null) throw new SubmissionException("Empty submission not allowed");
        List<String> releases= new ArrayList<String>();
        releases.add(release.getId());
        removeReleasesById(releases);
        
    }
    public final void addReleases(List<ReleaseWs2> releases)throws MBWS2Exception{
        
        if (releases==null) throw new SubmissionException("Empty submission not allowed");
        List<String> releasesId= new ArrayList<String>();
        for (ReleaseWs2 release : releases)
        {
            releasesId.add(release.getId());
        }
        
        addReleasesById(releasesId);
        
    }
    public final void removeReleases(List<ReleaseWs2> releases)throws MBWS2Exception{
        
        if (releases==null) throw new SubmissionException("Empty submission not allowed");
        List<String> releasesId= new ArrayList<String>();
        for (ReleaseWs2 release : releases)
        {
            releasesId.add(release.getId());
        }
        
        removeReleasesById(releasesId);
        
    }
     public final void addReleasesById(List<String> releases) throws MBWS2Exception{
         
         CollectionReleasesHandlerWs2 query = new CollectionReleasesHandlerWs2(getQueryWs(), getCollection(), releases);
         Metadata md = query.put();
         getIncluded().setReleases(false);
         // You could also test the metadata.message if is OK or throw an exception
     }
     public final void removeReleasesById(List<String> releases) throws MBWS2Exception{
         
         CollectionReleasesHandlerWs2 query = new CollectionReleasesHandlerWs2(getQueryWs(), getCollection(), releases);
         Metadata md = query.delete();
         getIncluded().setReleases(false);
         // You could also test the metadata.message if is OK or throw an exception
     }
}
