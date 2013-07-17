package org.musicbrainz.model;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc2.CalendarUtils;
import org.musicbrainz.model.entity.RecordingWs2;


/**
 * <p>A single recordings in a Medium of a specific release. 
 * .
 */
public class TrackWs2 
{
    private Log log = LogFactory.getLog(TrackWs2.class);

    private MediumWs2 medium;
    private int position;
    private String title;
    private RecordingWs2 recording;
    private Long durationInMillis;
           
    
    /**
   * Default Constructor
   */
    public TrackWs2()
    {

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
     * @return the recording
     */
    public RecordingWs2 getRecording() {
        return recording;
    }
        
    /**
     * @param recording the recording to set
     */
    
    public void setRecording(RecordingWs2 recording) {
        this.recording = recording;
    }

    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public String getDuration(){
       
      return CalendarUtils.calcDurationString(this.getDurationInMillis());
      
      
    }
    public Long getDurationInMillis(){
        
        if (durationInMillis != null 
                && durationInMillis !=0) return durationInMillis;
        
        if (getRecording()==null) return 0L;
        return getRecording().getDurationInMillis();
    }

     /**
     * @return the medium
     */
    public MediumWs2 getMedium() {
        return medium;
    }
    public String getMediumStr(){
        
        if (getMedium()==null) return "";
        
        String out =  getMedium().getFormat()+" "
                +String.valueOf(getMedium().getPosition());
        
        return out;
    }
    
    /**
     * @param medium the medium to set
     */
    public void setMedium(MediumWs2 medium) {
        this.medium = medium;
    }
    @Override
    public String toString() {
        return getPosition()+" - "+getTitle();
    }
}
