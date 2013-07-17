package org.musicbrainz.model.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;


/**
 * <p>Represents a release group.</p>
 * 
 */
public class ReleaseGroupWs2 extends EntityWs2 {
	
     private Log log = LogFactory.getLog(ArtistCreditWs2.class);
     
    public static final String TYPE_NONE = NS_MMD_2 + "none";
    
    public static final String TYPE_NAT = NS_MMD_2 + "nat";
    public static final String TYPE_ALBUM = NS_MMD_2 + "album";
    public static final String TYPE_SINGLE = NS_MMD_2 + "single";
    public static final String TYPE_EP = NS_MMD_2 + "ep";
    public static final String TYPE_COMPILATION = NS_MMD_2 + "compilation";
    public static final String TYPE_SOUNDTRACK = NS_MMD_2 + "soundtrack";
    public static final String TYPE_SPOKENWORD = NS_MMD_2 + "spokenword";
    public static final String TYPE_INTERVIEW = NS_MMD_2 + "interview";
    public static final String TYPE_AUDIOBOOK = NS_MMD_2 + "audiobook";
    public static final String TYPE_LIVE = NS_MMD_2 + "live";
    public static final String TYPE_REMIX = NS_MMD_2 + "remix";
    public static final String TYPE_OTHER = NS_MMD_2 + "other";

    private String type;
    private String typeString;
    private String title;
    private String firstReleaseDateStr;
    private String disambiguation;
    private ArtistCreditWs2 artistCredit;
    private ReleaseListWs2 releaseList = new ReleaseListWs2();

    /**
     * @return the typeString
     */
    public String getTypeString() {
            return typeString;
    }

    /**
     * @param typeString the typeString to set
     */
    public void setTypeString(String typeString) {
            this.typeString = typeString;
    }
     /**
     * @return the typeString
     */
    public String getType() {
            return type;
    }

    /**
     * @param typeString the typeString to set
     */
    public void setType(String type) {
            this.type = type;
    }

    /**
     * @return the title
    */
    public String getTitle() {
            return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
            this.title = title;
    }
    public String getFirstReleaseDateStr() {
        return firstReleaseDateStr;
    }

    public void setFirstReleaseDateStr(String dateStr) {
        this.firstReleaseDateStr = dateStr;
    }
       /**
     * @return the disambiguation comment
    */
    public String getDisambiguation() {
            return disambiguation;
    }

    /**
     * @param comment the disambiguation comment to set
     */
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
    public String getArtistCreditString() {
        
        if (artistCredit==null) return "";
        
        return artistCredit.getArtistCreditString();
    }
    /**
     * @return the ArtistCredit
     */
    public ArtistCreditWs2 getArtistCredit() {
            return artistCredit;
    }

    /**
     * @param artistCredit the ArtistCredit to set
     */
    public void setArtistCredit(ArtistCreditWs2 artistCredit) {
            this.artistCredit = artistCredit;
    }

    /**
     * @return the releases
     */
    public ReleaseListWs2 getReleaseList() {
            return releaseList;
    }

    /**
     * @param releases the releases to set
     */
    public void setReleaseList(ReleaseListWs2 releaseList) {
            this.releaseList = releaseList;
    }
    public void addRelease(ReleaseWs2 release) 
	{
		if (releaseList == null) {
			releaseList = new ReleaseListWs2();
		} 
		releaseList.addRelease(release);
	}
    /**
     * Gets the underlying <code>List</clode> of releases.
     * 
     * @return the releases
     */
    public List<ReleaseWs2> getReleases() {
            return ( releaseList == null ? null : releaseList.getReleases());
    }
/**
    * Parses the date string and returns a Date
    * @return A Date object
    */
    public Date getFirstReleaseDate() {
        return firstReleaseDateFromFirstReleaseDateString();
    }

    private Date firstReleaseDateFromFirstReleaseDateString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy");

        if (firstReleaseDateStr == null || firstReleaseDateStr.isEmpty()) 
                return null;

        if (firstReleaseDateStr.length() == 10) 
                f = new SimpleDateFormat("yyyy-MM-dd");

        if (firstReleaseDateStr.length() == 7) 
                f = new SimpleDateFormat("yyyy-MM");

        try {
                return f.parse(firstReleaseDateStr);
        } catch (ParseException e) {
                log.warn("Could not parse date string - returning null", e);
                return null;
        }
    }
    public String getYear(){
       
        if (getFirstReleaseDate() == null)

        {return "";}

        Date d = getFirstReleaseDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(d.getTime());
    }
    /*
    // Older method to retrieve the first release.
    
    private ReleaseWs2 getEarliestRelease() {
            
            if ( getReleaseList() == null ||
                  getReleaseList().getReleases() == null || 
                  getReleaseList().getReleases().isEmpty())
                   
                return null;
            
            List<ReleaseWs2> sorted = 
                    new ArrayList<ReleaseWs2>();
            
            for (ReleaseWs2 rel : getReleaseList().getReleases())
            {
                if (rel.getDate()!=null)
                {
                    sorted.add(rel);
                }
            }
            
            if (sorted.isEmpty()) return null;
            
            Collections.sort(sorted, new Comparator<ReleaseWs2>()
                    {
                            public int compare(ReleaseWs2 o1, ReleaseWs2 o2) {         
                                    return o1.getDate().compareTo(o2.getDate());
                            }
                    }
            );

            return sorted.get(0);
    }
   */
    @Override
    public String toString() {
            return getTitle();
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ReleaseGroupWs2)) {
            return false;
        }
        ReleaseGroupWs2 other = (ReleaseGroupWs2) object;
        if (this.getIdUri().equals(other.getIdUri()))
        {
            return true;
        }

        return false;
    }
}
