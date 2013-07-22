package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;

public interface GenericDao<T> {

	long save(T entity) throws DatabaseException;
	int update(T entity) throws DatabaseException;
	

}
