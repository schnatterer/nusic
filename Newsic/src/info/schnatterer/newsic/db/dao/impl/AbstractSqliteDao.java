package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.GenericDao;
import info.schnatterer.newsic.db.model.Entity;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.util.Log;

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
	// private Context context;
	private SQLiteDatabase db;
	private NewsicDatabase newsicDb = null;

	private Cursor cursor = null;
	private Context context;

	// private Set<DataChangedListener> listeners = new
	// HashSet<DataChangedListener>();

	public AbstractSqliteDao(Context context) {
		this.context = context;
		// Opens database connection
		newsicDb = new NewsicDatabase(context);
		db = newsicDb.getWritableDatabase();
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

	protected void putIfNotNull(ContentValues values, String column,
			Object value) {
		if (value == null || values == null) {
			return;
		}
		/*
		 * Thanks for not providing a put(String,Object) method to the
		 * Map<String,Object>!!
		 */
		if (value instanceof Byte) {
			values.put(column, (Byte) value);
		} else if (value instanceof Short) {
			values.put(column, (Short) value);
		} else if (value instanceof Integer) {
			values.put(column, (Integer) value);
		} else if (value instanceof Long) {
			values.put(column, (Long) value);
		} else if (value instanceof Float) {
			values.put(column, (Float) value);
		} else if (value instanceof Double) {
			values.put(column, (Double) value);
		} else if (value instanceof Boolean) {
			values.put(column, (Boolean) value);
		} else if (value instanceof byte[]) {
			values.put(column, (byte[]) value);
		} else if (value instanceof String) {
			values.put(column, (String) value);
		} else {
			// Hope for the best and convert it to a string
			Log.w(Constants.LOG, "Column: " + column
					+ "Trying to put non primitive value to ContentValues: "
					+ value + ". Converting to string.");
			values.put(column, value.toString());
		}
	}

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

	// @Override
	// public void addDataChangedListener(DataChangedListener
	// dataChangedListener) {
	// listeners.add(dataChangedListener);
	//
	// }
	//
	// @Override
	// public boolean removeDataChangedListener(
	// DataChangedListener dataChangedListener) {
	// return listeners.remove(dataChangedListener);
	// }
	//
	// protected void notifyListeners() {
	// for (DataChangedListener listener : listeners) {
	// listener.onDataChanged();
	// }
	// }

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

	// /**
	// * Allows other DAOs to use this one's queries within the same database
	// * connection.
	// *
	// * @param db
	// */
	// public AbstractSqliteDao(SQLiteDatabase db) {
	// this.db = db;
	// }
	//
	// /**
	// * Opens the database for writing.
	// */
	// public void openDb() {
	// if (newsicDb != null && db != null) {
	// db = newsicDb.getWritableDatabase();
	// }
	// }
	//
	// /**
	// * Closes the database. Note: This might be expensive!
	// */
	// public void closeDb() {
	// if (newsicDb != null && db != null) {
	// newsicDb.close();
	// db = null;
	// }
	// }

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
