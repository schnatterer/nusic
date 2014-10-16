package org.musicbrainz.query.browse;

import org.musicbrainz.webservice.WebService;
import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.LabelBrowseFilterWs2;
import org.musicbrainz.includes.LabelIncludesWs2;
import org.musicbrainz.model.entity.LabelWs2;
import org.musicbrainz.model.entity.listelement.LabelListWs2;

public class LabelBrowseWs2 extends BrowseWs2{

    LabelListWs2 labelList = null;
  
    public LabelBrowseWs2(LabelBrowseFilterWs2 filter,
                                  LabelIncludesWs2 include){
        
       super(filter, include);
    }
    public LabelBrowseWs2(WebService ws,
                                  LabelBrowseFilterWs2 filter,
                                  LabelIncludesWs2 include){
        
       super(ws,filter, include);
    }

    public List <LabelWs2> getFullList() {

        getFirstPage();
        while (hasMore())
        {
           getNextPage();
        }
        return labelList.getLabels();

    }
    public List <LabelWs2> getFirstPage() {

        labelList = new LabelListWs2(); 
        getNextPage();

        return labelList.getLabels();
    }
 public List <LabelWs2> getNextPage() {
        
        if (labelList == null)
            return getFirstPage();
        
        List<LabelWs2> results  = getOnePage();
        
        labelList.addAllLabels(results); 
        filter.setOffset(filter.getOffset()+results.size());

        return results;
    }
    public List <LabelWs2> getResults(){
        
        if (labelList.getLabels() == null)
        return getFirstPage();
            
        return labelList.getLabels();
        
    }
    private List <LabelWs2> getOnePage() {

        List<LabelWs2> results
                = new ArrayList<LabelWs2>(0);
      
        try {
                LabelListWs2 temp = execQuery();
                results.addAll(temp.getLabels());

        } catch (org.musicbrainz.MBWS2Exception ex) {

                ex.printStackTrace();

        }
        return results;
    }
    
    
    private LabelListWs2 execQuery() throws MBWS2Exception
    {

        LabelListWs2 le = getMetadata(LABEL).getLabelListWs2();
        listElement = le;
        
        int sz  = le.getLabels().size();
        return le;
    }
}
