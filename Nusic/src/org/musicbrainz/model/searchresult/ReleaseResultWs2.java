package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.ReleaseWs2;

/**
 * Represents an release result.
 */
public class ReleaseResultWs2 extends SearchResultWs2 {

    /**
     * @return the release
     */
    public ReleaseWs2 getRelease() {
             return (ReleaseWs2)super.getEntity();
    }

    /**
     * @param release the track to set
     */
    public void setRelease(ReleaseWs2 release) {
            super.setEntity(release);
    }
	
}
