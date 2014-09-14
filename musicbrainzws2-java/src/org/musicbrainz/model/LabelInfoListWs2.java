package org.musicbrainz.model;

import org.musicbrainz.wsxml.element.ListElement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>A List of Label Info (Label & catalog number)referred by a release</p>

 */
public class LabelInfoListWs2 extends ListElement
{
    private Log log = LogFactory.getLog(LabelInfoListWs2.class);

    /**
    * A string containing the complete credit as join 
    * of credit names in the list.
   */
    private String labelInfoString="";
    
    private List<LabelInfoWs2> labelInfos
               = new ArrayList<LabelInfoWs2>();

    /**
    * Minimal Constructor
    * @param artistCreditElements A list of  ArtistCreditElementWs2
    * describing the Artist Credit.
   */
    public LabelInfoListWs2(List<LabelInfoWs2> labelInfos)
    {
        if (labelInfos!=null)
        {       
            for (LabelInfoWs2 element : labelInfos)
            {
                addLabelInfo(element);
            }
        }
    }
    public String getLabelInfoString(){
        return labelInfoString;
    }
    public List<LabelInfoWs2>  getLabelInfos(){
        return labelInfos;
    }
    
    private void addLabelInfo(LabelInfoWs2 element){

        labelInfos.add(element);
        
        if (!labelInfoString.equals(""))
            labelInfoString= labelInfoString+", ";
        
        labelInfoString = labelInfoString+element.getLabelInfoString();
        
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
            return labelInfoString;
    }

}