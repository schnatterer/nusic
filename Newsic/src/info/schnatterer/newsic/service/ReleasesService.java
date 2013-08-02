package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.event.ArtistProgressListener;

import java.util.Date;
import java.util.List;

/**
 * @author schnatterer
 * 
 */
public interface ReleasesService {
	/**
	 * Updates the releases in the database. Queries only releases that are
	 * within the date range of <code>startDate</code> and <code>endDate</code>
	 * including the dates.
	 * 
	 * If both <code>startDate</code> and <code>endDate</code> are
	 * <code>null</code>, no date is specified resulting in "all" results.
	 * 
	 * @param endDate
	 *            can be <code>null</code>, which results in an "open" end.
	 * @param startDate
	 *            can be <code>null</code>, which results in a search beginning
	 *            at 0.
	 * @param sortResult
	 *            if <code>true</code>, the result is sorted by release date,
	 *            descending.
	 * @throws ServiceException
	 * @throws {@link NullPointerException} when artist is <code>null</code>
	 * @return
	 */
	List<Release> updateNewestReleases(Date startDate, Date endDate,
			boolean sortResult);

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