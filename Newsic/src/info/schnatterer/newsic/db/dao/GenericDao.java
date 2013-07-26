package info.schnatterer.newsic.db.dao;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.model.Entity;

public interface GenericDao<T extends Entity> {

	long save(T entity) throws DatabaseException;

	int update(T entity) throws DatabaseException;

}
