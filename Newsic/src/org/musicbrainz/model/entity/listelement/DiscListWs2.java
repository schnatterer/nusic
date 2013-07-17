package org.musicbrainz.model.entity.listelement;

import org.musicbrainz.model.entity.DiscWs2;
import org.musicbrainz.wsxml.element.ListElement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>A List of Disc Info (Disc & catalog number)referred by a release</p>

 */
public class DiscListWs2 extends ListElement
{
    private Log log = LogFactory.getLog(DiscListWs2.class);

    /**
    * A string containing the complete credit as join 
    * of credit names in the list.
   */
    
    private List<DiscWs2> discs
               = new ArrayList<DiscWs2>();

    /**
    * Minimal Constructor
    * @param artistCreditElements A list of  ArtistCreditElementWs2
    * describing the Artist Credit.
   */
    public DiscListWs2(List<DiscWs2> discs)
    {
        if (discs!=null)
        {       
            for (DiscWs2 element : discs)
            {
                addDisc(element);
            }
        }
    }
    
    public List<DiscWs2>  getDiscs(){
        return discs;
    }
    
    private void addDisc(DiscWs2 element){

        discs.add(element);

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
}