package org.musicbrainz.query.browse;

import org.musicbrainz.webservice.WebService;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.browsefilter.ReleaseGroupBrowseFilterWs2;
import org.musicbrainz.includes.ReleaseGroupIncludesWs2;
import org.musicbrainz.model.entity.ReleaseGroupWs2;
import org.musicbrainz.model.entity.listelement.ReleaseGroupListWs2;

public class ReleaseGroupBrowseWs2 extends BrowseWs2{
    
    ReleaseGroupListWs2 releaseGroupList = null;
  
    public ReleaseGroupBrowseWs2(ReleaseGroupBrowseFilterWs2 filter,
                                   ReleaseGroupIncludesWs2 include){
        
       super(filter, include);
    }
    public ReleaseGroupBrowseWs2(WebService ws,
                                            ReleaseGroupBrowseFilterWs2 filter,
                                            ReleaseGroupIncludesWs2 include){
        
       super(ws,filter, include);
    }

    public List <ReleaseGroupWs2> getFullList() {

        getFirstPage();
        while (hasMore())
        {
           getNextPage();
        }
        return releaseGroupList.getReleaseGroups();

    }
    public List <ReleaseGroupWs2> getFirstPage() {

        releaseGroupList = new ReleaseGroupListWs2(); 
        getNextPage();

        return releaseGroupList.getReleaseGroups();
    }
 public List <ReleaseGroupWs2> getNextPage() {
        
        if (releaseGroupList == null)
            return getFirstPage();
        
        List<ReleaseGroupWs2> results  = getOnePage();
        
        releaseGroupList.addAllReleaseGroups(results); 
        filter.setOffset(filter.getOffset()+results.size());

        return results;
    }
    public List <ReleaseGroupWs2> getResults(){
        
        if (releaseGroupList.getReleaseGroups() == null)
        return getFirstPage();
            
        return releaseGroupList.getReleaseGroups();
        
    }
    private List <ReleaseGroupWs2> getOnePage() {

        List<ReleaseGroupWs2> results
                = new ArrayList<ReleaseGroupWs2>(0);

        try {
                ReleaseGroupListWs2 temp = execQuery();
                results.addAll(temp.getReleaseGroups());


        } catch (org.musicbrainz.MBWS2Exception ex) {

                ex.printStackTrace();
                    
        }
        return results;
    }
    
    
    private ReleaseGroupListWs2 execQuery() throws MBWS2Exception
    {

        ReleaseGroupListWs2 le = getMetadata(RELEASEGROUP).getReleaseGroupListWs2();
        listElement = le;
        
        int sz  = le.getReleaseGroups().size();
        return le;
    }
}
