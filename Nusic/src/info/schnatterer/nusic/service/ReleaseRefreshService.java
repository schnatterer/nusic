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
package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.service.event.ArtistProgressListener;

/**
 * @author schnatterer
 * 
 */
public interface ReleaseRefreshService {

	/**
	 * Updates the releases in the database from the internet. The time period
	 * queried depends on
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()},
	 * {@link PreferencesService#getLastReleaseRefresh()},
	 * {@link PreferencesService#isIncludeFutureReleases()} and
	 * {@link PreferencesService#isForceFullRefresh()}.
	 * 
	 * @param updateOnlyIfNeccesary
	 *            if <code>true</code> the refresh is only done when
	 *            {@link ReleaseRefreshService#isUpdateNeccesarry()} returns
	 *            <code>true</code>. Otherwise, the refresh is done at any case.
	 * @return <code>false</code> if no update was necessary. Otherwise
	 *         <code>true</code>.
	 */
	boolean refreshReleases(boolean updateOnlyIfNeccesary);

	/**
	 * Finds out if an update is necessary. An update definitely is necessary
	 * whenever the app runs for the first time ever or in this version.
	 * 
	 * @return <code>true</code> if the app run the first time ever or the first
	 *         time this version. Otherwise returns <code>false</code>
	 */
	boolean isUpdateNeccesarry();

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

	void removeArtistProcessedListeners();

}