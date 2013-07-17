package org.musicbrainz.model.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.utils.MbUtils;

/**
 * Rapresents a Work
 */
public class WorkWs2 extends EntityWs2 
{
    // TODO: Should we add the typeURI URI definitions?
    // Seems they are work in progress...
    
    //public static final String TYPE_ARIA = NS_MMD_2 + "Aria";
    //public static final String TYPE_BALLET = NS_MMD_2 + "Ballet";
    //public static final String TYPE_CANTATA = NS_MMD_2 + "Cantata";
    //...
    
    private String typeURI;
    private String title;
    private ArtistCreditWs2 artistCredit;
    private String iswc;
    private String disambiguation;
    
    public WorkWs2() {

    }

    public String getTypeURI() {
        return typeURI;
    }
    public void setTypeURI(String type) {
        this.typeURI = type;
    }
    public String getType() {

       if (getTypeURI()== null) return "";
       if (getTypeURI().isEmpty()) return "";
       return MbUtils.extractTypeFromURI(getTypeURI());
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public ArtistCreditWs2 getArtistCredit() {
        return artistCredit;
    }
    public void setArtistCredit(ArtistCreditWs2 artistCredit) {
        this.artistCredit = artistCredit;
    }
        
    public String getIswc() {
        return iswc;
    }
    public void setIswc(String iswc) {
        this.iswc = iswc;
    }
  
    public String getDisambiguation() {
        return disambiguation;
    }
    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }
    
    public String getUniqueTitle()
    {
        if (StringUtils.isNotBlank(disambiguation)) {
                return title + " (" + disambiguation + ")";
        }
        return title;
    }
    public String getWritersString()
    {
        if (getRelationList() == null ) return "";
        if (getRelationList().getRelations().isEmpty()) return "";

        List<String> writers = new ArrayList<String>();
        for (Iterator <RelationWs2> i = getRelationList().getRelations().iterator(); i.hasNext();)
        {
            RelationWs2 r = i.next();
            if (!r.getTargetType().equals(RelationWs2.TO_ARTIST)) continue;
            
            if (((ArtistWs2)r.getTarget()).getUniqueName().isEmpty()) continue;
            if (writers.contains(((ArtistWs2)r.getTarget()).getUniqueName())) continue;
                
            writers.add(((ArtistWs2)r.getTarget()).getUniqueName());
            
        }
        String out = Arrays.toString(writers.toArray()).trim();
        out = out.substring(1);
        out = out.substring(0, out.length()-1).trim();
        
        return out;
    }
    public String getArtistCreditString() {
        if (artistCredit!=null)
        {
            return artistCredit.getArtistCreditString();
        }
        return "";
    }
    
    public String getBy(){
           if (!getArtistCreditString().isEmpty()) 
               return getArtistCreditString();
           return getWritersString();
           
   }

    @Override
    public String toString() {
        return getUniqueTitle()+" by "+getBy();
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WorkWs2)) {
            return false;
        }
        WorkWs2 other = (WorkWs2) object;
        if (this.getIdUri().equals(other.getIdUri()))
        {
            return true;
        }

        return false;
    }
}
