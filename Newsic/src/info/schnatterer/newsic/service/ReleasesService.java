package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.event.ArtistProgressListener;

/**
 * @author schnatterer
 * 
 */
public interface ReleasesService {

	/**
	 * Updates the releases in the database from the internet. The time period
	 * queried depends on
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()},
	 * {@link PreferencesService#getLastReleaseRefresh()},
	 * {@link PreferencesService#isIncludeFutureReleases()} and
	 * {@link PreferencesService#isForceFullRefresh()}.
	 * 
	 * @param updateOnlyIfNeccesary
	 *            updates only if the app is started for the first time or the
	 *            first start in the current version.
	 * @return <code>false</code> if no update was necessary. Otherwise
	 *         <code>true</code>.
	 */
	boolean refreshReleases(boolean updateOnlyIfNeccesary);

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