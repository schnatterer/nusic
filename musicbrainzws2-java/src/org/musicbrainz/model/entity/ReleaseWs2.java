package org.musicbrainz.model.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.LabelInfoListWs2;
import org.musicbrainz.model.MediumListWs2;
/**
 * <p>Represents a release.</p>
 
 */
public class ReleaseWs2 extends EntityWs2 {

    private Log log = LogFactory.getLog(ReleaseWs2.class);
    
    public static final String TYPE_NONE = ReleaseGroupWs2.TYPE_NONE;

    public static final String TYPE_NAT = ReleaseGroupWs2.TYPE_NAT;
    public static final String TYPE_ALBUM = ReleaseGroupWs2.TYPE_SINGLE;
    public static final String TYPE_SINGLE = ReleaseGroupWs2.TYPE_SINGLE;
    public static final String TYPE_EP = ReleaseGroupWs2.TYPE_EP;
    public static final String TYPE_COMPILATION = ReleaseGroupWs2.TYPE_COMPILATION;
    public static final String TYPE_SOUNDTRACK = ReleaseGroupWs2.TYPE_SOUNDTRACK;
    public static final String TYPE_SPOKENWORD = ReleaseGroupWs2.TYPE_SPOKENWORD;
    public static final String TYPE_INTERVIEW = ReleaseGroupWs2.TYPE_INTERVIEW;
    public static final String TYPE_AUDIOBOOK = ReleaseGroupWs2.TYPE_AUDIOBOOK;
    public static final String TYPE_LIVE = ReleaseGroupWs2.TYPE_LIVE;
    public static final String TYPE_REMIX = ReleaseGroupWs2.TYPE_REMIX;
    public static final String TYPE_OTHER = ReleaseGroupWs2.TYPE_OTHER;

    public static final String STATUS_OFFICIAL = "official";
    public static final String STATUS_PROMOTION = "promotion";
    public static final String STATUS_BOOTLEG = "bootleg";
    public static final String STATUS_PSEUDO_RELEASE = "pseudo-release";

    private String title;
    private String disambiguation;
    private String packaging;
    private String status;
    private String quality;
    private String qualityStr;
    private String textLanguage;
    private String textScript;
    private String dateStr;
    private String countryId;
    private String barcode;
    private String asin;
    
    private ArtistCreditWs2 artistCredit;
    private ReleaseGroupWs2 releaseGroup;
    private LabelInfoListWs2 labelInfoList;
    private MediumListWs2 mediumList;
    
    // Recording is via Medium.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDisambiguation() {
        return disambiguation;
    }
    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }
    
    public String getPackaging() {
        return packaging;
    }
    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getQualityStr() {
        return qualityStr;
    }
     public void setQualityStr(String qualityStr) {
        this.qualityStr = qualityStr;
    }
    public String getQuality() {
        return quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getTextLanguage() {
        return textLanguage;
    }
    public void setTextLanguage(String textLanguage) {
        this.textLanguage = textLanguage;
    }

    public String getTextScript() {
        return textScript;
    }
    public void setTextScript(String textScript) {
        this.textScript = textScript;
    }
    
    public String getAsin() {
        return asin;
    }
    public void setAsin(String asin) {
        this.asin = asin;
    }
    
    public String getCountryId() {
        return countryId;
    }
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getArtistCreditString() {
            if (getArtistCredit()==null) return "";
            return artistCredit.getArtistCreditString();
    }
    public ArtistCreditWs2 getArtistCredit() {
            return artistCredit;
    }
    public void setArtistCredit(ArtistCreditWs2 artistCredit) {
            this.artistCredit = artistCredit;
    }

     public ReleaseGroupWs2 getReleaseGroup() {
            return releaseGroup;
    }
    public void setReleaseGroup(ReleaseGroupWs2 releaseGroupWs2) {
            this.releaseGroup = releaseGroupWs2;
    }
    public String getLabelInfoString() {
        if (labelInfoList!=null)
        {
            return labelInfoList.getLabelInfoString();
        }
        return "";
    }
    public LabelInfoListWs2 getLabelInfoList() {
            return labelInfoList;
    }
    public void setLabelInfoList(LabelInfoListWs2 labelInfoList) {
            this.labelInfoList = labelInfoList;
    }
    
    public MediumListWs2 getMediumList() {
        return mediumList;
    }

    public void setMediumList(MediumListWs2 mediumList) {
        this.mediumList = mediumList;
    }
    public String getFormat() {
       
       if  (getMediumList() == null) return "";
       
       return getMediumList().getFormat();
   } 
   public int getTracksCount(){

       if  (getMediumList() == null) return 0;
       
       return getMediumList().getTracksCount();
   }
   
    /**
    * Parses the date string and returns a Date
    * @return A Date object
    */
    public Date getDate() {
        return dateFromdateString();
    }

    private Date dateFromdateString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy");

        if (dateStr == null) 
                return null;

        if (dateStr.length() == 10) 
                f = new SimpleDateFormat("yyyy-MM-dd");

        if (dateStr.length() == 7) 
                f = new SimpleDateFormat("yyyy-MM");
        // Values retunred by musicbrainz are always in UTC!
        f.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
                return f.parse(dateStr);
        } catch (ParseException e) {
                log.warn("Could not parse date string - returning null", e);
                return null;
        }
    }
    public String getYear(){
       
        if (getDate() == null)

        {return "";}

        Date d = getDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        // Values retunred by musicbrainz are always in UTC!
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(d.getTime());
    }
    public Long getDurationInMillis(){
        
        if (getMediumList()==null ||
             getMediumList().getDurationInMillis()==null)
            
            return 0L;
        
        return getMediumList().getDurationInMillis();
    }
    public String getDuration(){
    
        if (getMediumList()==null ||
             getMediumList().getDurationInMillis()==null)
            
            return "";
        
        return getMediumList().getDuration();
    }

    public String getUniqueTitle()
    {
        if (StringUtils.isNotBlank(disambiguation)) {
                return title + " (" + disambiguation + ")";
        }
        return title;
    }

    @Override
    public String toString() {
        return getUniqueTitle();
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ReleaseWs2)) {
            return false;
        }
        ReleaseWs2 other = (ReleaseWs2) object;
        if (this.getIdUri().equals(other.getIdUri()))
        {
            return true;
        }

        return false;
    }

}
