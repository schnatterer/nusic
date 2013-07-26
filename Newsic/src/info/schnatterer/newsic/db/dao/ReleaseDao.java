package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;

import java.util.List;

public interface ReleaseDao extends GenericDao<Release> {

	/**
	 * Convenience method for {@link #saveOrUpdate(List)}, that checks if the
	 * artists
	 * 
	 * @param releases
	 * @throws DatabaseException
	 */
	void saveOrUpdate(List<Release> releases) throws DatabaseException;

	/**
	 * Creates new {@link Release}s or updates existing ones (matching by
	 * {@link Release#getMusicBrainzId()}). Does not update the
	 * {@link Release#getArtist()}.
	 * 
	 * @param releases
	 * @param saveArtist
	 *            <code>false</code> assumes that the artist is persisted
	 *            (increases performance). Make sure {@link Artist#getId()} is
	 *            not <code>null</code>. Otherwise the
	 *            {@link Release#getArtist()} is saved during the process, if
	 *            the it does not exist.
	 * @throws DatabaseException
	 */
	void saveOrUpdate(List<Release> releases, boolean saveArtist)
			throws DatabaseException;

	/**
	 * Finds out of if release with a specific
	 * {@link Release#getMusicBrainzId()} exists.
	 * 
	 * @param musicBrainzId
	 * @return the {@link Release#getId()} of the release if the release exists.
	 *         Otherwise <code>null</code>.
	 */
	Long findByMusicBrainzId(String musicBrainzId) throws DatabaseException;

	/**
	 * Finds all releases
	 * 
	 * @return
	 */
	List<Release> findAll() throws DatabaseException;

}
