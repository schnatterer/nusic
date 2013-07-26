package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.model.Entity;

public interface GenericDao<T extends Entity> {

	long save(T entity) throws DatabaseException;

	int update(T entity) throws DatabaseException;

//	/**
//	 * Adds an {@link DataChangedListener} to the Service. This is called
//	 * whenever an <code>insert</code> or <code>update </code> method are called
//	 * on the DAO.
//	 */
//	void addDataChangedListener(DataChangedListener dataChangedListener);
//
//	boolean removeDataChangedListener(
//			DataChangedListener dataChangedListener);

}
