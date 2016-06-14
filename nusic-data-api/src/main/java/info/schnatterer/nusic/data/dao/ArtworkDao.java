/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-data-api.
 *
 * nusic-data-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-data-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-data-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.data.dao;

import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.model.Release;

import java.io.InputStream;

public interface ArtworkDao {

	public enum ArtworkType {
		SMALL;
	}

	/**
	 * Stores an artwork, if does not exist yet.
	 * 
	 * @param release
	 * @param type
	 * @param artwork
	 * @return <code>true</code> if the artwork was saved, <code>false</code> if
	 *         it wasn't saved because it existed before
	 * 
	 * @throws DatabaseException
	 *             error writing data, etc.
	 */
	public boolean save(Release release, ArtworkType type, InputStream artwork)
			throws DatabaseException;

	/**
	 * Provides a stream to the artwork data.
	 * 
	 * @param release
	 * @param type
	 * @return a stream to artwork data or <code>null</code> if there is none
	 * @throws DatabaseException
	 */
	public InputStream findStreamByRelease(Release release, ArtworkType type)
			throws DatabaseException;

	boolean exists(Release release, ArtworkType type) throws DatabaseException;

	/**
	 * Provides an URI String to the artwork data, e.g.
	 * <code>file:////my/directory/myfile.ext</code>
	 * 
	 * @param release
	 * @param type
	 * @return an URI string to artwork data or <code>null</code> if there is
	 *         none
	 * @throws DatabaseException
	 */
	String findUriByRelease(Release release, ArtworkType type)
			throws DatabaseException;

}
