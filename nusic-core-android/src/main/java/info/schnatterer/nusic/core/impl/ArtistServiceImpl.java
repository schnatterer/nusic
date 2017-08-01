/**
 * Copyright (C) 2013 Johannes Schnatterer
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.i18n.CoreMessageKey;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;

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
            throw new AndroidServiceException(CoreMessageKey.ERROR_WRITING_TO_DB, e);
        }
    }

    @Override
    public int update(Artist artist) throws ServiceException {
        return update(artist, null);
    }

    @Override
    public long saveOrUpdate(Artist artist) throws ServiceException {
        try {
            Artist existingArtist = artistDao.findByAndroidId(artist.getAndroidAudioArtistId());
            if (existingArtist != null) {
                artist.setId(existingArtist.getId());
                artist.setDateCreated(existingArtist.getDateCreated());
                update(artist, existingArtist.getReleases());
            } else {
                save(artist);
            }
            return artist.getId();
        } catch (DatabaseException e) {
            throw new AndroidServiceException(CoreMessageKey.ERROR_WRITING_TO_DB, e);
        }
    }

    private int update(Artist artist, List<Release> existingReleases) throws ServiceException {
        int ret;
        try {
            if (existingReleases != null) {
                deleteSuperfluousReleases(existingReleases, artist.getReleases());
            }
            ret = artistDao.update(artist);
            // TODO do we need to save the artist here? i.e. shouldn't the second parameter be false?
            releaseService.saveOrUpdate(artist.getReleases());
            return ret;
        } catch (DatabaseException e) {
            throw new AndroidServiceException(CoreMessageKey.ERROR_WRITING_TO_DB, e);
        }
    }

    // TODO move to releaseService?
    private void deleteSuperfluousReleases(List<Release> existingReleases, List<Release> newReleases) throws ServiceException {
        Set<String> newReleaseIds = toSetOfMusicBrainzIds(newReleases);

        deleteReleasesNotContainedIn(existingReleases, newReleaseIds);
    }

    private void deleteReleasesNotContainedIn(List<Release> existingReleases, Set<String> newReleaseIds) throws ServiceException {
        for (Release existingRelease : existingReleases) {
            if (!newReleaseIds.contains(existingRelease.getMusicBrainzId())) {
                releaseService.delete(existingRelease);
            }
        }
    }

    private Set<String> toSetOfMusicBrainzIds(List<Release> releases) {
        Set<String> newReleaseIds = new HashSet<>(releases.size());
        for (Release newRelease : releases) {
            newReleaseIds.add(newRelease.getMusicBrainzId());
        }
        return newReleaseIds;
    }
}
