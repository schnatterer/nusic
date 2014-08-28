package org.musicbrainz.query.search.readysearches;

import java.util.List;

import org.musicbrainz.filter.searchfilter.RecordingSearchFilterWs2;
import org.musicbrainz.model.searchresult.RecordingResultWs2;
import org.musicbrainz.query.search.RecordingSearchWs2;

public class RecordingSearchbyTitle  {

    private RecordingSearchWs2 q;
    private RecordingSearchFilterWs2 f;
  
    public RecordingSearchbyTitle(String title){

        f = new RecordingSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
       
        f.setTitle(title);
        
        q = new RecordingSearchWs2(f);
        
    }
    public List <RecordingResultWs2> getFullList() {

        return q.getFullList();

    }
    public List <RecordingResultWs2> getFirstPage() {

        f.setOffset((long)0);
        q = new RecordingSearchWs2(f);
        return q.getFirstPage();
    }
    public List <RecordingResultWs2> getNextPage() {
        return q.getNextPage();
    }

    public boolean hasMore(){
        return q.hasMore();
    }
}
