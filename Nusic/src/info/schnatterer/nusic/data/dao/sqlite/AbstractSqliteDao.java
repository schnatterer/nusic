/* Copyright (C) 2013 Johannes Schnatterer
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

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.data.dao.sqlite;

import info.schnatterer.nusic.data.DatabaseException;
import info.schnatterer.nusic.data.NusicDatabaseSqlite;
import info.schnatterer.nusic.data.dao.GenericDao;
import info.schnatterer.nusic.data.model.Entity;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.CancellationSignal;
import android.provider.BaseColumns;

/**
 * Wraps the {@link SQLiteDatabase} object as well as the {@link Cursor} and
 * provides {@link SQLiteDatabase}'s <code>query()</code> methods to its
 * concrete sub classes.
 * 
 * Allows for only one concurrent database query at a time, which in turn allows
 * other classes (such as {@link AsyncTaskLoader}s to close the {@link Cursor}
 * any time by calling {@link #closeCursor()}.
 * 
 * @author schnatterer
 * 
 * @param <T>
 */
public abstract class AbstractSqliteDao<T extends Entity> implements
		GenericDao<T> {
	private SQLiteDatabase db;

	private Cursor cursor = null;
	private Context context;

	public AbstractSqliteDao(Context context) {
		this.context = context;
		// Opens database connection
		db = NusicDatabaseSqlite.getInstance().getWritableDatabase();
	}

	/**
	 * Makes sure the cursor is closed.
	 */
	public void closeCursor() {
		// Close the cursor
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		cursor = null;
	}

	/**
	 * For reasons of efficiency when joining.
	 * 
	 * @param cursor
	 * @param startIndex
	 * @return
	 */
	public abstract Long toId(Cursor cursor, int startIndex);

	/**
	 * Only fills in "direct" columns. Relation (joins) must be taken care of by
	 * DAO's methods.
	 * 
	 * @param cursor
	 * @param startIndex
	 * @return
	 */
	public abstract T toEntity(Cursor cursor, int startIndex);

	public abstract ContentValues toContentValues(T t);

	public abstract String getTableName();

	protected abstract Long getId(T entity);

	@Override
	public long save(T entity) throws DatabaseException {
		try {
			long id = db.insertOrThrow(getTableName(), null,
					toContentValuesSave(entity));
			entity.setId(id);
			return id;
		} catch (Throwable t) {
			throw new DatabaseException("Unable to save " + entity, t);
		}
	}

	/**
	 * Calls {@link Entity#prePersist()} before converting to
	 * {@link ContentValues}.
	 * 
	 * @param t
	 * @return
	 */
	private ContentValues toContentValuesSave(T t) {
		t.prePersist();
		return toContentValues(t);
	}

	@Override
	public int update(T entity) throws DatabaseException {
		try {
			Long id = getId(entity);
			if (id == null) {
				throw new DatabaseException(
						"Unable to update because Id is null in entity: "
								+ entity);
			}
			return db.update(getTableName(), toContentValues(entity),
					new StringBuffer(BaseColumns._ID).append("=").append(id)
							.toString(), null);
		} catch (Throwable t) {
			throw new DatabaseException("Unable to update " + entity, t);
		}
	}

	@Override
	public int update(ContentValues values, String whereClause,
			String[] whereArgs) throws DatabaseException {
		try {
			return db.update(getTableName(), values, whereClause, whereArgs);
		} catch (Throwable t) {
			throw new DatabaseException("Unable execute update for columns: "
					+ values + "; Where: " + whereClause + ", Args: "
					+ whereArgs, t);
		}
	}

	protected Context getContext() {
		return context;
	}

	/**
	 * Create a new cursor, closing the old one first.
	 * 
	 * @param cursor
	 * @return
	 */
	private Cursor newCursor(Cursor cursor) {
		// Make sure previous resources are released
		closeCursor();
		this.cursor = cursor;
		return cursor;
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#query(boolean, String, String[], String, String[], String, String, String, String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor query(boolean distinct, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		return newCursor(db.query(distinct, table, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#query(String, String[], String, String[], String, String, String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return newCursor(db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#query(String, String[], String, String[], String, String, String, String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return newCursor(db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy, limit));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#query(boolean, String, String[], String, String[], String, String, String, String, CancellationSignal)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor query(boolean distinct, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {
		return newCursor(db.query(distinct, table, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit,
				cancellationSignal));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#queryWithFactory(CursorFactory, boolean, String, String[], String, String[], String, String, String, String, CancellationSignal)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 * 
	 */
	protected Cursor queryWithFactory(CursorFactory cursorFactory,
			boolean distinct, String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit, CancellationSignal cancellationSignal) {
		return newCursor(db.queryWithFactory(cursorFactory, distinct, table,
				columns, selection, selectionArgs, groupBy, having, orderBy,
				limit, cancellationSignal));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#queryWithFactory(CursorFactory, boolean, String, String[], String, String[], String, String, String, String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor queryWithFactory(CursorFactory cursorFactory,
			boolean distinct, String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return newCursor(db.queryWithFactory(cursorFactory, distinct, table,
				columns, selection, selectionArgs, groupBy, having, orderBy,
				limit));
	}

	/**
	 * See
	 * {@link SQLiteDatabase#rawQueryWithFactory(CursorFactory, String, String[], String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor rawQuery(String sql, String[] selectionArgs) {
		return newCursor(db.rawQuery(sql, selectionArgs));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#rawQuery(String, String[], CancellationSignal)},
	 * storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor rawQuery(String sql, String[] selectionArgs,
			CancellationSignal cancellationSignal) {
		return newCursor(db.rawQuery(sql, selectionArgs, cancellationSignal));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#rawQueryWithFactory(CursorFactory, String, String[], String)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor rawQueryWithFactory(CursorFactory cursorFactory,
			String sql, String[] selectionArgs, String editTable) {
		return newCursor(db.rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable));
	}

	/**
	 * Delegates to
	 * {@link SQLiteDatabase#rawQueryWithFactory(CursorFactory, String, String[], String, CancellationSignal)}
	 * , storing a reference of the cursor so it can be closed at any time via
	 * {@link #closeCursor()}.
	 */
	protected Cursor rawQueryWithFactory(CursorFactory cursorFactory,
			String sql, String[] selectionArgs, String editTable,
			CancellationSignal cancellationSignal) {
		return newCursor(db.rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable, cancellationSignal));
	}
}
