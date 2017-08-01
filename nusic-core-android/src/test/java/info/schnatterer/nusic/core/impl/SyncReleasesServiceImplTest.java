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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.DeviceMusicService;
import info.schnatterer.nusic.core.PreferencesService;
import info.schnatterer.nusic.core.RemoteMusicDatabaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.event.ArtistProgressListener;
import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.model.Artist;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyncReleasesServiceImplTest {

    @Mock
    private RemoteMusicDatabaseService remoteMusicDatabaseService;

    @Mock
    private DeviceMusicService deviceMusicService;

    @Mock
    private PreferencesService preferencesService;

    @Mock
    private ArtistService artistService;

    @Mock
    private ArtistProgressListener artistProgressListener;

    private Artist artist = new Artist(42L);

    @InjectMocks
    private SyncReleasesServiceImpl syncReleasesService = new SyncReleasesServiceImpl();

    @Before
    public void setup() throws Exception {
        syncReleasesService.addArtistProcessedListener(artistProgressListener);
        when(deviceMusicService.getArtists()).thenReturn(new Artist[]{artist});
    }

    // TODO add positive test(s)

    @Test
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored") // Exceptions are passed around as parameters here
    public void syncReleasesFindReleasesFails() throws Exception {
        ServiceException expectedException = new ServiceException(new DatabaseException("mocked"));
        when(remoteMusicDatabaseService
            .findReleases(any(Artist.class), (Date) any(), (Date) any()))
            .thenThrow(expectedException);
        syncReleasesService.syncReleases();

        ArgumentCaptor<Exception> exceptionArgumentCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(artistProgressListener).onProgressFailed(
            any(Artist.class), anyInt(), anyInt(), (Boolean) any(), exceptionArgumentCaptor.capture());
        assertEquals(expectedException, exceptionArgumentCaptor.getValue().getCause());
    }




}
