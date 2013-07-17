package org.musicbrainz.model;

import org.musicbrainz.model.entity.listelement.DiscListWs2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>A single Medium and relative TrackList.

 */
public class MediumWs2 
{
    private Log log = LogFactory.getLog(MediumWs2.class);

    private int position;
    private String title;
    private String format;
    private int tracksCount;
    private int discCount;
    
    private DiscListWs2 discList;
    private TrackListWs2 trackList;
           
    
    /**
   * Default Constructor
   */
    public MediumWs2()
    {

    }

    @Override
    public String toString() {
        
        if (getFormat()!=null)
        {
            return getFormat() +" "+getPosition()+" - "+getTitle();
        }
        
        return getPosition()+" - "+getTitle();
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the discList
     */
    public DiscListWs2 getDiscList() {
        return discList;
    }

    /**
     * @param discList the discList to set
     */
    public void setDiscList(DiscListWs2 discList) {
        this.discList = discList;
    }

    /**
     * @return the trackList
     */
    public TrackListWs2 getTrackList() {
        return trackList;
    }

    /**
     * @param trackList the trackList to set
     */
    public void setTrackList(TrackListWs2 trackList) {
        this.trackList = trackList;
    }

    /**
     * @return the tracksCount
     */
    public int getTracksCount() {
        return tracksCount;
    }

    /**
     * @param tracksCount the tracksCount to set
     */
    public void setTracksCount(int tracksCount) {
        this.tracksCount=tracksCount;
    }

    /**
     * @return the discCount
     */
    public int getDiscCount() {
        return discCount;
    }

    /**
     * @param discCount the discCount to set
     */
    public void setDiscCount(int discCount) {
        this.discCount = discCount;
    }
    
    public Long getDurationInMillis(){
        
        if (getTrackList()==null ||
             getTrackList().getDurationInMillis()==null)
            
            return 0L;
        
        return getTrackList().getDurationInMillis();
    }
    public String getDuration(){
    
        if (getTrackList()==null ||
             getTrackList().getDurationInMillis()==null)
            
            return "";
        
        return getTrackList().getDuration();
    }
}
