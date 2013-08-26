package org.musicbrainz.model.entity;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.mc2.CalendarUtils;
import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.IsrcWs2;
import org.musicbrainz.model.PuidWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;

/**
 <p> Represents a Recording.</p>
 */
public class RecordingWs2 extends EntityWs2 
{
    private String title;
    private Long durationInMillis;
    private String disambiguation;
    
    private ArtistCreditWs2 artistCredit;

    private List<PuidWs2> puids;
    private List<IsrcWs2> isrcs;

    private ReleaseListWs2 releaseList = new ReleaseListWs2();
	
    public RecordingWs2() {

    }
    
    
    public String getDuration(){
        return CalendarUtils.calcDurationString(this.getDurationInMillis());
    }
    
        
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Long getDurationInMillis() {
        return durationInMillis;
    }
    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }
  
    public String getDisambiguation() {
        return disambiguation;
    }
    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }
  
    public ArtistCreditWs2 getArtistCredit() {
        return artistCredit;
    }
    public void setArtistCredit(ArtistCreditWs2 artistCredit) {
        this.artistCredit = artistCredit;
    }
     public String getArtistCreditString() {
            if (getArtistCredit()==null) return "";
            return artistCredit.getArtistCreditString();
    }

    public List<PuidWs2> getPuids() {
        return puids;
    }
    public void setPuids(List<PuidWs2> puids) {
        this.puids = puids;
    }
    public void addPuid(PuidWs2 puid) {
        if (puids == null)
                puids = new ArrayList<PuidWs2>();

        puids.add(puid);
    }
    public String getIsrcString() {
        return StringUtils.join(getIsrcs(), ", ");
    }
    public List<IsrcWs2> getIsrcs() {
        return isrcs;
    }
    public void setIsrcs(List<IsrcWs2> isrcs) {
        this.isrcs = isrcs;
    }
    public void addIsrc(IsrcWs2 isrc) {
        if (isrcs == null)
                isrcs = new ArrayList<IsrcWs2>();

        isrcs.add(isrc);
    }

    public List<ReleaseWs2> getReleases() {
        return (releaseList == null ? null : releaseList.getReleases());
    }
    public void setReleases(List<ReleaseWs2> releases) 
    {
        if (releaseList == null) {
                releaseList = new ReleaseListWs2();
        }
        this.releaseList.setReleases(releases);
    }
    public ReleaseListWs2 getReleaseList() {
        return releaseList;
    }
    public void setReleaseList(ReleaseListWs2 releaseList) {
        this.releaseList = releaseList;
    }
    public void addRelease(ReleaseWs2 release) 
    {
        if (releaseList == null) {
                releaseList = new ReleaseListWs2();
        } 
        releaseList.addRelease(release);
    }
    public String getUniqueTitle()
    {
        if (StringUtils.isNotBlank(disambiguation)) {
                return title + " (" + disambiguation + ")";
        }
        return title;
    }

    @Override
    public String toString() {
        return getUniqueTitle();
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RecordingWs2)) {
            return false;
        }
        RecordingWs2 other = (RecordingWs2) object;
        if (this.getIdUri().equals(other.getIdUri()))
        {
            return true;
        }

        return false;
    }
	
}
