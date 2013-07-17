package org.musicbrainz.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.utils.MbUtils;

public class ReleaseTypeFilterWs2 implements FilterWs2 
{
    private boolean typeAll = true;
    private boolean typeNat = false;
    private boolean typeAlbum = false;
    private boolean typeSingle = false;
    private boolean typeEp = false;
    private boolean typeCompilation = false;
    private boolean typeSoundTrack = false;
    private boolean typeSpokenword = false;
    private boolean typeInterview = false;
    private boolean typeAuodioBook = false;
    private boolean typeLive = false;
    private boolean typeRemix = false;
    private boolean typeOther = false;

    public ReleaseTypeFilterWs2() {
        
    }
    
    private String createFilterString(){
        
        if (isTypeAll())
        {
            return "";
        }
        
        List<String> Types = new ArrayList<String>();
        
        if (isTypeNat()) Types.add(ReleaseWs2.TYPE_NAT);
        if (isTypeAlbum()) Types.add(ReleaseWs2.TYPE_ALBUM);
        if (isTypeSingle()) Types.add(ReleaseWs2.TYPE_SINGLE);
        if (isTypeEp()) Types.add(ReleaseWs2.TYPE_EP);
        if (isTypeCompilation()) Types.add(ReleaseWs2.TYPE_COMPILATION);
        if (isTypeSoundTrack()) Types.add(ReleaseWs2.TYPE_SOUNDTRACK);
        if (isTypeSpokenword()) Types.add(ReleaseWs2.TYPE_SPOKENWORD);
        if (isTypeInterview()) Types.add(ReleaseWs2.TYPE_INTERVIEW);
        if (isTypeAuodioBook()) Types.add(ReleaseWs2.TYPE_AUDIOBOOK);
        if (isTypeLive()) Types.add(ReleaseWs2.TYPE_LIVE);
        if (isTypeRemix()) Types.add(ReleaseWs2.TYPE_REMIX);
        if (isTypeOther()) Types.add(ReleaseWs2.TYPE_OTHER);
        
        String filterString="";
        
        for (String type : Types)
        {
            if (!filterString.equals(""))
            {
                filterString= filterString+"|";
            }
            filterString= filterString+MbUtils.extractTypeFromURI(type);
        }
        
        return filterString;
    }

    public Map<String, String> createParameters() 
    {
            Map<String, String> map = new HashMap<String, String>();
            
            String filterString=createFilterString();
            
            // construct the track filter's map			

            if (!filterString.equals(""))
                map.put("type",filterString);

            return map;
    }

    /**
     * @return the typeNat
     */
    public boolean isTypeNat() {
        return typeNat;
    }

    /**
     * @param typeNat the typeNat to set
     */
    public void setTypeNat(boolean typeNat) {
        this.typeNat = typeNat;
    }

    /**
     * @return the typeAlbum
     */
    public boolean isTypeAlbum() {
        return typeAlbum;
    }

    /**
     * @param typeAlbum the typeAlbum to set
     */
    public void setTypeAlbum(boolean typeAlbum) {
        this.typeAlbum = typeAlbum;
    }

    /**
     * @return the typeSingle
     */
    public boolean isTypeSingle() {
        return typeSingle;
    }

    /**
     * @param typeSingle the typeSingle to set
     */
    public void setTypeSingle(boolean typeSingle) {
        this.typeSingle = typeSingle;
    }

    /**
     * @return the typeEp
     */
    public boolean isTypeEp() {
        return typeEp;
    }

    /**
     * @param typeEp the typeEp to set
     */
    public void setTypeEp(boolean typeEp) {
        this.typeEp = typeEp;
    }

    /**
     * @return the typeCompilation
     */
    public boolean isTypeCompilation() {
        return typeCompilation;
    }

    /**
     * @param typeCompilation the typeCompilation to set
     */
    public void setTypeCompilation(boolean typeCompilation) {
        this.typeCompilation = typeCompilation;
    }

    /**
     * @return the typeSoundTrack
     */
    public boolean isTypeSoundTrack() {
        return typeSoundTrack;
    }

    /**
     * @param typeSoundTrack the typeSoundTrack to set
     */
    public void setTypeSoundTrack(boolean typeSoundTrack) {
        this.typeSoundTrack = typeSoundTrack;
    }

    /**
     * @return the typeSpokenword
     */
    public boolean isTypeSpokenword() {
        return typeSpokenword;
    }

    /**
     * @param typeSpokenword the typeSpokenword to set
     */
    public void setTypeSpokenword(boolean typeSpokenword) {
        this.typeSpokenword = typeSpokenword;
    }

    /**
     * @return the typeInterview
     */
    public boolean isTypeInterview() {
        return typeInterview;
    }

    /**
     * @param typeInterview the typeInterview to set
     */
    public void setTypeInterview(boolean typeInterview) {
        this.typeInterview = typeInterview;
    }

    /**
     * @return the typeAuodioBook
     */
    public boolean isTypeAuodioBook() {
        return typeAuodioBook;
    }

    /**
     * @param typeAuodioBook the typeAuodioBook to set
     */
    public void setTypeAuodioBook(boolean typeAuodioBook) {
        this.typeAuodioBook = typeAuodioBook;
    }

    /**
     * @return the typeLive
     */
    public boolean isTypeLive() {
        return typeLive;
    }

    /**
     * @param typeLive the typeLive to set
     */
    public void setTypeLive(boolean typeLive) {
        this.typeLive = typeLive;
    }

    /**
     * @return the typeRemix
     */
    public boolean isTypeRemix() {
        return typeRemix;
    }

    /**
     * @param typeRemix the typeRemix to set
     */
    public void setTypeRemix(boolean typeRemix) {
        this.typeRemix = typeRemix;
    }

    /**
     * @return the typeOther
     */
    public boolean isTypeOther() {
        return typeOther;
    }

    /**
     * @param typeOther the typeOther to set
     */
    public void setTypeOther(boolean typeOther) {
        this.typeOther = typeOther;
    }

    /**
     * @return the typeAll
     */
    public boolean isTypeAll() {
        return typeAll;
    }

    /**
     * @param typeAll the typeAll to set
     */
    public void setTypeAll(boolean typeAll) {
        this.typeAll = typeAll;
    }
}