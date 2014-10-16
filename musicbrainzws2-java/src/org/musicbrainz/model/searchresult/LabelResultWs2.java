package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.LabelWs2;

/**
 * Represents a label result.
 */
public class LabelResultWs2 extends SearchResultWs2 {
	
    /*
     * @return the label
     */
    public LabelWs2 getLabel() {
            return (LabelWs2)super.getEntity();
    }

    /**
     * @param label the label to set
     */
    public void setLabel(LabelWs2 label) {
            super.setEntity(label);
    }
	
}
