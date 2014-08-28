package org.musicbrainz.query.search.readysearches;

import java.util.List;

import org.musicbrainz.filter.searchfilter.ArtistSearchFilterWs2;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.query.search.ArtistSearchWs2;

public class ArtistSearchbyName  implements EntitySearchInterface{

    private ArtistSearchWs2 q;
    private ArtistSearchFilterWs2 f;
  
    public ArtistSearchbyName(String name){

        f = new ArtistSearchFilterWs2();
        f.setLimit((long)100);
        f.setOffset((long)0);
        f.setMinScore((long)20);

        f.setArtistName(name);
        
        q = new ArtistSearchWs2(f);
        
    }
    public List <ArtistResultWs2> getFullList() {

        return q.getFullList();

    }
    public List <ArtistResultWs2> getFirstPage() {

        f.setOffset((long)0);
        q = new ArtistSearchWs2(f);
        return q.getFirstPage();
    }
    public List <ArtistResultWs2> getNextPage() {
        return q.getNextPage();
    }

    public boolean hasMore(){
        return q.hasMore();
    }
}
