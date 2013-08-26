package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Artist;
import android.content.ContentResolver;

public interface ArtistQueryService {

	/**
	 * Gets the artist names from the local android database
	 * 
	 * @return
	 * @throws ServiceException
	 */
	Artist[] getArtists(ContentResolver contentResolver)
			throws ServiceException;

}