package org.musicbrainz.query.search;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.webservice.WebService;

import org.musicbrainz.filter.searchfilter.SearchFilterWs2;
import org.musicbrainz.query.QueryWs2;
import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.wsxml.element.ListElement;

/*
 * 
 * Lucerne Search Implementation.
 * 
 * Searches are implemented by the search server and are documented at 
 * Next_Generation_Schema/SearchServerXML. 
 * see: http://musicbrainz.org/doc/Next_Generation_Schema/SearchServerXML
 *
 * Use a filter subclass to set the search parameters.
 * Return a SearchResult subtype.
 * 
 */

public class SearchWs2 extends QueryWs2 {

    private SearchFilterWs2 filter;
    private ListElement listElement;
    private int lastScore=100;


    protected SearchWs2(SearchFilterWs2 filter)
    {
            super();
            this.filter=filter;
    }

    /**
     * Custom WebService Constructor
     *  
     * @param ws An implementation of {@link WebService}
    */
    protected SearchWs2(WebService ws, SearchFilterWs2 filter)
    {
            super(ws);
            this.filter=filter;
    }

    public boolean hasMore()
    {
        if (getListElement()== null) return false;
        if (getFilter() == null) return false;
        
        int count  = getListElement().getCount() == null ? 0 : getListElement().getCount().intValue();
        int offset =getListElement().getOffset() == null ? 0 : getListElement().getOffset().intValue();
        int limit =getFilter().getLimit()  == null ? 0 : getFilter().getLimit().intValue();

        // maybe the real one is lower, if we reached the end
        // but it does'nt matter in this context.
        
        int newOffset = offset+limit; 
        
        int minScore  = getFilter().getMinScore()== null ? 0 : getFilter().getMinScore().intValue();
        
        if (count >= newOffset && getLastScore()>=minScore) return true;
        return false;
    }
     
    protected Metadata getMetadata(String entity) throws MBWS2Exception
    {
        return getFromWebService(entity, "", null, getFilter());
    }

    /**
     * @return the filter
     */
    public SearchFilterWs2 getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(SearchFilterWs2 filter) {
        this.filter = filter;
    }

    /**
     * @return the listElement
     */
    public ListElement getListElement() {
        return listElement;
    }

    /**
     * @param listElement the listElement to set
     */
    public void setListElement(ListElement listElement) {
        this.listElement = listElement;
    }

    /**
     * @return the lastScore
     */
    public int getLastScore() {
        return lastScore;
    }

    /**
     * @param lastScore the lastScore to set
     */
    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

}
