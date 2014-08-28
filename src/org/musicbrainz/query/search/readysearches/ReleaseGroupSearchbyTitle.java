package org.musicbrainz.query.search.readysearches;

import java.util.List;

import org.musicbrainz.filter.searchfilter.ReleaseGroupSearchFilterWs2;
import org.musicbrainz.model.searchresult.ReleaseGroupResultWs2;
import org.musicbrainz.query.search.ReleaseGroupSearchWs2;

public class ReleaseGroupSearchbyTitle  {

    private ReleaseGroupSearchWs2 q;
    private ReleaseGroupSearchFilterWs2 f;
  
    public ReleaseGroupSearchbyTitle(String title){

        f = new ReleaseGroupSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
       
        f.setTitle(title);
        
        q = new ReleaseGroupSearchWs2(f);
        
    }
    public List <ReleaseGroupResultWs2> getFullList() {

        return q.getFullList();

    }
    public List <ReleaseGroupResultWs2> getFirstPage() {

        f.setOffset((long)0);
        q = new ReleaseGroupSearchWs2(f);
        return q.getFirstPage();
    }
    public List <ReleaseGroupResultWs2> getNextPage() {
        return q.getNextPage();
    }

    public boolean hasMore(){
        return q.hasMore();
    }
}
