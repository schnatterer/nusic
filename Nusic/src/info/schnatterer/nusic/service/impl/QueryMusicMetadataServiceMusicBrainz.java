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
package info.schnatterer.nusic.service.impl;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.QueryMusicMetadataService;
import info.schnatterer.nusic.service.ServiceException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.model.ArtistCreditWs2;
import org.musicbrainz.model.NameCreditWs2;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;

import android.annotation.SuppressLint;

import com.google.common.util.concurrent.RateLimiter;

public class QueryMusicMetadataServiceMusicBrainz implements
		QueryMusicMetadataService {
	/**
	 * MusicBrainz allows at max 22 requests in 20 seconds. However, we still
	 * get 503s then. Try 1 request per second.
	 */
	private static final double PERMITS_PER_SECOND = 1.0;
	final RateLimiter rateLimiter = RateLimiter.create(PERMITS_PER_SECOND);
	/** Application name used in user agent string of request. */
	private String appName;
	/** Application version used in user agent string of request. */
	private String appVersion;
	/**
	 * Contact URL or author email used in user agent string of request.
	 */
	private String appContact;
	/**
	 * See http://musicbrainz.org/doc/Development/XML_Web_Service/Version_2#
	 * Release_Type_and_Status
	 */
	private static final String SEARCH_BASE = "type:album";
	private static final String SEARCH_DATE_BASE = " AND date:[";
	private static final String SEARCH_DATE_TO = " TO ";
	private static final String SEARCH_DATE_OPEN_END = "?";
	private static final String SEARCH_DATE_FINAL = "]";
	private static final String SEARCH_ARTIST_1 = " AND artist:\"";
	private static final String SEARCH_ARTIST_2 = "\"";

	static {
		/*
		 * Some class are flooding our logs with warnings. Give us some space!
		 */
		Logger.getLogger("org.musicbrainz.wsxml.impl.JDOMParserWs2").setLevel(
				java.util.logging.Level.SEVERE);
	}

	/**
	 * Creates a service instance for finding releases.
	 * 
	 * @param appName
	 *            application name used in user agent string of request
	 * 
	 * @param appVersion
	 *            application version used in user agent string of request
	 * 
	 * @param appContact
	 *            contact URL or author email used in user agent string of
	 *            request
	 */
	public QueryMusicMetadataServiceMusicBrainz(String appName,
			String appVersion, String appContact) {
		this.appName = appName;
		this.appVersion = appVersion;
		this.appContact = appContact;
	}

	@SuppressLint("SimpleDateFormat")
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Artist findReleases(Artist artist, Date fromDate, Date endDate)
			throws ServiceException {
		if (artist == null || artist.getArtistName() == null) {
			return null;
		}
		String artistName = artist.getArtistName();
		Map<String, Release> releases = new HashMap<String, Release>();
		try {
			// List<ReleaseResultWs2> releaseResults = findReleases();
			org.musicbrainz.controller.Release releaseSearch = createReleaseSearch(
					appName, appVersion, appContact);
			releaseSearch.search(appendDate(fromDate, endDate,
					new StringBuffer(SEARCH_BASE)).append(SEARCH_ARTIST_1)
					.append(artistName).append(SEARCH_ARTIST_2).toString());

			// Limit request rate to avoid server bans
			rateLimiter.acquire();
			processReleaseResults(artistName, artist, releases,
					releaseSearch.getFirstSearchResultPage());

			while (releaseSearch.hasMore()) {
				// TODO check if internet connection still there?

				// Limit request rate to avoid server bans
				rateLimiter.acquire();
				processReleaseResults(artistName, artist, releases,
						releaseSearch.getNextSearchResultPage());
			}
		} catch (MBWS2Exception mBWS2Exception) {
			throw new ServiceException(
					R.string.ServiceException_errorQueryingMusicBrainz,
					mBWS2Exception, artistName);
		} catch (SecurityException securityException) {
			throw securityException;
		} catch (Throwable t) {
			throw new ServiceException(
					R.string.ServiceException_errorFindingReleasesArtist, t,
					artistName);
		}
		return artist;
	}

	public StringBuffer appendDate(Date startDate, Date endDate,
			StringBuffer stringBuffer) {
		if (startDate == null && endDate == null) {
			// Don't append anything
			return stringBuffer;
		}
		stringBuffer.append(SEARCH_DATE_BASE);
		if (startDate != null) {
			stringBuffer.append(dateFormat.format(startDate));
		} else {
			stringBuffer.append("0");
		}

		stringBuffer.append(SEARCH_DATE_TO);
		if (endDate != null) {
			stringBuffer.append(dateFormat.format(endDate));
		} else {
			stringBuffer.append(SEARCH_DATE_OPEN_END);
		}
		stringBuffer.append(SEARCH_DATE_FINAL);
		return stringBuffer;
	}

	/**
	 * Creates an instance of the release search object.
	 * 
	 * @param userAgentName
	 *            custom application name used in user agent string. If
	 *            <code>null</code>, the default user agent string is used.
	 * @param userAgentVersion
	 *            custom application version used in user agent string
	 * @param userAgentContact
	 *            contact URL or author email used in user agent string
	 * 
	 * @return a new instance of the web service implementation.
	 */
	protected org.musicbrainz.controller.Release createReleaseSearch(
			String userAgentName, String userAgentVersion,
			String userAgentContact) {
		return new org.musicbrainz.controller.Release(userAgentName,
				userAgentVersion, userAgentContact);
	}

	protected void processReleaseResults(String artistName, Artist artist,
			Map<String, Release> releases, List<ReleaseResultWs2> releaseResults) {
		for (ReleaseResultWs2 releaseResultWs2 : releaseResults) {
			// Make sure not to add other artists albums
			ReleaseWs2 releaseResult = releaseResultWs2.getRelease();
			if (releaseResult.getArtistCredit().getArtistCreditString().trim()
					.equalsIgnoreCase(artistName.trim())) {
				if (artist.getMusicBrainzId() == null
						|| artist.getMusicBrainzId().isEmpty()) {
					artist.setMusicBrainzId(getMusicBrainzId(releaseResult
							.getArtistCredit()));
				}

				// Use only the release with the "oldest" date of a release
				// group
				String releaseGroupId = releaseResult.getReleaseGroup().getId()
						.trim();
				Release existingRelease = releases.get(releaseGroupId);
				Date newDate = releaseResult.getDate();
				if (existingRelease == null) {
					Release release = new Release();
					release.setArtist(artist);
					release.setReleaseName(releaseResult.getTitle());
					release.setReleaseDate(newDate);
					release.setMusicBrainzId(releaseGroupId);

					releases.put(releaseGroupId, release);
					artist.getReleases().add(release);
					// TODO store all release dates and their countries?
				} else {
					if (existingRelease.getReleaseDate() == null
							|| (newDate != null && existingRelease
									.getReleaseDate().after(newDate))) {
						// Change date of existing release
						existingRelease.setReleaseDate(newDate);
					}
				}
			}
		}
	}

	private String getMusicBrainzId(ArtistCreditWs2 artistCredit) {
		String musicBrainzId = null;
		List<NameCreditWs2> nameCredits = artistCredit.getNameCredits();
		if (nameCredits.size() > 0 && nameCredits.get(0).getArtist() != null) {
			musicBrainzId = nameCredits.get(0).getArtist().getId();
		}
		return musicBrainzId;
	}
}
