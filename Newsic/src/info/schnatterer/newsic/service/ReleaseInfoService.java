package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Artist;

import java.util.Date;

public interface ReleaseInfoService {

	Artist findReleases(String artistName, Date fromDate) throws ServiceException;

}
