package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Artist;

import java.util.Date;

public interface QueryMusicMetadataService {

	Artist findReleases(Artist artist, Date fromDate, Date endDate) throws ServiceException;

}
