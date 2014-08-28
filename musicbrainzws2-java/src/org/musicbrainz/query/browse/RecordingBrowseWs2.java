package org.musicbrainz.query.browse;

import org.musicbrainz.webservice.WebService;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.RecordingBrowseFilterWs2;
import org.musicbrainz.includes.RecordingIncludesWs2;
import org.musicbrainz.model.entity.RecordingWs2;
import org.musicbrainz.model.entity.listelement.RecordingListWs2;

public class RecordingBrowseWs2 extends BrowseWs2{
    
    RecordingListWs2 recordingList = null;
  
    public RecordingBrowseWs2(RecordingBrowseFilterWs2 filter,
                                 RecordingIncludesWs2 include){
        
       super(filter, include);
    }
    public RecordingBrowseWs2(WebService ws,
                                       RecordingBrowseFilterWs2 filter,
                                       RecordingIncludesWs2 include){
        
       super(ws,filter, include);
    }

    public List <RecordingWs2> getFullList() {

        getFirstPage();
        while (hasMore())
        {
           getNextPage();
        }
        return recordingList.getRecordings();

    }
    public List <RecordingWs2> getFirstPage() {

        recordingList = new RecordingListWs2(); 
        getNextPage();

        return recordingList.getRecordings();
    }
 public List <RecordingWs2> getNextPage() {
        
        if (recordingList == null)
            return getFirstPage();
             
        List<RecordingWs2> results  = getOnePage();
        
        recordingList.addAllRecordings(results); 
        filter.setOffset(filter.getOffset()+results.size());

        return results;
    }
    public List <RecordingWs2> getResults(){
        
        if (recordingList.getRecordings() == null)
        return getFirstPage();
            
        return recordingList.getRecordings();
        
    }
    private List <RecordingWs2> getOnePage() {

        List<RecordingWs2> results
                = new ArrayList<RecordingWs2>(0);
        
       
        try {
                RecordingListWs2 temp = execQuery();
                results.addAll(temp.getRecordings());


        } catch (org.musicbrainz.MBWS2Exception ex) {

                ex.printStackTrace();

        }
        return results;
    }
    
    
    private RecordingListWs2 execQuery() throws MBWS2Exception
    {

        RecordingListWs2 le = getMetadata(RECORDING).getRecordingListWs2();
        listElement = le;
        
        int sz  = le.getRecordings().size();
        return le;
    }
}
