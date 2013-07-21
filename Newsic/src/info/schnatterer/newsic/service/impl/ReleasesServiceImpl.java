package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.ArtistQueryService;
import info.schnatterer.newsic.service.ReleaseInfoService;
import info.schnatterer.newsic.service.ReleasesService;
import info.schnatterer.newsic.service.ServiceException;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import info.schnatterer.newsic.service.event.ProgressListener;
import info.schnatterer.newsic.service.event.ProgressUpdater;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

public class ReleasesServiceImpl implements ReleasesService {
	private Context context;
	private ReleaseInfoService releaseInfoService = new ReleaseInfoServiceMusicBrainz();
	private ArtistQueryService artistQueryService = new ArtistQueryServiceImpl();

	private Set<ProgressListener<Artist, List<Release>>> listenerList = new HashSet<ProgressListener<Artist, List<Release>>>();
	private ProgressUpdater<Artist, List<Release>> progressUpdater = new ProgressUpdater<Artist, List<Release>>(
			listenerList) {
	};

	private Date fromDate = null;

	public ReleasesServiceImpl(Context context) {
		this.context = context;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		fromDate = cal.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * info.schnatterer.testapp.service.impl.ReleasesService#getNewestReleases()
	 */
	@Override
	public List<Release> getNewestReleases() {
		return addNewestReleases(new LinkedList<Release>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.schnatterer.newsic.service.ReleasesService#addNewestReleases
	 * (java.util.List)
	 */
	@Override
	public List<Release> addNewestReleases(List<Release> releases) {
		List<Artist> artists;
		try {
			artists = getArtists();

			progressUpdater.progressStarted(artists.size());
			int artistCount = 1;
			for (Artist artist : artists) {
				/*
				 * TODO find out if its more efficient to concat all artist in
				 * one big query and then process it page by page (keep URL
				 * limit of 2048 chars in mind)
				 */
				ServiceException potentialException = null;
				try {
					List<Release> artistReleases = releaseInfoService
							.findReleases(artist.getArtistName(), fromDate)
							.getReleases();
					if (artistReleases.size() > 0) {
						artist.setReleases(artistReleases);
						releases.addAll(artistReleases);
					}
					// TODO query images from lastfm
					// de.umass.lastfm.Artist artistInfo =
					// de.umass.lastfm.Artist.getInfo(artist.getArtistName(),
					// Constants.LASTFM_API_KEY);
				} catch (ServiceException e) {
					Log.w(Constants.LOG, e.getMessage(), e.getCause());
					// Allow for displaying errors to the user.
					potentialException = e;
				} catch (Throwable t) {
					Log.w(Constants.LOG, t);
					progressUpdater.progressFailed(artist, artistCount, t,
							releases);
					return sortReleasesByDate(releases);
				}

				progressUpdater.progress(artist, artistCount++,
						potentialException);
			}
			progressUpdater.progressFinished(releases);
			return sortReleasesByDate(releases);
			// } catch (ServiceException e) {
		} catch (Throwable t) {
			Log.w(Constants.LOG, t);
			progressUpdater.progressFailed(null, 0, t, releases);
			return new LinkedList<Release>();
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

	public List<Artist> getArtists() throws ServiceException {
		return artistQueryService.getArtists(getContentResolver());
	}

	/**
	 * Sorts an instance of a list of releases.
	 * 
	 * @param releases
	 * @return the same instance as <code>releases</code>
	 */
	public List<Release> sortReleasesByDate(List<Release> releases) {
		Collections.sort(releases, Collections.reverseOrder(new Comparator<Release>() {
			public int compare(Release o1, Release o2) {
				return o1.getReleaseDate().compareTo(o2.getReleaseDate());
			}
		}));
		return releases;
	}
}
