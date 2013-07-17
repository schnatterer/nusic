package info.schnatterer.newsic.service;

import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.service.event.ArtistProcessListener;

import java.util.List;

public interface ReleasesService {

	/**
	 * Gets the artist names from the local android database
	 * 
	 * @return
	 * @throws ServiceException
	 */
	List<Artist> getArtists() throws ServiceException;

	List<Artist> getNewestReleases() throws ServiceException;

	/**
	 * @param artists
	 * @throws ServiceException
	 * @throws {@link NullPointerException} when artist is <code>null</code>
	 */
	List<Artist> addNewestReleases(List<Artist> artists)
			throws ServiceException;

	/**
	 * Adds an {@link ArtistProcessListener} to the Service. This is called
	 * whenever an {@link Artist} was processed by one of the method calls.
	 * 
	 * @param l
	 *            the <code>ArtistProcessedListener</code> to be added
	 */
	void addArtistProcessedListener(
			ArtistProcessListener artistProcessedListener);
	
	boolean removeArtistProcessedListener(
			ArtistProcessListener artistProcessedListener);
}