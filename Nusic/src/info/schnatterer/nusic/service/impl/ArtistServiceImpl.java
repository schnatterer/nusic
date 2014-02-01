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
				artist.setId(artistDao.findByAndroidId(artist
						.getAndroidAudioArtistId()));
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
