package org.musicbrainz.model.entity.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.wsxml.element.ListElement;


/**
 * A list of Labels
 */
public class LabelListWs2 extends ListElement{

    private List<LabelWs2> labels = new ArrayList<LabelWs2>();

    /**
     * @return the labels
     */
    public List<LabelWs2> getLabels() {
            return labels;
    }

    /**
     * @param labels the labels to set
     */
    public void setLabels(List<LabelWs2> labels) {
            this.labels = labels;
    }

    /**
     * Adds a release to the list.
     * 
     * It will also create and set new ArrayList if
     * {@link #labels} is null.
     * 
     * @param release The release to add
     */
    public void addLabel(LabelWs2 label) 
    {
            if (labels == null) {
                    labels = new ArrayList<LabelWs2>();
            }

            labels.add(label);
    }
    public void addAllLabels(List<LabelWs2> labelList) 
    {
            if (labels == null) {
                    labels = new ArrayList<LabelWs2>();
            }

            labels.addAll(labelList);
    }
}
