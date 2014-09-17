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
package info.schnatterer.nusic.db.dao;

import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.model.Artist;

public interface ArtistDao extends GenericDao<Artist> {
	/**
	 * Finds out of if artist with a specific
	 * {@link Artist#getAndroidAudioArtistId()} exists.
	 * 
	 * @param androidId
	 * @return the {@link Artist} containing ID and dateCreated, if existing.
	 *         Otherwise <code>null</code>.
	 */
	Artist findByAndroidId(long androidId)
			throws DatabaseException;

	/**
	 * Finds out of if artist with a specific
	 * {@link Artist#getAndroidAudioArtistId()} exists.
	 * 
	 * @param androidId
	 * @return the {@link Artist#getId()} of the artist if the artist exists.
	 *         Otherwise <code>null</code>.
	 * @throws DatabaseException
	 */
	Long findIdByAndroidId(Long androidAudioArtistId) throws DatabaseException;

	/**
	 * Set <code>isHidden</code> to <code>false</code> for all {@link Artist}s.
	 * 
	 * @throws DatabaseException
	 */
	void setIsHiddenFalse() throws DatabaseException;
}
