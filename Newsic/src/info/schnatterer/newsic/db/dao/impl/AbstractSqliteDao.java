package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.GenericDao;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractSqliteDao<T> implements GenericDao<T> {
	// private Context context;
	private SQLiteDatabase db;
	private NewsicDatabase newsicDb = null;

	public AbstractSqliteDao(Context context) {
		// this.context = context;
		// Opens database connection
		newsicDb = new NewsicDatabase(context);
		db = newsicDb.getWritableDatabase();
	}

	/**
	 * Allows other DAOs to use this one's queries within the same database
	 * connection.
	 * 
	 * @param db
	 */
	public AbstractSqliteDao(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void open() {
		if (newsicDb != null && db != null) {
			db = newsicDb.getWritableDatabase();
		}
	}

	public void close() {
		if (newsicDb != null && db != null) {
			newsicDb.close();
			db = null;
		}
	}
	
	protected SQLiteDatabase getDb() {
		return db;
	}
	
	protected long persistDate(Date date) {
		return date.getTime();
	}

	protected Date loadDate(long date) throws DatabaseException {
		return new Date(date);
	}

	@Override
	public long save(T entity) throws DatabaseException {
		try {
			return getDb()
					.insert(getTableName(), null, toContentValues(entity));
		} catch (Throwable t) {
			throw new DatabaseException("Unable to save " + entity, t);
		}
	}

	@Override
	public int update(T entity) throws DatabaseException {
		try {
			return getDb()
					.update(getTableName(), toContentValues(entity), getId(entity).toString(), null);
		} catch (Throwable t) {
			throw new DatabaseException("Unable to update " + entity, t);
		}
	}

	public abstract ContentValues toContentValues(T t);

	public abstract String getTableName();
	
	protected abstract Long getId(T entity);
}
