package org.musicbrainz.filter.searchfilter;

import java.util.Map;

/**
 * <p>A filter for the label collection.</p>
 * 
 * <p>Note that the <code>name</code> and <code>query</code> properties
 * may not be used together.</p>
 */
public class LabelSearchFilterWs2 extends SearchFilterWs2 {

    /**
    * The name of the label
    */
    private String labelName = null;

    public LabelSearchFilterWs2() {
       super();
    }

    @Override
    public Map<String, String> createParameters() 
    {
        Map<String, String> map = super.createParameters();
        if (this.labelName != null) 
        {
                if (map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("The name and query properties may not be used together!");
                }

                map.put(QUERY, this.labelName);
        } 
        else {
                if (!map.containsKey(QUERY)) {
                        throw new IllegalArgumentException("This filter must specify a query or a label name!");
                }
        }

        return map;
    }


    /**
    * @return the labelName
    */
    public String getLabelName() {
        return labelName;
    }

    /**
    * @param labelName the labelName to set
    */
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

}