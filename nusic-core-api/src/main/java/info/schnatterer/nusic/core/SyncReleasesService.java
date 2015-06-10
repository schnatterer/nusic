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
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core;

import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.core.event.ArtistProgressListener;

/**
 * Service that realizes the logic for getting all artists from the local device
 * (see {@link DeviceMusicService}) and synchronizing all their releases with
 * the some remote service (see {@link RemoteMusicDatabaseService}).
 * 
 * @author schnatterer
 * 
 */
public interface SyncReleasesService {

	/**
	 * Synchronizes the releases in the local database with the one from the
	 * internet. The time period queried depends on
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()},
	 * {@link PreferencesService#getLastReleaseRefresh()},
	 * {@link PreferencesService#isIncludeFutureReleases()} and
	 * {@link PreferencesService#isForceFullRefresh()}.
	 * 
	 */
	void syncReleases();

	/**
	 * Adds an {@link ArtistProgressListener} to the Service. This is called
	 * whenever an {@link Artist} was processed by one of the method calls.
	 * 
	 * @param artistProcessedListener
	 *            the progress lListener to be added
	 */
	void addArtistProcessedListener(
			ArtistProgressListener artistProcessedListener);

	/**
	 * Return an {@link ArtistProgressListener} from the service.
	 * 
	 * @param artistProcessedListener
	 *            the progress lListener to be removed
	 * @return <code>true</code> if the listener was present and is now removed.
	 *         <code>false</code> otherwise.
	 */
	boolean removeArtistProcessedListener(
			ArtistProgressListener artistProcessedListener);

	/**
	 * Removes all process listeners.
	 */
	void removeArtistProcessedListeners();

}