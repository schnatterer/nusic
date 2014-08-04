package info.schnatterer.nusic.db.dao;

import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.model.Release;

import java.io.InputStream;

public interface ArtworkDao {

	public enum ArtworkType {
		SMALL;
	}

	/**
	 * Stores an artwork, if does not exist yet.
	 * 
	 * @param release
	 * @param type
	 * @param artwork
	 * @return <code>true</code> if the artwork was saved, <code>false</code> if
	 *         it wasn't saved because it existed before
	 * 
	 * @throws DatabaseException
	 *             error writing data, etc.
	 */
	public boolean save(Release release, ArtworkType type, InputStream artwork)
			throws DatabaseException;

	/**
	 * Provides a stream to the artwork data.
	 * 
	 * @param release
	 * @param type
	 * @return a stream to artwork data or <code>null</code> if there is none
	 * @throws DatabaseException
	 */
	public InputStream findByRelease(Release release, ArtworkType type)
			throws DatabaseException;

	boolean exists(Release release, ArtworkType type) throws DatabaseException;

}
