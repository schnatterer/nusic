/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.core;

import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.model.Artist;

/**
 * Provides access to the {@link Artist}s stored locally. This would be the
 * place to implement transaction handling.
 * 
 * @author schnatterer
 */
public interface ArtistService {

	long save(Artist artist) throws ServiceException;

	int update(Artist artist) throws ServiceException;

	/**
	 * Creates a new {@link Artist} or updates an existing one (matching by
	 * {@link Artist#getAndroidAudioArtistId()}).
	 * 
	 * @param artist
	 * @return
	 * @throws DatabaseException
	 */
	long saveOrUpdate(Artist artist) throws ServiceException;

}