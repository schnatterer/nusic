package org.musicbrainz.query.search;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.searchfilter.AnnotationSearchtFilterWs2;
import org.musicbrainz.model.searchresult.AnnotationResultWs2;
import org.musicbrainz.model.searchresult.listelement.AnnotationSearchResultsWs2;
import org.musicbrainz.webservice.DefaultWebServiceWs2;
import org.musicbrainz.webservice.WebService;

public class AnnotationSearchWs2 extends SearchWs2{

    AnnotationSearchResultsWs2 annotationSearchResults = null;
  
    public AnnotationSearchWs2(AnnotationSearchtFilterWs2 filter){
       super(filter);
       ((DefaultWebServiceWs2)super.getWs()).setHost(ANNOTATIONHOST);
    }
    public AnnotationSearchWs2(WebService ws, AnnotationSearchtFilterWs2 filter){
       super(ws, filter);
    }

    public List <AnnotationResultWs2> getFullList() {

        getFirstPage();
        while (hasMore())
        {
           getNextPage();
        }
        return annotationSearchResults.getAnnotationResults();

    }
    public List <AnnotationResultWs2> getFirstPage() {

        annotationSearchResults = new AnnotationSearchResultsWs2(); 
        setLastScore(100);
        getNextPage();

        return annotationSearchResults.getAnnotationResults();
    }

    public List <AnnotationResultWs2> getNextPage() {
       
        List<AnnotationResultWs2> results  = getOnePage();
        
        annotationSearchResults.getAnnotationResults().addAll(results); 
        getFilter().setOffset(getFilter().getOffset()+results.size());

        return results;
    }
    public List <AnnotationResultWs2> getResults(){
        
        if (annotationSearchResults.getAnnotationResults() == null)
        return getFirstPage();
            
        return annotationSearchResults.getAnnotationResults();
        
    }
     private List <AnnotationResultWs2> getOnePage(){

        List <AnnotationResultWs2> results 
                = new ArrayList <AnnotationResultWs2>();

            try {
                    AnnotationSearchResultsWs2 temp = execQuery();
                    results.addAll(temp.getAnnotationResults());

            } catch (org.musicbrainz.MBWS2Exception ex) {
                    ex.printStackTrace();
                }

        return results;
    }
    
    private AnnotationSearchResultsWs2 execQuery() throws MBWS2Exception
    {

        AnnotationSearchResultsWs2 le = getMetadata(ANNOTATION).getAnnotationResultsWs2();
        setListElement(le);
        
        int sz  = le.getAnnotationResults().size();
        if (sz>0)
        {
            setLastScore((int) le.getAnnotationResults().get(sz-1).getScore());
        }
        return le;
    }
}
