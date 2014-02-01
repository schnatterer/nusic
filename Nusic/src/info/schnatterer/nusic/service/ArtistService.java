package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.model.Artist;

/**
 * Provides access to the elements stored in Table {@link Artist}. This would be
 * the place to implement transaction handling.
 * 
 * @author schnatterer
 * 
 */
public interface ArtistService {

	long save(Artist artist) throws ServiceException;

	int update(Artist artist) throws ServiceException;

	/**
	 * Creates a new {@link Artist} or updates an existing one (matching by
	 * {@link Artist#getAndroidAudioArtistId()}).
	 * 
	 * @param artist
	 * @return
	 * @throws DatabaseException
	 */
	long saveOrUpdate(Artist artist) throws ServiceException;

}