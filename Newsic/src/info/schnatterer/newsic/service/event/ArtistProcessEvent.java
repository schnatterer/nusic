package info.schnatterer.newsic.service.event;

import info.schnatterer.newsic.model.Artist;

public class ArtistProcessEvent {
	private Artist artist;
	int artistNumber;
	private Throwable potentialException;

	public ArtistProcessEvent(Artist artist, int artistNumber,
			Throwable potentialException) {
		this.artist = artist;
		this.artistNumber = artistNumber;
		this.potentialException = potentialException;
	}

	public Artist getArtist() {
		return artist;
	}

	public int getArtistNumber() {
		return artistNumber;
	}

	public Throwable getPotentialException() {
		return potentialException;
	}
}
