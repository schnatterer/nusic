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
package info.schnatterer.nusic.core.impl;

import static info.schnatterer.nusic.util.DateUtil.todayMidnightUtc;
import static info.schnatterer.nusic.util.DateUtil.tomorrowMidnightUtc;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.dao.ReleaseDao;
import info.schnatterer.nusic.data.dao.sqlite.ArtistDaoSqlite;
import info.schnatterer.nusic.data.dao.sqlite.ReleaseDaoSqlite;
import info.schnatterer.nusic.data.model.Release;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;

/**
 * Default implementation of {@link ReleaseService}.
 * 
 * @author schnatterer
 *
 */
public class ReleaseServiceImpl implements ReleaseService {
	@Inject
	private ReleaseDao releaseDao;
	@Inject
	private ArtistDao artistDao;
	@Inject
	private PreferencesService preferencesService;

	public ReleaseServiceImpl() {
	}

	// TODO DI remove constructors once DI is use throughout the whole system
	public ReleaseServiceImpl(Context context) {
		releaseDao = new ReleaseDaoSqlite(context);
		artistDao = new ArtistDaoSqlite(context);
		preferencesService = PreferencesServiceSharedPreferences.getInstance();
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
						Long existingArtist = artistDao
								.findIdByAndroidId(release.getArtist()
										.getAndroidAudioArtistId());
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
				Release existingRelease = releaseDao
						.findByMusicBrainzId(release.getMusicBrainzId());
				if (existingRelease != null) {
					release.setId(existingRelease.getId());
					// Never overwrite date created!
					release.setDateCreated(existingRelease.getDateCreated());
				}
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
	public List<Release> findJustCreated() throws ServiceException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,
				-preferencesService.getJustAddedTimePeriod());
		return findByDateCreatedGreaterThan(cal.getTimeInMillis());
	}

	@Override
	public List<Release> findByDateCreatedGreaterThan(long gtDateCreated)
			throws ServiceException {
		try {
			return releaseDao
					.findByDateCreatedGreaterThanAndIsHiddenNotTrue(gtDateCreated);
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorReadingFromDb, e);
		}
	}

	@Override
	public List<Release> findReleasedToday() throws ServiceException {
		try {
			/*
			 * Find all releases that are released between today 00:00:00h and
			 * before tomorrow 00:00:00h. As the dates are strored as UTC
			 * values, find out the dates in UTC first.
			 */
			// Don't care about the order
			return releaseDao
					.findByReleaseDateGreaterThanEqualsAndReleaseDateLessThanAndIsHiddenNotTrue(
							todayMidnightUtc(), tomorrowMidnightUtc());
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorReadingFromDb, e);
		}
	}

	@Override
	public List<Release> findAvailableToday(boolean isAvailable)
			throws ServiceException {
		try {
			if (isAvailable) {
				return releaseDao
						.findByReleaseDateGreaterThanEqualsAndReleaseDateLessThanAndIsHiddenNotTrue(
								createReleaseDateLowerLimit(),
								tomorrowMidnightUtc());
			} else {
				// Announced
				return releaseDao
						.findByReleaseDateGreaterThanEqualsAndIsHiddenNotTrueSortByReleaseDateAsc(tomorrowMidnightUtc());
			}
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorReadingFromDb, e);
		}
	}

	@Override
	public List<Release> findAllNotHidden() throws ServiceException {
		try {
			return releaseDao
					.findByReleaseDateGreaterThanEqualsAndIsHiddenNotTrueSortByReleaseDateDesc(createReleaseDateLowerLimit());
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorReadingFromDb, e);
		}
	}

	@Override
	public void showAll() throws ServiceException {
		try {
			releaseDao.setIsHiddenFalse();
			artistDao.setIsHiddenFalse();
		} catch (DatabaseException e) {
			throw new ServiceException(
					R.string.ServiceException_errorWritingToDb, e);
		}
	}

	/**
	 * Calculates the date where the release time period begins.<br/>
	 * 
	 * Releases tha were released in the past are only downloaded and displayed
	 * if there were release within a certain time period. This
	 * {@link PreferencesService#getDownloadReleasesTimePeriod()} is relative to
	 * the current date and shows how many months of the past are downloaded and
	 * displayed. So there is a lower limit for the release date.<br/>
	 * All releases that were release before this limit are not displayed.
	 * 
	 * @return all releases that were released before this date are not
	 *         displayed.
	 */
	private long createReleaseDateLowerLimit() {
		int months = preferencesService.getDownloadReleasesTimePeriod();
		if (months <= 0) {
			return Long.MIN_VALUE;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -months);
		return cal.getTimeInMillis();
	}
}
