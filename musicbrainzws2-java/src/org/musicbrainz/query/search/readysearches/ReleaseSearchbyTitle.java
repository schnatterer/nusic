package org.musicbrainz.query.search.readysearches;

import java.util.List;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.searchfilter.ReleaseSearchFilterWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.musicbrainz.query.search.ReleaseSearchWs2;

public class ReleaseSearchbyTitle  {

    private ReleaseSearchWs2 q;
    private ReleaseSearchFilterWs2 f;
  
    public ReleaseSearchbyTitle(String title){

        f = new ReleaseSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);
       
        f.setTitle(title);
        
        q = new ReleaseSearchWs2(f);
        
    }
    public List <ReleaseResultWs2> getFullList() throws MBWS2Exception {

        return q.getFullList();

    }
    public List <ReleaseResultWs2> getFirstPage() throws MBWS2Exception {

        f.setOffset((long)0);
        q = new ReleaseSearchWs2(f);
        return q.getFirstPage();
    }
    public List <ReleaseResultWs2> getNextPage() throws MBWS2Exception {
        return q.getNextPage();
    }

    public boolean hasMore(){
        return q.hasMore();
    }
}
