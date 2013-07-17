/*
 * A controller for the Recording Entity.
 * 
 */
package org.musicbrainz.controller;


import org.musicbrainz.query.lookUp.LookUpWs2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.includes.RecordingIncludesWs2;
import org.musicbrainz.model.PuidWs2;

public class Puid extends Controller{

   private Log log = LogFactory.getLog(Puid.class);
   private PuidWs2 puidWs2;
   
   
   public Puid(){
    
        super();
        setIncluded(new RecordingIncludesWs2());
    }
    // -------------- Search  -------------------------------------------------//
 
    // -------------- LookUp -------------------------------------------------//
    /**
     * @return the releaseIncludes
     */
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
        
        inc.setWorkLevelRelations(false);


        return inc;
    }
    @Override
    protected RecordingIncludesWs2 getIncluded() {
        return (RecordingIncludesWs2)super.getIncluded();
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
        
        return (  puidWs2 == null ||
                     super.needsLookUp(inc) ||
                     inc.isArtistCredits() ||
                     inc.isPuids() ||
                     inc.isIsrcs());
    }
    public PuidWs2 lookUp(PuidWs2 puid) throws MBWS2Exception{

        if (puid == null || puid.getId() == null || puid.getId().isEmpty()) 
            throw new MBWS2Exception("invalid request");
        
        PuidWs2 incoming =puid;

        puidWs2= lookUp(puid.getId());
        
        if (puidWs2== null)return incoming;
        return puidWs2;
    }

    public PuidWs2 lookUp(String id) throws MBWS2Exception{

        if (id==null || id.isEmpty()){
            throw new MBWS2Exception("invalid request");
        }
        puidWs2 = new PuidWs2();
        puidWs2.setId(id);
        
        RecordingIncludesWs2 inc = getIncrementalInc(new RecordingIncludesWs2());
       
        // Sanity check.
        inc.setRecordingLevelRelations(false); // invalid request
        inc.setWorkLevelRelations(false); // invalid request

        if (needsLookUp(inc))
        {    
            setLookUp(new LookUpWs2(getQueryWs()));
           
            puidWs2= getLookUp().getPuidById(puidWs2, inc);
            
            setIncluded(inc);
        }
      
        return puidWs2;
    }
}
