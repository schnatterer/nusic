package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.model.Release;

import java.util.List;

public interface ReleaseDao extends GenericDao<Release> {
	/**
	 * Creates new {@link Release}s or updates existing ones (matching by
	 * {@link Release#getMusicBrainzId()}).
	 * 
	 * @param artist
	 */
	void saveOrUpdate(List<Release> releases) throws DatabaseException;;

	/**
	 * Finds out of if release with a specific
	 * {@link Release#getMusicBrainzId()} exists.
	 * 
	 * @param musicBrainzId
	 * @return the {@link Release#getId()} of the release if the release exists.
	 *         Otherwise <code>null</code>.
	 */
	Long getByMusicBrainzId(String musicBrainzId) throws DatabaseException;;
}
