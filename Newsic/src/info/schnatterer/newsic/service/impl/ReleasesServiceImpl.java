package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.dao.ArtistDao;
import info.schnatterer.newsic.db.dao.impl.ArtistDaoSqlite;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ArtistQueryService;
import info.schnatterer.newsic.service.QueryMusicMetadataService;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.event.ProgressListener;
import info.schnatterer.newsic.service.event.ProgressUpdater;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

public class ReleasesServiceImpl implements ReleasesService {
	private Context context;
	private QueryMusicMetadataService queryMusicMetadataService = new QueryMusicMetadataServiceMusicBrainz();
	private ArtistQueryService artistQueryService = new ArtistQueryServiceImpl();

	private ArtistDao artistDao = null;

	private Set<ProgressListener<Artist, Void>> listenerList = new HashSet<ProgressListener<Artist, Void>>();
	private ProgressUpdater<Artist, Void> progressUpdater = new ProgressUpdater<Artist, Void>(
			listenerList) {
	};

	public ReleasesServiceImpl(Context context) {
		this.context = context;
		if (context != null) {
			this.artistDao = new ArtistDaoSqlite(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.schnatterer.newsic.service.ReleasesService#addNewestReleases
	 * (java.util.List)
	 */
	@Override
	public void updateNewestReleases(Date startDate, Date endDate) {

		// TODO create service for checking wifi and available internet
		// connection

		Artist[] artists;
		try {
			artists = getArtists();

			progressUpdater.progressStarted(artists.length);
			for (int i = 0; i < artists.length; i++) {
				Artist artist = artists[i];
				/*
				 * TODO find out if its more efficient to concat all artist in
				 * one big query and then process it page by page (keep URL
				 * limit of 2048 chars in mind)
				 */
				ServiceException potentialException = null;
				try {
					queryMusicMetadataService.findReleases(artist, startDate,
							endDate).getReleases();

					// TODO query images from lastfm
					// de.umass.lastfm.Artist artistInfo =
					// de.umass.lastfm.Artist.getInfo(artist.getArtistName(),
					// Constants.LASTFM_API_KEY);

					if (artist.getReleases().size() > 0) {
						artistDao.saveOrUpdate(artist);
						// After saving, release memory for releases

						// for (Release release : artist.getReleases()) {
						// // clear reference from release to artist as well
						// release.setArtist(null);
						// }
						artist.setReleases(null);
					}
					// Release memory for artist
					artists[i] = null;

				} catch (ServiceException e) {
					Log.w(Constants.LOG, e.getMessage(), e.getCause());
					// Allow for displaying errors to the user.
					potentialException = e;
				} catch (DatabaseException databaseException) {
					Log.w(Constants.LOG, databaseException.getMessage(),
							databaseException.getCause());
					progressUpdater
							.progressFailed(
									artist,
									i + 1,
									new ServiceException(
											R.string.ReleasesService_errorPersistingData,
											databaseException), null);
					return;
				} catch (Throwable t) {
					Log.w(Constants.LOG, t);
					progressUpdater.progressFailed(artist, i + 1, t, null);
					return;
				}

				progressUpdater.progress(artist, i + 1, potentialException);
			}
			// Success
			progressUpdater.progressFinished(null);
			return;
			// } catch (ServiceException e) {
		} catch (Throwable t) {
			Log.w(Constants.LOG, t);
			progressUpdater.progressFailed(null, 0, t, null);
			return;
		}
	}

	protected ContentResolver getContentResolver() {
		return context.getContentResolver();
	}

	@Override
	public void addArtistProcessedListener(ArtistProgressListener l) {
		listenerList.add(l);
	}

	@Override
	public boolean removeArtistProcessedListener(
			ArtistProgressListener artistProcessedListener) {
		return listenerList.remove(artistProcessedListener);
	}

	public Artist[] getArtists() throws ServiceException {
		return artistQueryService.getArtists(getContentResolver());
	}
}
