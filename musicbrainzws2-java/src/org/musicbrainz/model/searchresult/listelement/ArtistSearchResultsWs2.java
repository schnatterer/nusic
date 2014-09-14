package org.musicbrainz.model.searchresult.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.listelement.ArtistListWs2;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.wsxml.element.ListElement;

public class ArtistSearchResultsWs2 extends ListElement{

    private List<ArtistResultWs2> artistResults = new ArrayList<ArtistResultWs2>();
    private ArtistListWs2 artistList = new ArtistListWs2();

    /**
     * Convenience method to adds an {@link ArtistResultWs1} to the list.
     * 
     * This will create a new <code>ArrayList</code> if {@link #artistResults} is null.
     * 
     * @param artistResult The artist result to add
     */
    public void addArtistResult(ArtistResultWs2 artistResult) 
    {
            if (getArtistResults() == null) {
                    artistResults = new ArrayList<ArtistResultWs2>();
            }
            if (getArtistList() == null) {
                artistList = new ArtistListWs2();
            }
            
            artistResults.add(artistResult);
            artistList.addArtist(artistResult.getArtist()); 
            
            artistList.setCount(getCount());
            artistList.setOffset(getOffset());
    }
    
    public List<ArtistResultWs2> getArtistResults() {
            return artistResults;
    }

    public ArtistListWs2 getArtistList() {
        return artistList;
    }
}
