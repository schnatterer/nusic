package info.schnatterer.nusic.core.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.model.Artist;

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

    private Long getExpectedAndroidAudioArtistId = 42L;
    private Long expectedId = 23L;

    private Artist newArtist = new Artist(getExpectedAndroidAudioArtistId);
    private Artist existingArtist = new Artist(getExpectedAndroidAudioArtistId);

    @Before
    public void setup() throws Exception {
        existingArtist.setId(expectedId);

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
    }

    @Test
    public void saveOrUpdateSaveExisting() throws Exception {
        long actualId = artistService.saveOrUpdate(existingArtist);

        assertEquals(expectedId, Long.valueOf(actualId));
        verify(artistDao).update(existingArtist);
    }

}
