package info.schnatterer.newsic.test.service.impl;

import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.service.impl.ReleasesServiceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import android.annotation.SuppressLint;

public class ReleasesServiceImplTest extends TestCase {

	@SuppressLint("SimpleDateFormat")
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private String oldestDateStr = "1990-01-01";
	private String mediumDateStr = "2013-07-21";
	private String newestDateStr = "2013-07-22";
	private Date oldestDate;
	private Date mediumDate;
	private Date newestDate;

	private ReleasesServiceImpl releasesService = new ReleasesServiceImpl(null);

	protected void setUp() throws Exception {
		oldestDate = dateFormat.parse(oldestDateStr);
		mediumDate = dateFormat.parse(mediumDateStr);
		newestDate = dateFormat.parse(newestDateStr);
	}

	public void testSortReleasesByDate() {
		final Release newestRelease = createRelease("a1", "r1", newestDate);
		final Release mediumRelease = createRelease("a2", "r2", mediumDate);
		final Release oldestRelease = createRelease("a3", "r3", oldestDate);
		@SuppressWarnings("serial")
		List<Release> releases = new LinkedList<Release>() {
			{
				add(mediumRelease);
				add(oldestRelease);
				add(newestRelease);
			}
		};
		// Assert unorder
		assertEquals("Unexpected initial state of release list", mediumRelease,
				releases.get(0));
		assertEquals("Unexpected initial state of release list", oldestRelease,
				releases.get(1));
		assertEquals("Unexpected initial state of release list", newestRelease,
				releases.get(2));

		// Invoke method
		releasesService.sortReleasesByDate(releases);

		// Assert ordered
		assertEquals("List order not as expected", newestRelease,
				releases.get(0));
		assertEquals("List order not as expected", mediumRelease,
				releases.get(1));
		assertEquals("List order not as expected", oldestRelease,
				releases.get(2));
	}

	@SuppressWarnings("serial")
	private Release createRelease(String artistName, String releaseName,
			Date releaseDate) {
		Artist artist = new Artist();
		artist.setArtistName(artistName);

		final Release release = new Release();
		release.setReleaseName(releaseName);
		release.setArtist(artist);
		release.setReleaseDate(releaseDate);

		artist.setReleases(new LinkedList<Release>() {
			{
				add(release);
			}
		});
		return release;
	}
}
