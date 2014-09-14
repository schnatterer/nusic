package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.WorkWs2;

/**
 * Represents an release result.
 */
public class WorkResultWs2 extends SearchResultWs2 {
	
    /**
     * @return the work
     */
    public WorkWs2 getWork() {
            return (WorkWs2)super.getEntity();
    }

    /**
     * @param work the work to set
     */
    public void setWork(WorkWs2 work) {
            super.setEntity(work);
    }
}
