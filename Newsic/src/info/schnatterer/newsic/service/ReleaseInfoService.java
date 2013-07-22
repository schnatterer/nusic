package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Artist;

import java.util.Date;

public interface ReleaseInfoService {

	Artist findReleases(Artist artist, Date fromDate) throws ServiceException;

}
