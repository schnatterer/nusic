package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.RecordingWs2;


/**
 * Represents an recording result.
 */
public class RecordingResultWs2 extends SearchResultWs2 {
	
    /**
     * @return the recording
     */
    public RecordingWs2 getRecording() {
            return (RecordingWs2)super.getEntity();
    }

    /**
     * @param recording the recording to set
     */
    public void setRecording(RecordingWs2 recording) {
            super.setEntity(recording);
    }
	
}
