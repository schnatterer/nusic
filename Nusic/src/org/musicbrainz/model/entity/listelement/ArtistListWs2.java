package org.musicbrainz.model.entity.listelement;

import org.musicbrainz.wsxml.element.ListElement;
import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.ArtistWs2;


/**
 * A list of Artists
 * 
 */
public class ArtistListWs2 extends ListElement{

    protected List<ArtistWs2> artists = new ArrayList<ArtistWs2>();

	/**
	 * @return the artists
	 */
	public List<ArtistWs2> getArtists() {
		return artists;
	}

	/**
	 * @param artists the artists to set
	 */
	public void setArtist(List<ArtistWs2> artists) {
		this.artists = artists;
	}
	
	/**
	 * Convenience method to adds an artist to the list.
	 * 
	 * This will create a new <code>ArrayList</code> if {@link #artists} is null.
	 * 
	 * @param artist
	 */
	public void addArtist(ArtistWs2 artist) 
	{
		if (artists == null) {
			artists = new ArrayList<ArtistWs2>();
		}
		artists.add(artist);
	}
           public void addAllArtists(List<ArtistWs2> artistList) 
           {
                if (artists == null) {
                        artists = new ArrayList<ArtistWs2>();
                }

                artists.addAll(artistList);
           }
}
