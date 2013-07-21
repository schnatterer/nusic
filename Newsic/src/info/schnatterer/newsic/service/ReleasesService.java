package info.schnatterer.newsic.service;

import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.model.Release;
import info.schnatterer.newsic.service.event.ArtistProgressListener;

import java.util.List;

public interface ReleasesService {

	List<Release> getNewestReleases();

	/**
	 * @param releases
	 * @throws ServiceException
	 * @throws {@link NullPointerException} when artist is <code>null</code>
	 *         @return
	 */
	List<Release> addNewestReleases(List<Release> releases);

	/**
	 * Adds an {@link ArtistProgressListener} to the Service. This is called
	 * whenever an {@link Artist} was processed by one of the method calls.
	 * 
	 * @param l
	 *            the <code>ProgressListener</code> to be added
	 */
	void addArtistProcessedListener(
			ArtistProgressListener artistProcessedListener);

	boolean removeArtistProcessedListener(
			ArtistProgressListener artistProcessedListener);
}