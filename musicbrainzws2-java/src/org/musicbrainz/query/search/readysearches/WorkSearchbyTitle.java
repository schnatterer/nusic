package org.musicbrainz.query.search.readysearches;

import java.util.List;

import org.musicbrainz.filter.searchfilter.WorkSearchFilterWs2;
import org.musicbrainz.model.searchresult.WorkResultWs2;
import org.musicbrainz.query.search.WorkSearchWs2;

public class WorkSearchbyTitle  {

    private WorkSearchWs2 q;
    private WorkSearchFilterWs2 f;
  
    public WorkSearchbyTitle(String title){

        f = new WorkSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
       
        f.setWorkTitle(title);
        
        q = new WorkSearchWs2(f);
        
    }
    public List <WorkResultWs2> getFullList() {

        return q.getFullList();

    }
    public List <WorkResultWs2> getFirstPage() {

        f.setOffset((long)0);
        q = new WorkSearchWs2(f);
        return q.getFirstPage();
    }
    public List <WorkResultWs2> getNextPage() {
        return q.getNextPage();
    }

    public boolean hasMore(){
        return q.hasMore();
    }
}
