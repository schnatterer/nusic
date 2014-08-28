package org.musicbrainz.filter;

import java.util.HashMap;
import java.util.Map;


public class DiscTocFilterWs2 implements FilterWs2 
{

    private String toc;
    private Boolean cdStub=true;

    public DiscTocFilterWs2() {
        
    }

    @Override
    public Map<String, String> createParameters() 
    {
        Map<String, String> map = new HashMap<String, String>();

        // construct the track filter's map			

        if (toc != null && !toc.isEmpty())
            map.put("toc",toc);
        if (!isCdStub())
            map.put("cdstubs","no");

        return map;
    }

    /**
     * @return the toc
     */
    public String getToc() {
        return toc;
    }

    /**
     * @param toc the toc to set
     */
    public void setToc(String toc) {
        this.toc = toc;
    }

    /**
     * @return the cdStub
     */
    public Boolean isCdStub() {
        return cdStub;
    }

    /**
     * @param cdStub the cdStub to set
     */
    public void setCdStub(Boolean cdStub) {
        this.cdStub = cdStub;
    }


}