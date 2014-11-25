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
package info.schnatterer.nusic.core;

import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;

import java.util.Date;

/**
 * Wraps access to a remote service that provides information about releases.
 * This service hides (technical) querying of the releases and mapping the data
 * to the local entities such as {@link Release} or {@link Artist}.
 * 
 * @author schnatterer
 *
 */
public interface RemoteMusicDatabaseService {

	/**
	 * Finds releases by artist.
	 * 
	 * @param artist
	 *            the artist to query releases for.
	 * @param fromDate
	 *            the lower boundary of the time range in which release were
	 *            published
	 * @param endDate
	 *            the upper boundary of the time range in which release were
	 *            published
	 * @return an artists object that contains all the releases that were
	 *         published in the specified time range
	 * @throws ServiceException
	 */
	Artist findReleases(Artist artist, Date fromDate, Date endDate)
			throws ServiceException;
}
