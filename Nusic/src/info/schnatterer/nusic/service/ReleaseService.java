package info.schnatterer.nusic.service;

import info.schnatterer.nusic.db.model.Release;

import java.util.Date;
import java.util.List;

/**
 * Provides access to the elements stored in Table {@link Release}. This would
 * be the place to implement transaction handling.
 * 
 * @author schnatterer
 * 
 */
public interface ReleaseService {

	/**
	 * Convenience method for {@link #saveOrUpdate(List)}, that checks if the
	 * artists
	 * 
	 * @param releases
	 * @throws ServiceException
	 */
	void saveOrUpdate(List<Release> releases) throws ServiceException;

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
	 * @throws ServiceException
	 */
	void saveOrUpdate(List<Release> releases, boolean saveArtist)
			throws ServiceException;

	/**
	 * Saves or updates only the {@link Release}, not any related {@link Artist}
	 * .
	 * 
	 * @param release
	 * @return
	 * @throws ServiceException
	 */
	long saveOrUpdate(Release release) throws ServiceException;

	/**
	 * Finds all releases that were created after a specific date.
	 * 
	 * @param gtDateCreated
	 *            all releases whose creation data is greater than this date are
	 *            returned.
	 * 
	 * @return all releases that were created after <code>gtDateCreated</code>
	 * @throws ServiceException
	 */
	List<Release> findJustCreated(Date beforeRefresh) throws ServiceException;

}