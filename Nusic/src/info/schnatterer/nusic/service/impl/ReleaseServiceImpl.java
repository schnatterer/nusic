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
import info.schnatterer.nusic.db.dao.ReleaseDao;
import info.schnatterer.nusic.db.dao.sqlite.ArtistDaoSqlite;
import info.schnatterer.nusic.db.dao.sqlite.ReleaseDaoSqlite;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;

import java.util.Date;
import java.util.List;

import android.content.Context;

public class ReleaseServiceImpl implements ReleaseService {
	private ReleaseDao releaseDao;
	private ArtistDao artistDao;

	public ReleaseServiceImpl(Context context) {
		releaseDao = new ReleaseDaoSqlite(context);
		artistDao = new ArtistDaoSqlite(context);
	}

	@Override
	public int update(Release release) throws ServiceException {
		try {
			return releaseDao.update(release);
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}
	}

	@Override
	public void saveOrUpdate(List<Release> releases) throws ServiceException {
		saveOrUpdate(releases, true);
	}

	@Override
	public void saveOrUpdate(List<Release> releases, boolean saveArtist)
			throws ServiceException {

		if (releases.size() == 0) {
			return;
		}

		for (Release release : releases) {
			try {
				/* Get existing artist */
				if (release.getArtist() == null && saveArtist) {
					if (release.getArtist().getId() == null) {
						Long existingArtist = artistDao.findByAndroidId(release
								.getArtist().getAndroidAudioArtistId());
						if (existingArtist == null) {
							artistDao.save(release.getArtist());
						}
					}
				}

				saveOrUpdate(release);
			} catch (Throwable t) {
				throw new ServiceException(
						R.string.ServiceException_errorWritingToDb, t);
			}
		}
	}

	@Override
	public long saveOrUpdate(Release release) throws ServiceException {
		// Does release exist?
		try {
			if (release.getId() == null) {
				release.setId(releaseDao.findByMusicBrainzId(release
						.getMusicBrainzId()));
			}
			if (release.getId() == null) {
				releaseDao.save(release);
			} else {
				releaseDao.update(release);
			}
			return release.getId();
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}
	}

	@Override
	public List<Release> findJustCreated(Date gtDateCreated)
			throws ServiceException {
		try {
			return releaseDao.findJustCreated(gtDateCreated);
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorReadingFromDb, e);
		}
	}

	@Override
	public void showAll() throws ServiceException {
		try {
			releaseDao.showAll();
			artistDao.showAll();
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}

	}
}
