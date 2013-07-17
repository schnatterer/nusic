package org.musicbrainz.query.browse;

import org.musicbrainz.webservice.WebService;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.WorkBrowseFilterWs2;
import org.musicbrainz.includes.WorkIncludesWs2;
import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.model.entity.listelement.WorkListWs2;

public class WorkBrowseWs2 extends BrowseWs2{
    
    WorkListWs2 WorkList = null;
  
    public WorkBrowseWs2(WorkBrowseFilterWs2 filter,
                                 WorkIncludesWs2 include){
        
       super(filter, include);
    }
    public WorkBrowseWs2(WebService ws,
                                 WorkBrowseFilterWs2 filter,
                                 WorkIncludesWs2 include){
        
       super(ws,filter, include);
    }

    public List <WorkWs2> getFullList() {

        getFirstPage();
        while (hasMore())
        {
           getNextPage();
        }
        return WorkList.getWorks();

    }
    public List <WorkWs2> getFirstPage() {

        WorkList = new WorkListWs2(); 
        getNextPage();

        return WorkList.getWorks();
    }
 public List <WorkWs2> getNextPage() {
        
       if (WorkList == null)
            return getFirstPage();
        
        List<WorkWs2> results  = getOnePage();
        
        WorkList.addAllWorks(results); 
        filter.setOffset(filter.getOffset()+results.size());

        return results;
    }
    public List <WorkWs2> getResults(){
        
        if (WorkList.getWorks() == null)
        return getFirstPage();
            
        return WorkList.getWorks();
        
    }
    private List <WorkWs2> getOnePage() {

        List<WorkWs2> results
                = new ArrayList<WorkWs2>(0);
       
        try {
                WorkListWs2 temp = execQuery();
                results.addAll(temp.getWorks());

        } catch (org.musicbrainz.MBWS2Exception ex) {

                ex.printStackTrace();
        }
        return results;
    }
    
    
    private WorkListWs2 execQuery() throws MBWS2Exception
    {

        WorkListWs2 le = getMetadata(WORK).getWorkListWs2();
        listElement = le;
        
        int sz  = le.getWorks().size();
        return le;
    }
}
