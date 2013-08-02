package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Artist;

import java.util.Date;

public interface QueryMusicMetadataService {

	Artist findReleases(Artist artist, Date fromDate, Date endDate) throws ServiceException;

}
