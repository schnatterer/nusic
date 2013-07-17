package org.musicbrainz.model.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.model.LifeSpanWs2;
import org.musicbrainz.model.entity.listelement.ReleaseListWs2;

/**
 * <p>Represents a  Label.</p>
 */
public class LabelWs2 extends EntityWs2 {

           public static final String TYPE_NONE = NS_MMD_2 + "None";
	public static final String TYPE_DISTRIBUTOR = NS_MMD_2 + "Distributor";
	public static final String TYPE_HOLDING = NS_MMD_2 + "Holding";
           public static final String TYPE_PRODUCTION = NS_MMD_2 + "Production";
           public static final String TYPE_ORIGINAL_PRODUCTION = NS_MMD_2 + "Original Production";
           public static final String TYPE_BOOTLEG_PRODUCTION = NS_MMD_2 + "Bootleg Production";
           public static final String TYPE_REISSUE_PRODUCTION = NS_MMD_2 + "Reissue Production";
	public static final String TYPE_PUBLISHER = NS_MMD_2 + "Publisher";
           public static final String TYPE_OTHER = NS_MMD_2 + "Other";
           
           /**
	 * The alabel's type (as an absolute URI).
	 */
           private String type;
    
	/**
	 * The label's name.
	 */
	private String name;
	
	/**
	 * The sort name is the label's name in a special format which
	 * is better suited for lexicographic sorting. The MusicBrainz
	 * style guide specifies this format.
	 */
	private String sortName;
	
	private LifeSpanWs2 lifespan;
	
	/**
	 * The disambiguation attribute may be used if there is more
	 * than one label with the same name. In this case,
	 * disambiguation attributes are added to the label names
	 * to keep them apart.
	 * 
	 */
	private String disambiguation;
	
            /**
	 * The label's country.
	 */
           private String country;
           /**
	 * The label's code (without the "LC-" prefix).
	 */
           private String labelCode;
            /**
	 * The label's ipi.
	 */
           private String ipi;
	/**
	 * The list of releases from this label.
	 *
	 */
	private ReleaseListWs2 releaseList = new ReleaseListWs2();

          /**
	 * @return the name
	 */
           
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
           /**
	 * @return the sortName
	 */
	public String getSortName() {
		return sortName;
	}
	/**
	 * @param sortName the sortName to set
	 */
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	/**
	 * @return the disambiguation
	 */
	public String getDisambiguation() {
		return disambiguation;
	}
	/**
	 * @param disambiguation the disambiguation to set
	 */
	public void setDisambiguation(String disambiguation) {
		this.disambiguation = disambiguation;
	}
        
   	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
         * @return the LifeSpan
	*/
           public LifeSpanWs2 getLifeSpan(){
               return lifespan;
           }
           /**
	 * @param LifeSpan the LifeSpan to set
        */
           public void setLifeSpan(LifeSpanWs2 lifespan){
               this.lifespan=lifespan;
           }
           /**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
        /**
	 * @return the country
	 */
	public String getLabelCode() {
		return labelCode;
	}
	/**
	 * @param country the country to set
	 */
	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}
        	/**
         * @return the ipi
         */
            public String getIpi() {
                return ipi;
            }
            /**
             * @param ipi the ipi to set
             */
            public void setIpi(String ipi) {
                this.ipi = ipi;
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
	 * Sets the underlying <code>List</clode> of releases.
	 * 
	 * Note: This will implicitly create a new {@link #releaseList}
	 * if it is null.
	 * 
	 * @param releases the releases to set
	 */
	public void setReleases(List<ReleaseWs2> releases) 
	{
		if (releaseList == null) {
			releaseList = new ReleaseListWs2();
		}
			
		this.releaseList.setReleases(releases);
	}
            /**
	 * @return the releaseList
	 */
	public ReleaseListWs2 getReleaseList() {
		return releaseList;
	}

	/**
	 * @param releaseList the releaseList to set
	 */
	public void setReleaseList(ReleaseListWs2 releaseList) {
		this.releaseList = releaseList;
	}
            /**
	 * <p>Adds a release to the underlying <code>List</clode>
	 * of releases.</p>
	 * 
	 * <p><em>Note: This will implicitly create a new {@link #releaseList}
	 * if it is null.</em></p>
	 * 
	 * @param release The {@link ReleaseWs1} to add.
	 */
	public void addRelease(ReleaseWs2 release) 
	{
		if (releaseList == null) {
			releaseList = new ReleaseListWs2();
		} 
		releaseList.addRelease(release);
	}
        
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	/**
	 * Returns a unique label name using disambiguation
	 * (if available).
	 * 
	 * This method returns the label name together with the
	 * disambiguation attribute in parenthesis if it exists.
	 * Example: 'Vixen (Hip-hop)'.
	 */
	public String getUniqueName()
	{
		if (StringUtils.isNotBlank(disambiguation)) {
			return name + " (" + disambiguation + ")";
		}
		return name;
	}
	@Override
	public String toString() {
		return getUniqueName();
	}
            @Override
            public boolean equals(Object object) {
                if (!(object instanceof LabelWs2)) {
                    return false;
                }
                LabelWs2 other = (LabelWs2) object;
                if (this.getIdUri().equals(other.getIdUri()))
                {
                    return true;
                }

                return false;
            }
}
