package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.ArtistWs2;


/**
 * Represents an artist result.
 */
public class ArtistResultWs2 extends SearchResultWs2 {

    /**
     * @return the artist
     */
    public ArtistWs2 getArtist() {
            return (ArtistWs2)super.getEntity();
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(ArtistWs2 artist) {
            super.setEntity(artist);
    }
	
}
