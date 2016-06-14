/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic.
 *
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.data.dao;

import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.model.Entity;

import java.util.Map;

public interface GenericDao<T extends Entity> {

	/**
	 * Stores an entity, if does not exist yet.
	 * 
	 * @param entity
	 * @return
	 * @throws DatabaseException
	 *             entity already exists, error writing data, etc.
	 */
	long save(T entity) throws DatabaseException;

	/**
	 * Updates an entity, if does exist.
	 * 
	 * @param entity
	 * @return
	 * @throws DatabaseException
	 *             entity does not exist, error writing data, etc.
	 */
	int update(T entity) throws DatabaseException;

	/**
	 * Convenience method for updating rows in the database. This is useful when
	 * updating more than one row at a time. To update only one row, consider
	 * using {@link #update(Entity)}.
	 * 
	 * @param values
	 *            a map from column names to new column values. null is a valid
	 *            value that will be translated to NULL.
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null
	 *            will update all rows.
	 * @return the number of rows affected
	 * @throws DatabaseException
	 */
	int update(Map<String, Object> values, String whereClause,
			String[] whereArgs) throws DatabaseException;
}
