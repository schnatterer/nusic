package org.musicbrainz.model;

import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.wsxml.element.ListElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc2.CalendarUtils;

/**
 * <p>A List of Media (Medium) referred by a release</p>

 */
public class MediumListWs2 extends ListElement
{
    private Log log = LogFactory.getLog(MediumListWs2.class);

    private int tracksCount;
    
    private List<MediumWs2> media
               = new ArrayList<MediumWs2>();


    public MediumListWs2(List<MediumWs2> media)
    {
        if (media!=null)
        {       
            for (MediumWs2 element : media)
            {
                addMedium(element);
            }
        }
    }
    
    public List<MediumWs2>  getMedia(){
        return media;
    }
    
    private void addMedium(MediumWs2 element){

        media.add(element);

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
        this.tracksCount = tracksCount;
    }
    public String getFormat(){
        
        if (media == null || media.isEmpty()) return "";
        
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (MediumWs2 m : media)
        {
            String format = m.getFormat();
            
            if (format==null) continue;
            
            if (map.isEmpty() || !map.containsKey(m.getFormat()))
            {
                map.put(format, 1);
            }
            else
            {
                map.put(format, map.get(format)+1);
            }
        }
        if (map.isEmpty()) return "";
        
        StringBuilder out = new StringBuilder();

        Iterator<Entry<String, Integer>> it = map.entrySet().iterator();

        while (it.hasNext()) {

            Entry<String, Integer> e = it.next();

            if (map.entrySet().size()>1)
            {
                out.append(e.getValue()).append("x").append(e.getKey());
            }
            else if (e.getValue()>1)
            {
                out.append(e.getValue()).append("x").append(e.getKey());
            }
            else
            {
                out.append(e.getKey());
            }
            out.append(", ");
        }
        return out.substring(0, out.length()-2);
    }
    public Long getDurationInMillis(){
        
        Long dur = 0L;
        if (media == null) return dur;
        
        for (MediumWs2 m : media)
        {
            dur = dur+  m.getDurationInMillis();
        }
        return dur;
    }
    public String getDuration(){
        return CalendarUtils.calcDurationString(this.getDurationInMillis());
    }
    public List<TrackWs2> getCompleteTrackList(){
       
      List<TrackWs2> complete = new ArrayList<TrackWs2>();
      
      if (getMedia()== null ||
           getMedia().isEmpty()) return complete;
      
      for (MediumWs2 m : getMedia())
      {
          if (m.getTrackList()== null ||
               m.getTrackList().getTracks() == null ||
               m.getTrackList().getTracks().isEmpty()) continue;
          
          complete.addAll(m.getTrackList().getTracks());
      }
      
      return complete;
       
   }
    public List<DiscWs2> getCompleteDiscList(){
       
      List<DiscWs2> complete = new ArrayList<DiscWs2>();
      
      if (getMedia()== null ||
           getMedia().isEmpty()) return complete;
      
      for (MediumWs2 m : getMedia())
      {
          if (m.getDiscList()== null ||
               m.getDiscList().getDiscs() == null ||
               m.getDiscList().getDiscs().isEmpty()) continue;
          
          complete.addAll(m.getDiscList().getDiscs());
      }
      
      return complete;
       
   }

}