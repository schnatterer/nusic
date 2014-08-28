package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * Search annotations by ID for any entity.
 * 
 */
public class AnnotationSearchtFilterWs2 extends SearchFilterWs2 {
    
    public static final String ENTITY = "entity";
    
    private String entity = null;
    
    /* (non-Javadoc)
    * @see org.musicbrainz.webservice.Filter()
    */
    public AnnotationSearchtFilterWs2() {
       super();
    }

    @Override
    public Map<String, String> createParameters() 
    {
        Map<String, String> map = super.createParameters();
        if (this.entity != null) 
        {
                if (map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("The text and query properties may not be used together!");
                }

                map.put(QUERY, ENTITY+":"+this.entity);
        } 
        else {
                if (!map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("This filter must specify a query or a text!");
                }
        }

        return map;
    }


    /**
    * @return the workTitle
    */
    public String getEntity() {
        return entity;
    }

    /**
    * @param workTitle the workTitle to set
    */
    public void setEntity(String entity) {
        this.entity = entity;
    }

}