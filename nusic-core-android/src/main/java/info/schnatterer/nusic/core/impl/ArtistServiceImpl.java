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
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core.impl;

import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.i18n.CoreMessageKey;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.model.Artist;

import javax.inject.Inject;

/**
 * Default implementation of {@link ArtistService}.
 * 
 * @author schnatterer
 *
 */
public class ArtistServiceImpl implements ArtistService {

	@Inject
	private ReleaseService releaseService;
	@Inject
	private ArtistDao artistDao;

	@Override
	public long save(Artist artist) throws ServiceException {
		try {
			long ret = artistDao.save(artist);
			releaseService.saveOrUpdate(artist.getReleases(), false);
			return ret;
		} catch (DatabaseException e) {
			throw new AndroidServiceException(
					CoreMessageKey.ERROR_WRITING_TO_DB, e);
		}
	}

	@Override
	public int update(Artist artist) throws ServiceException {
		int ret;
		try {
			ret = artistDao.update(artist);
			releaseService.saveOrUpdate(artist.getReleases());
			return ret;
		} catch (DatabaseException e) {
			throw new AndroidServiceException(
					CoreMessageKey.ERROR_WRITING_TO_DB, e);
		}
	}

	@Override
	public long saveOrUpdate(Artist artist) throws ServiceException {
		try {
			// Does artist exist?
			if (artist.getId() == null) {
				Artist existingArtist = artistDao.findByAndroidId(artist
						.getAndroidAudioArtistId());
				if (existingArtist != null) {
					artist.setId(existingArtist.getId());
					artist.setDateCreated(existingArtist.getDateCreated());
				}
			}
			if (artist.getId() == null) {
				save(artist);
			} else {
				update(artist);
			}
			return artist.getId();
		} catch (DatabaseException e) {
			throw new AndroidServiceException(
					CoreMessageKey.ERROR_WRITING_TO_DB, e);
		}
	}
}
