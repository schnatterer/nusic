package org.musicbrainz.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.musicbrainz.model.entity.ArtistWs2;

/**
 * <p>A single Element (artist or artist name variation) 
 * of an Artist Credit
 */
public class NameCreditWs2 
{
    private Log log = LogFactory.getLog(NameCreditWs2.class);

    String joinphrase=null;
    String name=null;
    ArtistWs2 artist=null;
    /**
   * Default Constructor
   */
    public NameCreditWs2()
    {

    }
	
    /**
    * Complete Constructor
    * @param joinphrase A string containing the join sentence used to build 
    * the Artist Credit String ((i.e. &,/, and,...)
    * @param name A string containing the name variation used to build 
    * the Artist Credit String 
    * @param artist the ArtistWs2 referred by the element.
    */
    public NameCreditWs2(String joinphrase,
                                             String name,
                                             ArtistWs2 artist)
    {
        this.joinphrase = joinphrase;
        this.name = name;
        this.artist = artist;
    }
    public String getNameCreditString(){
        
        String credit="";
        
        if (name != null && !name.equals(""))
        {
            credit = name;
        }
        else if (artist !=null)
        {
            credit = artist.getName();
        }
        
        if (joinphrase!=null&& !joinphrase.equals(""))
        {
            credit= credit+joinphrase;
        }
        return credit;
    }
    public String getJoinphrase() {
        return joinphrase;
    }
    public void setJoinphrase(String joinphrase) {
        this.joinphrase = joinphrase;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getArtistName() {
        if (getArtist()==null) return "";
        return getArtist().getUniqueName();
    }
    public ArtistWs2 getArtist() {
        return artist;
    }
    public void setArtist(ArtistWs2 artist) {
        this.artist = artist;
    }
    @Override
    public String toString() {

        return getNameCreditString();
    }
}
