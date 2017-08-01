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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceImplTest {
    @Mock
    private ReleaseService releaseService;

    @Mock
    private ArtistDao artistDao;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Long expectedAndroidAudioArtistId = 42L;
    private Long expectedId = 23L;

    private Artist newArtist = new Artist(expectedAndroidAudioArtistId);
    private Artist existingArtist = new Artist(expectedAndroidAudioArtistId);

    private List<Release> existingReleases = Arrays.asList(
        createRelease("existing1", existingArtist), createRelease("existing2", existingArtist));

    private List<Release> newReleases = Arrays.asList(createRelease("new1", newArtist), createRelease("nwe2", newArtist));

    @Before
    public void setup() throws Exception {
        existingArtist.setId(expectedId);
        existingArtist.setArtistName("existingArtist");
        newArtist.setArtistName("newArtist");

        when(artistDao.save(any(Artist.class))).then(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Artist param = (Artist) invocation.getArguments()[0];
                param.setId(expectedId);
                return expectedId;
            }
        });
    }

    @Test
    public void saveOrUpdateSaveNew() throws Exception {
        long actualId = artistService.saveOrUpdate(newArtist);

        assertEquals(expectedId, Long.valueOf(actualId));
        verify(artistDao).save(newArtist);
        verify(releaseService).saveOrUpdate(newReleases, false);
    }

    @Test
    public void saveOrUpdateSaveExisting() throws Exception {
        when(artistDao.findByAndroidId(expectedAndroidAudioArtistId)).thenReturn(existingArtist);

        long actualId = artistService.saveOrUpdate(newArtist);

        assertEquals(expectedId, Long.valueOf(actualId));
        verify(artistDao).update(existingArtist);
        verify(releaseService).saveOrUpdate(newReleases);
    }

    private Release createRelease(String name, Artist artist) {
        Release release = new Release();
        release.setReleaseName(name);
        release.setArtist(artist);
        artist.getReleases().add(release);
        return release;
    }
}
