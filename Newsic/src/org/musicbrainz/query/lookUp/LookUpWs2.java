package org.musicbrainz.query.lookUp;

import org.apache.http.HttpStatus;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.DiscTocFilterWs2;
import org.musicbrainz.webservice.RequestException;
import org.musicbrainz.webservice.ResponseException;

import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.utils.MbUtils;

import org.musicbrainz.includes.ArtistIncludesWs2;
import org.musicbrainz.includes.CollectionIncludesWs2;
import org.musicbrainz.includes.IncludesWs2;
import org.musicbrainz.includes.LabelIncludesWs2;
import org.musicbrainz.includes.RecordingIncludesWs2;
import org.musicbrainz.includes.ReleaseGroupIncludesWs2;
import org.musicbrainz.includes.ReleaseIncludesWs2;
import org.musicbrainz.includes.WorkIncludesWs2;
import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.model.PuidWs2;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.entity.CollectionWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;
import org.musicbrainz.query.QueryWs2;

/*  Implements Lookup query in WS2.

 * You can perform a lookup of an entity when you have the MBID 
 * for that entity: 

 * lookup:   /<ENTITY>/<MBID>?inc=<INC>
 * 
 * Use an Include to set the INC paramters.
 * 
 * Note that the number of linked entities returned is always limited to 25, 
 * if you need the remaining results, or more related info,
 * you will have to perform a browse request. 
 * 
 */

public class LookUpWs2 extends QueryWs2 {

    public LookUpWs2()
    {
            super();
    }

    /**
     * Custom WebService Constructor
     *  
     * @param ws An implementation of {@link WebService}
   */
    public LookUpWs2(WebService ws)
    {
        super(ws);
    }
    
     public EntityWs2 getById(String id, IncludesWs2 includes) throws MBWS2Exception
    {
        try {
                return lookup(id, includes);

        } catch (org.musicbrainz.MBWS2Exception ex) {

                log.error("Webservice returned: "+HttpStatus.SC_SERVICE_UNAVAILABLE+" message: " + ex.getMessage());
                throw ex;
        }

    }
    private EntityWs2 lookup(String id, IncludesWs2 includes) throws MBWS2Exception{
        
        String resType = MbUtils.extractResTypeFromURI(id);
        
         if (resType.equals(LABEL))
        {
            LabelIncludesWs2 inc = new LabelIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());
            inc.setArtistCredits(includes.isArtistCredits());
            
            if(inc.isArtistCredits()) inc.setReleases(true); // to avoid the artist credits exceptions.
            
            return getLabelById(id,inc);
        }
        else if (resType.equals(ARTIST))
        {
            ArtistIncludesWs2 inc = new ArtistIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());
            inc.setArtistCredits(includes.isArtistCredits());
            
            if(inc.isArtistCredits()) inc.setRecordings(true); // to avoid the artist credits exceptions.
            
            return getArtistById(id,inc);
        }
         else if (resType.equals(RELEASEGROUP))
        {
            ReleaseGroupIncludesWs2 inc = new ReleaseGroupIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());
            inc.setArtistCredits(includes.isArtistCredits());
            
            return getReleaseGroupById(id,inc);
        }
        else if (resType.equals(RELEASE))
        {
            ReleaseIncludesWs2 inc = new ReleaseIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());
            inc.setArtistCredits(includes.isArtistCredits());
            
            return getReleaseById(id,inc);
        }
        else if (resType.equals(RECORDING))
        {
            RecordingIncludesWs2 inc = new RecordingIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());
            inc.setArtistCredits(includes.isArtistCredits());
            
            return getRecordingById(id,inc);
        }
        else if (resType.equals(WORK))
        {
            WorkIncludesWs2 inc = new WorkIncludesWs2();
            
            inc.setArtistRelations(includes.isArtistRelations());
            inc.setLabelRelations(includes.isLabelRelations());
            inc.setRecordingRelations(includes.isRecordingRelations());
            inc.setReleaseGroupRelations(includes.isReleaseGroupRelations());
            inc.setReleaseRelations(includes.isReleaseRelations());
            inc.setUrlRelations(includes.isUrlRelations());
            inc.setWorkRelations(includes.isWorkRelations());

            inc.setArtistCredits(false); //non sense in work.

            
            return getWorkById(id,inc);
        }
        else if (resType.equals(COLLECTION))
        {
            CollectionIncludesWs2 inc = new CollectionIncludesWs2();
            
            inc.setArtistCredits(includes.isArtistCredits());
            
            return getCollectionById(id,inc);
        }
        return null;
    }

    public LabelWs2 getLabelById(String id, LabelIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, LABEL);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid label id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(LABEL, uuid, includes, null);
        if(md.getLabelWs2() == null)
                throw new ResponseException("Server didn't return label!");

        return md.getLabelWs2();
    }
    public ArtistWs2 getArtistById(String id, ArtistIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, ARTIST);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid artist id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(ARTIST, uuid, includes, null);
        if(md.getArtistWs2() == null)
                throw new ResponseException("Server didn't return artist!");

        return md.getArtistWs2();
    }
    
    public ReleaseGroupWs2 getReleaseGroupById(String id, ReleaseGroupIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, RELEASEGROUP);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid release-group id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(RELEASEGROUP, uuid, includes, null);
        if(md.getReleaseGroupWs2() == null)
                throw new ResponseException("Server didn't return release-group!");

        return md.getReleaseGroupWs2();
    }
    
    public ReleaseWs2 getReleaseById(String id, ReleaseIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, RELEASE);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid release id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(RELEASE, uuid, includes, null);
        if(md.getReleaseWs2() == null)
                throw new ResponseException("Server didn't return release!");

        return md.getReleaseWs2();
    }
    
    public RecordingWs2 getRecordingById(String id, RecordingIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, RECORDING);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid recording id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(RECORDING, uuid, includes, null);
        if(md.getRecordingWs2() == null)
                throw new ResponseException("Server didn't return recording!");

        return md.getRecordingWs2();
    }
        
    public WorkWs2 getWorkById(String id, WorkIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, WORK);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid work id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(WORK, uuid, includes, null);
        if(md.getWorkWs2() == null)
                throw new ResponseException("Server didn't return work!");

        return md.getWorkWs2();
    }
    public CollectionWs2 getCollectionById(String id, CollectionIncludesWs2 includes) throws MBWS2Exception
    {
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, COLLECTION);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid Collection id: " + e.getMessage(), e);
        }

        Metadata md = getFromWebService(COLLECTION, uuid, includes, null);
        if(md.getCollectionWs2() == null)
                throw new ResponseException("Server didn't return Collection!");

        return md.getCollectionWs2();
    }
    public PuidWs2 getPuidById(PuidWs2 puid, 
        RecordingIncludesWs2 includes)throws MBWS2Exception{
        
        String id = puid.getId();
        
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, PUID);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid Puid: " + e.getMessage(), e);
        }
        Metadata md = getFromWebService(PUID, uuid, includes, null);
        
        if (md.getPuidWs2()!=null) return md.getPuidWs2();
        
        puid.setRecordingList(md.getRecordingListWs2());
        return puid;
    }
    public DiscWs2 getDiscById(DiscWs2 disc, 
            ReleaseIncludesWs2 includes, DiscTocFilterWs2 filter)throws MBWS2Exception{
        
        String id = disc.getDiscId();
        String toc= disc.getToc();
        
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, DISC);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid disc id: " + e.getMessage(), e);
        }
        Metadata md = getFromWebService(DISCID, uuid, includes, filter);
       
        if (md.getDiscWs2()!=null) return md.getDiscWs2();
        
        disc.setReleaseList(md.getReleaseListWs2());
        return disc;
    }
    public ReleaseListWs2 getReleaseListByDiscId(String id, String toc, 
            ReleaseIncludesWs2 includes, DiscTocFilterWs2 filter)throws MBWS2Exception{
        
        String uuid;
        try {
                uuid = MbUtils.extractUuid(id, DISC);
        }
        catch(IllegalArgumentException e) {
                throw new RequestException("Invalid disc id: " + e.getMessage(), e);
        }
        Metadata md = getFromWebService(DISCID, uuid, includes, filter);
        if(md.getReleaseListWs2() == null)
                throw new ResponseException("Server didn't return releases!");
        
        if (md.getDiscWs2()!=null) return md.getDiscWs2().getReleaseList();
        else return md.getReleaseListWs2();
    }
}
