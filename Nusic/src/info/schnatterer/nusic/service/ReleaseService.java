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
package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;

import java.util.List;

/**
 * Provides access to the elements stored in Table {@link Release}. This would
 * be the place to implement transaction handling.
 * 
 * @author schnatterer
 * 
 */
public interface ReleaseService {

	int update(Release release) throws ServiceException;

	/**
	 * Convenience method for {@link #saveOrUpdate(List)}, that checks if the
	 * artists
	 * 
	 * @param releases
	 * @throws ServiceException
	 */
	void saveOrUpdate(List<Release> releases) throws ServiceException;

	/**
	 * Creates new {@link Release}s or updates existing ones (matching by
	 * {@link Release#getMusicBrainzId()}). Does not update the
	 * {@link Release#getArtist()}.
	 * 
	 * @param releases
	 * @param saveArtist
	 *            <code>false</code> assumes that the artist is persisted
	 *            (increases performance). Make sure {@link Artist#getId()} is
	 *            not <code>null</code>. Otherwise the
	 *            {@link Release#getArtist()} is saved during the process, if
	 *            the it does not exist.
	 * @throws ServiceException
	 */
	void saveOrUpdate(List<Release> releases, boolean saveArtist)
			throws ServiceException;

	/**
	 * Saves or updates only the {@link Release}, not any related {@link Artist}
	 * .
	 * 
	 * @param release
	 * @return
	 * @throws ServiceException
	 */
	long saveOrUpdate(Release release) throws ServiceException;

	/**
	 * Finds all releases that were created after a specific date and that are
	 * visible.
	 * 
	 * @param gtDateCreated
	 *            all releases whose creation data is greater than this date are
	 *            returned.
	 * 
	 * @return all releases that were created after <code>gtDateCreated</code>
	 * @throws ServiceException
	 */
	List<Release> findJustCreated(long beforeRefresh) throws ServiceException;

	/**
	 * Finds all releases that are released today. That is all releases whose
	 * release date is greater than or equal today midnight and less than
	 * tomorrow midnight.
	 * 
	 * @return all releases that will be released today
	 * @throws ServiceException
	 */
	List<Release> findReleasedToday() throws ServiceException;

	/**
	 * Set <code>isHidden</code> to <code>false</code> for all {@link Release}s
	 * <b>and {@link Artist}s</b>.
	 */
	void showAll() throws ServiceException;

}