package info.schnatterer.newsic.test.service.impl;

import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ServiceException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.NameCreditWs2;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.entity.ReleaseWs2;

import android.annotation.SuppressLint;

public class QueryMusicMetadataServiceMusicBrainzTest extends TestCase {
	@SuppressLint("SimpleDateFormat")
	private final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private QueryMusicMetadataServiceMusicBrainzMock QueryMusicMetadataService = new QueryMusicMetadataServiceMusicBrainzMock();

	private final String expectedFromDateStr = "2013-01-01";
	private Date expectedFromDate = null;
	private final String expectedReleaseDateStr = "2013-06-01";
	private Date expectedReleaseDate = null;
	private String expectedArtistName = "a";

	protected void setUp() throws Exception {
		expectedFromDate = dateFormat.parse(expectedFromDateStr);
		expectedReleaseDate = dateFormat.parse(expectedReleaseDateStr);
	}

	public void testFindReleasesStringDate() throws ServiceException {
		List<ReleaseWs2> mockedReleases = new LinkedList<ReleaseWs2>();
		mockedReleases
				.add(createRelease(expectedArtistName, "I", "2013-07-14"));
		mockedReleases.add(createRelease(expectedArtistName, "I",
				expectedReleaseDateStr));
		mockedReleases.add(createRelease("b", "X", "2013-07-14"));
		QueryMusicMetadataService.setMockedReleases(mockedReleases);

		Artist artist = new Artist();
		artist.setArtistName(expectedArtistName);
		artist = QueryMusicMetadataService.findReleases(artist,
				expectedFromDate);
		assertTrue("Query unexpected", QueryMusicMetadataService.getLastSearchText()
				.contains(expectedFromDateStr));
		assertTrue("Query unexpected", QueryMusicMetadataService.getLastSearchText()
				.contains(expectedArtistName));

		assertEquals("Returned wrong number of releases", 1, artist
				.getReleases().size());
		assertEquals("Returned the wrong release", expectedReleaseDate, artist
				.getReleases().get(0).getReleaseDate());
	}

	@SuppressWarnings("serial")
	private ReleaseWs2 createRelease(final String artistName,
			String releaseName, String date) {
		ReleaseWs2 release = new ReleaseWs2();
		release.setArtistCredit(new ArtistCreditWs2(
				new LinkedList<NameCreditWs2>() {
					{
						add(new NameCreditWs2("", artistName, new ArtistWs2()));
					}
				}));
		release.setDateStr(date);
		release.setTitle(releaseName);
		return release;
	}
}
