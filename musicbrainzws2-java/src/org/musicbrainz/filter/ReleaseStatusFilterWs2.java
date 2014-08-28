package org.musicbrainz.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.musicbrainz.model.entity.ReleaseWs2;

public class ReleaseStatusFilterWs2 implements FilterWs2 
{

    private boolean statusAll = true;
    
    private boolean statusOfficial = false;
    private boolean statusPromotion = false;
    private boolean statusBootleg = false;
    private boolean statusPseudoRelease = false;

    public ReleaseStatusFilterWs2() {
        
    }
    
    private String createFilterString(){
        
        if (isStatusAll()) return "";
        
        List<String> Status = new ArrayList<String>();
        
        if (isStatusOfficial()) Status.add(ReleaseWs2.STATUS_OFFICIAL);
        if (isStatusPromotion()) Status.add(ReleaseWs2.STATUS_PROMOTION);
        if (isStatusBootleg()) Status.add(ReleaseWs2.STATUS_BOOTLEG);
        if (isStatusPseudoRelease()) Status.add(ReleaseWs2.STATUS_PSEUDO_RELEASE);
        
        String filterString="";
        
        for (String status : Status)
        {
            if (!filterString.equals(""))
            {
                filterString= filterString+"|";
            }
            filterString= filterString+status;
        }
        
        return filterString;
    }

    public Map<String, String> createParameters() 
    {
            Map<String, String> map = new HashMap<String, String>();
            
            String filterString=createFilterString();
            
            // construct the track filter's map			

            if (!filterString.equals(""))
                map.put("status",filterString);

            return map;
    }

    /**
     * @return the statusOfficial
     */
    public boolean isStatusOfficial() {
        return statusOfficial;
    }

    /**
     * @param statusOfficial the statusOfficial to set
     */
    public void setStatusOfficial(boolean statusOfficial) {
        this.statusOfficial = statusOfficial;
    }

    /**
     * @return the statusPromotion
     */
    public boolean isStatusPromotion() {
        return statusPromotion;
    }

    /**
     * @param statusPromotion the statusPromotion to set
     */
    public void setStatusPromotion(boolean statusPromotion) {
        this.statusPromotion = statusPromotion;
    }

    /**
     * @return the statusBootleg
     */
    public boolean isStatusBootleg() {
        return statusBootleg;
    }

    /**
     * @param statusBootleg the statusBootleg to set
     */
    public void setStatusBootleg(boolean statusBootleg) {
        this.statusBootleg = statusBootleg;
    }

    /**
     * @return the statusPseudoRelease
     */
    public boolean isStatusPseudoRelease() {
        return statusPseudoRelease;
    }

    /**
     * @param statusPseudoRelease the statusPseudoRelease to set
     */
    public void setStatusPseudoRelease(boolean statusPseudoRelease) {
        this.statusPseudoRelease = statusPseudoRelease;
    }

    /**
     * @return the statusAll
     */
    public boolean isStatusAll() {
        return statusAll;
    }

    /**
     * @param statusAll the statusAll to set
     */
    public void setStatusAll(boolean statusAll) {
        this.statusAll = statusAll;
    }

}