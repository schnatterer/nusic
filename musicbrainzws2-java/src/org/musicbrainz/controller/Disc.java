/*
 * A controller for the Release Entity.
 * 
 */
package org.musicbrainz.controller;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.query.lookUp.LookUpWs2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.discid.DiscIdException;
import org.musicbrainz.discid.DiscInfo;
import org.musicbrainz.discid.DiscInfo.TrackInfo;
import org.musicbrainz.filter.DiscTocFilterWs2;
import org.musicbrainz.model.DiscTrackWs2;
import org.musicbrainz.webservice.ResourceNotFoundException;

public class Disc extends Controller{

   private Log log = LogFactory.getLog(Disc.class);
   
   
   public Disc(){
    
        super();
        setIncluded(new ReleaseIncludesWs2());
    }
    /**
     * @return the discWs2
     */
    public DiscWs2 getDiscWs2() {
        return (DiscWs2)getEntity();
    }

    /**
     * @param discWs2 the discWs2 to set
     */
    public void setDiscWs2(DiscWs2 discWs2) {
        setEntity(discWs2);
    }
    // -------------- Search  -------------------------------------------------//
 
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
        
        inc.setTags(false);
        inc.setRatings(false);
        inc.setUserTags(false);
        inc.setUserRatings(false);
        
        inc.setArtistCredits(true);
        inc.setLabel(true);
        inc.setReleaseGroups(true);
        inc.setRecordings(true);
         
        inc.setWorkLevelRelations(false);
        inc.setRecordingLevelRelations(false);

        return inc;
    }
    @Override
    protected ReleaseIncludesWs2 getIncluded() {
        return (ReleaseIncludesWs2)super.getIncluded();
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
    private boolean needsLookUp(DiscWs2 disc, ReleaseIncludesWs2 inc){
        

        return (getDiscWs2()==null ||
                   super.needsLookUp(inc) ||
                   inc.isLabel()||
                   inc.isReleaseGroups()||
                   inc.isArtistCredits()||
                   inc.isMedia()||
                   inc.isRecordings()||
                   inc.isDiscids()
                );
    }
    public DiscWs2 lookUp(String id, String toc) throws MBWS2Exception{

        if ((id==null || id.isEmpty()) && (toc==null || toc.isEmpty())){
            throw new MBWS2Exception("invalid request");
        }
        DiscWs2 disc  = new DiscWs2();
        disc.setDiscId(id);
        disc.setToc(toc);
        
        return lookUp(disc);
    }
    public DiscWs2 lookUp(String drive) throws MBWS2Exception{

        DiscWs2 disc  = readFromDisk(drive);
        
        return lookUp(disc);
    }
    
    public DiscWs2 lookUp(DiscWs2 disc) throws MBWS2Exception{

        if (disc == null) return null;
        
        if ((disc.getDiscId() == null || disc.getDiscId().isEmpty()) &&  
             (disc.getToc() == null|| disc.getToc().isEmpty()) &&
             (disc.getTracks() == null|| disc.getTracks().isEmpty())) {
                throw new MBWS2Exception("Invalid request");
        }
        
        if ((disc.getToc() == null|| disc.getToc().isEmpty()) &&
               disc.getTracks() != null && !disc.getTracks().isEmpty()){
            
            DiscWs2 transit = calcDiscIdFromTrackList(disc);
            disc.setToc(transit.getToc());
            
            if (disc.getDiscId()== null || disc.getDiscId().isEmpty()){
                disc.setDiscId(transit.getDiscId());
            }
            else if (transit.getDiscId() != null && 
                       !transit.getDiscId().equals( disc.getDiscId())){
                log.warn("libdiscId changed discId");
            };
        }
        else if ((disc.getTracks() == null|| disc.getTracks().isEmpty()) &&
               disc.getToc() != null && !disc.getToc().isEmpty()) {
            
            DiscWs2 transit = calcDiscIdFromToc(disc.getToc());
            disc.setTracks(transit.getTracks());
            
            if (disc.getDiscId()== null || disc.getDiscId().isEmpty()){
                disc.setDiscId(transit.getDiscId());
            }
            else if (transit.getDiscId() != null && 
                       !transit.getDiscId().equals( disc.getDiscId())){
                log.warn("libdiscId changed discId");
            };
            
        }
        else if (disc.getTracks() != null && !disc.getTracks().isEmpty()&&
                    disc.getToc() != null && !disc.getToc().isEmpty()){
            
            DiscWs2 transit = new DiscWs2();
            transit.setTracks(disc.getTracks());
            transit = calcDiscIdFromTrackList(transit);
            
            if (!transit.getToc().equals(disc.getToc()))
                throw new MBWS2Exception("Invalid request: non corresponding toc and trackList");
            
            if (disc.getDiscId()== null || disc.getDiscId().isEmpty()){
                disc.setDiscId(transit.getDiscId());
            }
            else if (transit.getDiscId() != null && 
                       !transit.getDiscId().equals( disc.getDiscId())){
                log.warn("libdiscId changed discId");
            };
        }
        
        ReleaseIncludesWs2 inc = getIncrementalInc(new ReleaseIncludesWs2());
       
        // Sanity check.
               
        if (inc.isMedia() || inc.isDiscids() ||inc.isRecordings())
        {
            inc.setRecordings(true);
        }
                
        inc.setRecordingLevelRelations(false);// invalid request
        inc.setWorkLevelRelations(false);// invalid request
        inc.setTags(false);// invalid request
        inc.setRatings(false);// invalid request
        inc.setUserTags(false);// invalid request
        inc.setUserRatings(false);// invalid request
        inc.setDiscids(false);// invalid request
        inc.setMedia(false);// invalid request

        if (needsLookUp(disc, inc)){    
            setLookUp(new LookUpWs2(getQueryWs()));
            
            DiscTocFilterWs2 tocFilter = new DiscTocFilterWs2();
            tocFilter.setToc(disc.getToc());
            try{
                disc = getLookUp().getDiscById(disc, inc, tocFilter);
            }catch (ResourceNotFoundException ex){
                disc.getReleaseList().getReleases().clear();
            }
                setDiscWs2(disc);
                setIncluded(inc);
            
            return getDiscWs2();
        }
      
        return getDiscWs2();
    }
    
    // --------------- LibDiscId -----------------------------------//
    private List<DiscTrackWs2> copyAndOrderTrackList(List<DiscTrackWs2> in) throws MBWS2Exception{
    
        List<DiscTrackWs2> ordered = new ArrayList<DiscTrackWs2>(in.size());
        
        for (int i=0; i<in.size();i++)
        {
            ordered.add(null);
        }
        for (DiscTrackWs2 t : in)
        {
            if (t.getTracknum() <1 ) 
                throw new MBWS2Exception("invalid trackList");
            if (t.getTracknum() > in.size()+1) 
                throw new MBWS2Exception("invalid trackList");
            if (ordered.get(t.getTracknum()-1) != null) 
                throw new MBWS2Exception("invalid trackList");
                
                ordered.set(t.getTracknum()-1, t);
            
        }
        return ordered;
    }
    private DiscWs2 replaceTrackList(DiscWs2 disc, List<DiscTrackWs2> tracklist){
        
        disc.getTracks().clear();
        for (DiscTrackWs2 t : tracklist)
        {
            disc.getTracks().add(t);
        }
        return disc;
    }
    private DiscWs2 readFromDisk(String drive) throws MBWS2Exception {
        
        DiscWs2 disc = new DiscWs2();
        
        DiscInfo discInfo;
        try {
            discInfo = DiscInfo.read(drive);
            log.debug("Disc Information:\n" + discInfo.toString());
            disc.setDiscId(discInfo.discid);
            disc.setSectors(discInfo.sectors);
            disc.setToc(discInfo.toc);
            for (TrackInfo t : discInfo.trackList)
            {
                disc.addTrack(new DiscTrackWs2(t.num, t.offset, t.length));
            }

        } catch (DiscIdException ex) {
            throw new MBWS2Exception(ex);
        }
        return disc;

    }
    private DiscWs2 calcDiscIdFromTrackList(DiscWs2 disc) throws MBWS2Exception{
        
        DiscInfo discInfo= calcDiscIdFromOffsets(calcOffsets(disc));
        disc.setDiscId(discInfo.discid);
        disc.setSectors(discInfo.sectors);
        disc.setToc(discInfo.toc);
        
        return disc;
    }
    private DiscInfo calcDiscIdFromOffsets (int[] offsets) throws MBWS2Exception{
        
        DiscInfo discInfo;
        try {
            discInfo = DiscInfo.fromOffsets(offsets);
        } catch (DiscIdException ex) {
            throw new MBWS2Exception(ex);
        }
        log.debug("Disc Information:\n" + discInfo.toString());
        return discInfo;
        
    }

    private int[] calcOffsets(DiscWs2 disc) throws MBWS2Exception{
        
        // Sanity checks on tracks
        if (disc.getTracks() == null ||disc.getTracks().isEmpty())
            throw new MBWS2Exception("Invalid track list");
        
        List<DiscTrackWs2> ordered = copyAndOrderTrackList(disc.getTracks());
       
        disc= replaceTrackList(disc, ordered);
        
        int totalLength= disc.getTracks().get(0).getOffset();

        int[] offsets = new int[disc.getTracks().size()+1];
        for (DiscTrackWs2 t : disc.getTracks())
        {
            t.setOffset(totalLength);
            
            offsets[t.getTracknum()]=t.getOffset();
            totalLength=totalLength+t.getLength();
            
        }
        offsets[0]=totalLength;
        return offsets;
    }
    private DiscWs2 calcDiscIdFromToc(String toc) throws MBWS2Exception{
        
         DiscWs2 disc = new DiscWs2();

         String [] tocArray = toc.split(" ");
         
         try{
             int firstTrack= Integer.parseInt(tocArray[0]);
             int lastTrack= Integer.parseInt(tocArray[1]);
             int sectors= Integer.parseInt(tocArray[2]);
             int [] offset= new int[tocArray.length-2];

             for (int i=2; i<tocArray.length; i++ ){
                  offset[i-2]= Integer.parseInt(tocArray[i]);
             }
             DiscInfo discInfo = calcDiscIdFromOffsets(offset);
             disc.setDiscId(discInfo.discid);
             disc.setSectors(discInfo.sectors);
             disc.setToc(discInfo.toc);
             for (TrackInfo t : discInfo.trackList)
            {
                disc.addTrack(new DiscTrackWs2(t.num, t.offset, t.length));
            }
         } catch (NumberFormatException ex){
             throw new MBWS2Exception("Invalid toc");
         }
         
         return disc;
    }

}
