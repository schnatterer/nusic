package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.CollectionWs2;


/**
 * Represents an artist result.
 */
public class CollectionResultWs2 extends SearchResultWs2 {

    /**
     * @return the artist
     */
    public CollectionWs2 getCollection() {
            return (CollectionWs2)super.getEntity();
    }

    /**
     * @param artist the artist to set
     */
    public void setCollection(CollectionWs2 artist) {
            super.setEntity(artist);
    }
	
}
