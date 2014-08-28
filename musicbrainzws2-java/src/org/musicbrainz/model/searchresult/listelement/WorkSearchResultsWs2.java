package org.musicbrainz.model.searchresult.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.listelement.WorkListWs2;
import org.musicbrainz.model.searchresult.WorkResultWs2;
import org.musicbrainz.wsxml.element.ListElement;

public class WorkSearchResultsWs2 extends ListElement{

    protected List<WorkResultWs2> workResults = new ArrayList<WorkResultWs2>();
    private WorkListWs2 workList = new WorkListWs2();

    public void addWorkResult(WorkResultWs2 workResult) 
    {
        if (workResults == null) {
                workResults = new ArrayList<WorkResultWs2>();
        }
        if (getWorkList() == null) {
                workList = new WorkListWs2();
        }
        workResults.add(workResult);
        workList.addWorks(workResult.getWork());
        
        workList.setCount(getCount());
        workList.setOffset(getOffset());
    }

    public List<WorkResultWs2> getWorkResults() {
        return workResults;
    }

    public WorkListWs2 getWorkList() {
        return workList;
    }
}
