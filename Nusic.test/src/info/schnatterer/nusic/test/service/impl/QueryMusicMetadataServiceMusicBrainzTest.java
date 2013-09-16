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
package info.schnatterer.nusic.test.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import android.annotation.SuppressLint;

public class QueryMusicMetadataServiceMusicBrainzTest extends TestCase {
	@SuppressLint("SimpleDateFormat")
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private QueryMusicMetadataServiceMusicBrainzMock queryMusicMetadataService = new QueryMusicMetadataServiceMusicBrainzMock();

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

	protected void setUp() throws Exception {
		expectedFromDate = dateFormat.parse(expectedFromDateStr);
		expectedToDate = dateFormat.parse(expectedToDateStr);
		// expectedReleaseDate = dateFormat.parse(expectedReleaseDateStr);
	}

	public void testAppendDate() {
		// Open beginning
		StringBuffer actual = new StringBuffer();
		queryMusicMetadataService.appendDate(null, expectedToDate, actual);
		assertEquals("Unexpected result for open start date",
				EXPECTED_STRING_OPEN_BEGINNING, actual.toString());

		// Open end
		actual = new StringBuffer();
		queryMusicMetadataService.appendDate(expectedFromDate, null, actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_OPEN_END, actual.toString());

		// No dates
		actual = new StringBuffer();
		queryMusicMetadataService.appendDate(null, null, actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_NO_DATES, actual.toString());

		// Defined beginning and end dates
		actual = new StringBuffer();
		queryMusicMetadataService.appendDate(expectedFromDate, expectedToDate,
				actual);
		assertEquals("Unexpected result for open end date",
				EXPECTED_STRING_REGULAR_DATES, actual.toString());
	}

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
}
