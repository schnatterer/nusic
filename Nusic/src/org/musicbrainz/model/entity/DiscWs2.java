package org.musicbrainz.model.entity;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.musicbrainz.controller.Disc;
import org.musicbrainz.model.DiscTrackWs2;
import org.musicbrainz.model.IsrcWs2;
import org.musicbrainz.model.MediumWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;


/**
 * <p>Represents an Audio CD.</p>
 * 
 * <p>This class represents an Audio CD. A disc can have an ID (the
 * MusicBrainz DiscID), which is calculated from the CD's table of
 * contents (TOC). There may also be data from the TOC like the length
 * of the disc in sectors, as well as position and length of the tracks.</p>
 * 
 * <p>Note that different TOCs, maybe due to different pressings, lead to
 * different DiscIDs. Conversely, if two different discs have the same
 * TOC, they also have the same DiscID (which is unlikely but not
 * impossible). DiscIDs are always 28 characters long and look like this:
 * <code>J68I_CDcUFdCRCIbHSEbTBCbooA-</code>. Sometimes they are also
 * referred to as CDIndex IDs.</p>
 */
public class DiscWs2 extends EntityWs2 {

    private Log log = LogFactory.getLog(IsrcWs2.class);
     /**
     * The medium this disk is in.
     */
    private MediumWs2 medium;
        /**
     * The length in sectors
     */
    private Integer sectors;

    /**
     * The 28-character disc id
     */
    private String discId;

       /**
     * The String rappresentation of the CD TOC.
     */
    private String toc;

    /**
     * The disc's Tracks
     */
    private List<DiscTrackWs2> tracks = new ArrayList<DiscTrackWs2>();
     /**
     * The disc's Releases
     */
    private ReleaseListWs2 releaseList= new ReleaseListWs2();

    /**
     * Default constructor
     */
    public DiscWs2() {
            tracks = new ArrayList<DiscTrackWs2>();
    }
    /**
     * @return a string containing a 28-character DiscID 
     */
    public String getDiscId() {
            return discId;
    }
    /**
     * @param discId the discId to set
     */
    public void setDiscId(String discId) {
            this.discId = discId;
    }
    /**
     * @return the sectors
     */
    public Integer getSectors() {
            return sectors;
    }
    /**
     * @param sectors the sectors to set
     */
    public void setSectors(Integer sectors) {
            this.sectors = sectors;
    }
    /**
     * @return the toc
     */
    public String getToc() {
        return toc;
    }

    /**
     * @param toc the toc to set
     */
    public void setToc(String toc) {
        this.toc = toc;
    }
    /**
     * @return the medium type
     */
    public MediumWs2 getMedium() {
        return medium;
    }
    /**
     * @param medium the medium type to set
     */
    public void setMedium(MediumWs2 medium) {
        this.medium = medium;
    }
    public String getMediumStr(){
        
        if (getMedium()==null) return "";
        
        String out =  getMedium().getFormat()+" "
                +String.valueOf(getMedium().getPosition());
        
        return out;
    }
    
    /**
     * <p>Returns the sector offset and length of this disc.</p>
     *
     * <p>This method returns a list of Disc.Tracks containing the track
     * offset and length in sectors for all tracks on this disc.
     * The track offset is measured from the beginning of the disc,
     * the length is relative to the track's offset. Note that the
     * leadout track is <em>not</em> included.</p>
     * 
     * @return a list of Disc.Tracks that contain offset and length as Integers
     */
    public List<DiscTrackWs2> getTracks(){
            if (tracks == null) tracks = new ArrayList<DiscTrackWs2>();
            return tracks;
    }
    /**
     * @param tracks the tracks to set
     */
    public void setTracks(List<DiscTrackWs2> tracks) {
        if (tracks == null) tracks = new ArrayList<DiscTrackWs2>();
        this.tracks = tracks;
    }
    /**
     * <p>Adds a track to the list.</p>
     *
     * <p>This method adds a Track (which contains offset, length) to the list of
     * tracks. The leadout track must <em>not</em> be added. The total
     * length of the disc can be set using {@link Disc#setSectors(Integer)}.</p>
     * 
     * @param track: a Disc.Track that contains offset and length as Integers
     * @see getTracks()
     */
    public void addTrack(DiscTrackWs2 track){
            getTracks().add(track);
    }
    public boolean isTrackListEquals(List<DiscTrackWs2> otherTracks){

        if (tracks== null && otherTracks == null) return true;
        if (tracks == null) return false;
        if (otherTracks == null) return false;
        if (tracks.size() != otherTracks.size()) return false;
        
        for (DiscTrackWs2 t1 : tracks)
        {
            if (!otherTracks.contains(t1)) return false;
            DiscTrackWs2 t2 = otherTracks.get(otherTracks.indexOf(t1));
            if(t1.getLength()!= t2.getLength()) return false;
            if(t1.getOffset()!= t2.getOffset()) return false;
        }
        
        return true;
    }
    public boolean isTocEquals(String otherToc){
    
          if (this.getToc() == null && otherToc == null) return true;

          if (otherToc == null) return false;
          if (this.getToc() == null) return false;

          return (otherToc.equals(this.getToc()));
    }
    public boolean isDiscIdEquals(String otherDiscId){
    
          if (this.getDiscId() == null && otherDiscId == null) return true;

          if (otherDiscId == null) return false;
          if (this.getDiscId() == null) return false;

          return (otherDiscId.equals(this.getDiscId()));
    }
   /**
     * Gets the underlying <code>List</clode> of releases.
     * 
     * @return the releases
     */
    public List<ReleaseWs2> getReleases() {
            return ( releaseList == null ? null : releaseList.getReleases());
    }
    /**
     * Sets the underlying <code>List</clode> of releases.
     * 
     * Note: This will implicitly create a new {@link #releaseList}
     * if it is null.
     * 
     * @param releases the releases to set
     */
    public void setReleases(List<ReleaseWs2> releases) 
    {
            if (releaseList == null) {
                    releaseList = new ReleaseListWs2();
            }

            this.releaseList.setReleases(releases);
    }
        /**
     * @return the releaseList
     */
    public ReleaseListWs2 getReleaseList() {
            return releaseList;
    }

    /**
     * @param releaseList the releaseList to set
     */
    public void setReleaseList(ReleaseListWs2 releaseList) {
            if (releaseList==null) 
                this.releaseList= new ReleaseListWs2();
            this.releaseList = releaseList;
    }
        /**
     * <p>Adds a release to the underlying <code>List</clode>
     * of releases.</p>
     * 
     * <p><em>Note: This will implicitly create a new {@link #releaseList}
     * if it is null.</em></p>
     * 
     * @param release The {@link ReleaseWs1} to add.
     */
    public void addRelease(ReleaseWs2 release) 
    {
            if (releaseList == null) {
                    releaseList = new ReleaseListWs2();
            } 
            releaseList.addRelease(release);
    }

    @Override
    public String toString() {
            return "Disc discId=" + discId + ", sectors=" + getSectors();
    }
    @Override
        public boolean equals(Object object) {
            if (!(object instanceof DiscWs2)) {
                return false;
            }
            DiscWs2 other = (DiscWs2) object;
            if (this.discId == null) return false;
            if (other.getDiscId()== null) return false;
            if (!this.discId.equals(other.getDiscId()))return false;
            
            return true;
        }   
}
