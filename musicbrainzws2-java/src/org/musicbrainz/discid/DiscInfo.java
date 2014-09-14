package org.musicbrainz.discid;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.mc2.CalendarUtils;


public class DiscInfo {

  public class TrackInfo {

    /** track number on disc */
    public final int num;
    /** sector offset for this track */
    public final int offset;
    /** length in sectors of this track */
    public final int length;

    public TrackInfo(LibDiscId libdiscid, int i) throws DiscIdException {
      this.num = i;
      this.offset = libdiscid.getTrackOffset(i);
      this.length = libdiscid.getTrackLength(i);
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
        return length-offset;
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
  }
  
  /** MusicBrainz DiscId for the cd */
  public final String discid;
  /** FreeDB id for the cd
   * FreeDB: <a href="http://www.freedb.org/">http://www.freedb.org/</a><br/>
   * BeNOW FreeDB Project: <a href="http://benow.ca/projects">http://benow.ca/projects</a>
   */
  public final String freeDBid;
  public final String source;
  public final URL submissionURL;
  public final String webServiceURL;
  public final int firstTrackNum;
  public final int lastTrackNum;
  public final int sectors;
  public final  List<TrackInfo> trackList;
  public final Map<Integer, TrackInfo> trackOffsets = new TreeMap<Integer, TrackInfo>();
  public final String toc;
  
  public DiscInfo(LibDiscId libdiscid, String source) throws DiscIdException {
    this.discid = libdiscid.getId();
    this.freeDBid = libdiscid.getFreeDBId();
    this.source = source != null ? source : libdiscid.getDefaultDevice();
    this.submissionURL=libdiscid.getSubmissionURL();
    this.webServiceURL=libdiscid.getWebServiceURL();
    this.firstTrackNum=libdiscid.getFirstTrackNum();
    this.lastTrackNum=libdiscid.getLastTrackNum();
    this.sectors=libdiscid.getSectors();
    for (int i = firstTrackNum; i <= lastTrackNum; i++)
      trackOffsets.put(i, new TrackInfo(libdiscid,i));
    
    trackList= new ArrayList<TrackInfo>();
    for (int i = firstTrackNum; i <= lastTrackNum; i++)
    {
        trackList.add(new TrackInfo(libdiscid,i));
    }
    
    String t = firstTrackNum+" "+lastTrackNum+" "+sectors+" ";
    for (Integer key : trackOffsets.keySet()) {
      TrackInfo info = trackOffsets.get(key);
      t = t+ info.offset + " ";
    } 
    t = t.substring(0, t.length()-1);
    this.toc=t;
  }

  public static DiscInfo read() throws DiscIdException {
    return read(null);
  }

  public static DiscInfo read(
      String device) throws DiscIdException {
    LibDiscId libdiscid = new LibDiscId(device);
    DiscInfo discId = new DiscInfo(libdiscid,
      device);
    libdiscid.close();
    return discId;
  }

  @Override
  public String toString() {
    String msg = "Device     : " + source + "\n" + 
                     "DiscId     : " + discid + "\n" + 
                     "Toc        : " + toc + "\n" + 
                     "FreeDb Id  : " + freeDBid + "\n" + 
                     //"Submit URL : " + submissionURL + "\n" + 
                     //"Web Service: " + webServiceURL + "\n" + 
                     "First Track: " + firstTrackNum+ "\n" + 
                     "Last Track : " + lastTrackNum + "\n" + 
                     "Sectors    : " + sectors + "\n";
    
    for (Integer key : trackOffsets.keySet()) {
      TrackInfo info = trackOffsets.get(key);
      msg += "  Track " +(key < 10 ? " " : "") + key + " :"+
               " Length: " + info.length + " - "+info.getLengthString()+" ("+info.getLengthInMillis()+")"+
               " Offset: " + info.offset + " - "+info.getOffsetString()+" ("+info.getLengthInMillis()+")"+"\n";
    }
    return msg;
  }

  public static DiscInfo fromOffsets(
      int first,
      int last,
      int[] offsets) throws DiscIdException {
    String offStr = first + " " + last;
    for (int i = 0; i < offsets.length; i++)
      offStr += " " + offsets[i];
    LibDiscId libdiscid = new LibDiscId(first,
      last,
      offsets);
    return new DiscInfo(libdiscid,
      "offsets: " + offStr);

  }

  public static DiscInfo fromOffsets(
      int[] offsets) throws DiscIdException {
    return fromOffsets(1, offsets.length-1, offsets);
  }
}
