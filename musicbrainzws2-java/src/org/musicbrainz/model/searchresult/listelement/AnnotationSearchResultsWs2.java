package org.musicbrainz.model.searchresult.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.AnnotationListWs2;
import org.musicbrainz.model.searchresult.AnnotationResultWs2;
import org.musicbrainz.wsxml.element.ListElement;

public class AnnotationSearchResultsWs2 extends ListElement{

    protected List<AnnotationResultWs2> annotationResults = new ArrayList<AnnotationResultWs2>();
    private AnnotationListWs2 annotationList = new AnnotationListWs2();

    public void addAnnotationResult(AnnotationResultWs2 annotationResult) 
    {
        if (annotationResults == null) {
                annotationResults = new ArrayList<AnnotationResultWs2>();
        }
        if (getAnnotationList() == null) {
                annotationList = new AnnotationListWs2();
        }
        annotationResults.add(annotationResult);
        annotationList.addAnnotations(annotationResult.getAnnotation());
        
        annotationList.setCount(getCount());
        annotationList.setOffset(getOffset());
    }

    public List<AnnotationResultWs2> getAnnotationResults() {
        return annotationResults;
    }

    public AnnotationListWs2 getAnnotationList() {
        return annotationList;
    }
}
