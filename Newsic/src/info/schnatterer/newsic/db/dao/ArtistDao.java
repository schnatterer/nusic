package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.model.Artist;

public interface ArtistDao extends GenericDao<Artist> {

	/**
	 * Creates a new {@link Artist} or updates an existing one (matching by
	 * {@link Artist#getAndroidAudioArtistId()}).
	 * 
	 * @param artist
	 * @return 
	 * @throws DatabaseException 
	 */
	long saveOrUpdate(Artist artist) throws DatabaseException;

	/**
	 * Finds out of if artist with a specific
	 * {@link Artist#getAndroidAudioArtistId()} exists.
	 * 
	 * @param androidId
	 * @return the {@link Artist#getId()} of the artist if the artist exists.
	 *         Otherwise <code>null</code>.
	 */
	Long getByAndroidId(long androidId) throws DatabaseException;

}
