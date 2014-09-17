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
package info.schnatterer.nusic.service.impl;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.dao.ArtistDao;
import info.schnatterer.nusic.db.dao.sqlite.ArtistDaoSqlite;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.service.ArtistService;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;
import android.content.Context;

public class ArtistServiceImpl implements ArtistService {

	private ReleaseService releaseService;
	private ArtistDao artistDao;

	public ArtistServiceImpl(Context context) {
		releaseService = new ReleaseServiceImpl(context);
		artistDao = new ArtistDaoSqlite(context);
	}

	@Override
	public long save(Artist artist) throws ServiceException {
		try {
			long ret = artistDao.save(artist);
			releaseService.saveOrUpdate(artist.getReleases(), false);
			return ret;
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
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
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}
	}

	@Override
	public long saveOrUpdate(Artist artist) throws ServiceException {
		try {
			// Does artist exist?
			if (artist.getId() == null) {
				Artist existingArtist = artistDao
						.findByAndroidId(artist
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
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}
	}
}
