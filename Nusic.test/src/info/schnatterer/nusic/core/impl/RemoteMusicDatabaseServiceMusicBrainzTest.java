/* Copyright (C) 2013 Johannes Schnatterer
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *  
 * This file is part of nusic.
 * 
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core.impl;

import static org.junit.Assert.assertEquals;
import info.schnatterer.nusic.android.application.NusicApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.annotation.SuppressLint;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RemoteMusicDatabaseServiceMusicBrainzTest {

	@SuppressLint("SimpleDateFormat")
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private RemoteMusicDatabaseServiceMusicBrainz remoteMusicDatabaseServiceMusicBrainz;

	private final String expectedFromDateStr = "2013-01-01";
	private Date expectedFromDate = null;
	private final String expectedToDateStr = "9000-12-30";
	private Date expectedToDate = null;
	// private final String expectedReleaseDateStr = "2013-06-01";
	// private Date expectedReleaseDate = null;
	// private String expectedArtistName = "a";
	private String EXPECTED_STRING_BASE = " AND date:[";
	private String EXPECTED_STRING_OPEN_BEGINNING = EXPECTED_STRING_BASE
			+ "0 TO " + expectedToDateStr + "]";
	private String EXPECTED_STRING_OPEN_END = EXPECTED_STRING_BASE
			+ expectedFromDateStr + " TO ?]";
	private String EXPECTED_STRING_NO_DATES = "";
	private String EXPECTED_STRING_REGULAR_DATES = EXPECTED_STRING_BASE
			+ expectedFromDateStr + " TO " + expectedToDateStr + "]";

	@Before
	public void setUp() throws Exception {
		new NusicApplication().onCreate();

		remoteMusicDatabaseServiceMusicBrainz = new QueryMusicMetadataServiceMusicUnderTest();

		expectedFromDate = dateFormat.parse(expectedFromDateStr);
		expectedToDate = dateFormat.parse(expectedToDateStr);
		// expectedReleaseDate = dateFormat.parse(expectedReleaseDateStr);
	}

	@Test
	public void testAppendDate() {
		// Open beginning
		StringBuffer actual = new StringBuffer();
		remoteMusicDatabaseServiceMusicBrainz.appendDate(null, expectedToDate,
				actual);
		assertEquals("Unexpected result for open start date",
				EXPECTED_STRING_OPEN_BEGINNING, actual.toString());

		// Open end
		actual = new StringBuffer();
		remoteMusicDatabaseServiceMusicBrainz.appendDate(expectedFromDate,
				null, actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_OPEN_END, actual.toString());

		// No dates
		actual = new StringBuffer();
		remoteMusicDatabaseServiceMusicBrainz.appendDate(null, null, actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_NO_DATES, actual.toString());

		// Defined beginning and end dates
		actual = new StringBuffer();
		remoteMusicDatabaseServiceMusicBrainz.appendDate(expectedFromDate,
				expectedToDate, actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_REGULAR_DATES, actual.toString());
	}

	// @Test
	// public void testFindReleasesStringDate() throws ServiceException {
	// List<ReleaseWs2> mockedReleases = new LinkedList<ReleaseWs2>();
	// mockedReleases
	// .add(createRelease(expectedArtistName, "I", "2013-07-14"));
	// mockedReleases.add(createRelease(expectedArtistName, "I",
	// expectedReleaseDateStr));
	// mockedReleases.add(createRelease("b", "X", "2013-07-14"));
	// queryMusicMetadataService.setMockedReleases(mockedReleases);
	//
	// Artist artist = new Artist();
	// artist.setArtistName(expectedArtistName);
	// artist = queryMusicMetadataService.findReleases(artist,
	// expectedFromDate);
	// assertTrue("Query unexpected",
	// queryMusicMetadataService.getLastSearchText()
	// .contains(expectedFromDateStr));
	// assertTrue("Query unexpected",
	// queryMusicMetadataService.getLastSearchText()
	// .contains(expectedArtistName));
	//
	// assertEquals("Returned wrong number of releases", 1, artist
	// .getReleases().size());
	// assertEquals("Returned the wrong release", expectedReleaseDate,
	// artist
	// .getReleases().get(0).getReleaseDate());
	// }

	// @Test
	// @SuppressWarnings("serial")
	// private ReleaseWs2 createRelease(final String artistName,
	// String releaseName, String date) {
	// ReleaseWs2 release = new ReleaseWs2();
	// release.setArtistCredit(new ArtistCreditWs2(
	// new LinkedList<NameCreditWs2>() {
	// {
	// add(new NameCreditWs2("", artistName, new ArtistWs2()));
	// }
	// }));
	// release.setDateStr(date);
	// release.setTitle(releaseName);
	// return release;
	// }

	public class QueryMusicMetadataServiceMusicUnderTest extends
			RemoteMusicDatabaseServiceMusicBrainz {

		public QueryMusicMetadataServiceMusicUnderTest() {
			super(null, null, null);
		}

		private String lastSearchText;
		private List<ReleaseWs2> mockedReleases;

		public String getLastSearchText() {
			return lastSearchText;
		}

		public List<ReleaseWs2> getMockedReleases() {
			return mockedReleases;
		}

		public void setMockedReleases(List<ReleaseWs2> mockedReleases) {
			this.mockedReleases = mockedReleases;
		}

		@Override
		protected org.musicbrainz.controller.Release createReleaseSearch(
				String userAgentName, String userAgentVersion,
				String userAgentContact) {
			return new org.musicbrainz.controller.Release() {
				@Override
				public boolean hasMore() {
					return false;
				}

				@Override
				public void search(String searchText) {
					super.search(searchText);
					lastSearchText = searchText;
				}

				@Override
				public List<ReleaseResultWs2> getFirstSearchResultPage()
						throws MBWS2Exception {
					List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
					for (ReleaseWs2 releaseWs2 : mockedReleases) {
						ReleaseResultWs2 result = new ReleaseResultWs2();
						result.setRelease(releaseWs2);
						result.getEntity().setIdUri("someId");
						ret.add(result);
					}

					return ret;
				}
			};
		}

		// @Override
		// protected List<ReleaseResultWs2> findReleases(String searchText) {
		// lastSearchText = searchText;
		// List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
		// for (ReleaseWs2 releaseWs2 : mockedReleases) {
		// ReleaseResultWs2 result = new ReleaseResultWs2();
		// result.setRelease(releaseWs2);
		// ret.add(result);
		// }
		//
		// return ret;
		// }
	}
}
