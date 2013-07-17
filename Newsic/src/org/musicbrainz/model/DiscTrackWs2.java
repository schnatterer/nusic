package org.musicbrainz.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.mc2.CalendarUtils;

/**
 * A DiscTrack consists of tracknum, offset and length of the drack IN the disc.
 * Primary used to rebuild TOC.
 *
 */

public class DiscTrackWs2 {
     /** track number on disc */
    private int tracknum;
    /** length in sectors of this track */
    private int length;
     /** offset position in sectors of this track */
    private int offset;

    public DiscTrackWs2(Integer tracknum, Integer offset, Integer length) {
        this.tracknum = tracknum;
        this.offset = offset;
        this.length = length;
    }

    public DiscTrackWs2() {
        
    }

     /**
     * @param tracknum the tracknum to set
     */
    public void setTracknum(int tracknum) {
        
        int oldTracknum = this.tracknum;
        this.tracknum = tracknum;
        changeSupport.firePropertyChange("tracknum", oldTracknum, tracknum);

    }
    /**
     * @param offset the offset to set in sectors
     */
    public void setOffset(int offset) {
        
        int oldOffset = this.offset;
        long oldOffsetInMillis = getOffsetInMillis();
        String oldOffsetString = getOffsetString();
        this.offset = offset;
        changeSupport.firePropertyChange("offset", oldOffset, offset);
        changeSupport.firePropertyChange("offsetInMillis", oldOffsetInMillis, getOffsetInMillis());
        changeSupport.firePropertyChange("offsetString", oldOffsetString, getOffsetString());
    }
    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        
        int oldLength = this.length;
        long oldLengthInMillis = getLengthInMillis();
        String oldLengthString = getLengthString();
        
        this.length = length;
        
        changeSupport.firePropertyChange("length", oldLength, length);
        changeSupport.firePropertyChange("lengthInMillis", oldLengthInMillis, getLengthInMillis());
        changeSupport.firePropertyChange("lengthString", oldLengthString, getLengthString());
    }
    /**
     * @return the tracknum
     */
    public int getTracknum() {
        return tracknum;
    }
    /**
     * @return the offset in sector
     */
    public int getOffset() {
        return offset;
    }
    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }
    
    public Long getOffsetInMillis(){
        return new Long(offset)*1000/75;
    }
    public Long getLengthInMillis(){
        return new Long(length)*1000/75;
    }
    public String getOffsetString(){
        
        return getTimeString(getOffsetInMillis());
    }
     public String getLengthString(){
        
        return getTimeString(getLengthInMillis());
    }
    public int getRealLength(){
        return getLength()-getOffset();
    }
     public Long getRealLengthInMillis(){
        return new Long(getRealLength())*1000/75;
    }
    public String getRealLengthString(){
        
        return getTimeString(getRealLengthInMillis());
    }
    private String getTimeString(Long millis){

        return CalendarUtils.calcDurationString(millis);
    }
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
