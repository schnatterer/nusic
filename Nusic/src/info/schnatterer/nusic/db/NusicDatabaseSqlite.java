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
package info.schnatterer.nusic.db;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.util.DateUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Basic database abstraction. Handles execution of DDL scripts.
 * 
 * @author schnatterer
 * 
 */
public class NusicDatabaseSqlite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "nusic";

	/** Last app version that needed a database update. */
	private static final int DATABASE_VERSION = Constants.APP_VERSION_0_2_1;

	private static final String DATABASE_TABLE_CREATE = "CREATE TABLE ";
	private static final String DATABASE_TABLE_DROP = "DROP TABLE ";

	private static final String DATABASE_FOREIGN_KEY = ", FOREIGN KEY(";
	private static final String DATABASE_REFERENCES = ") REFERENCES ";

	private static final NusicDatabaseSqlite instance = new NusicDatabaseSqlite(
			Application.getContext());

	protected NusicDatabaseSqlite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static NusicDatabaseSqlite getInstance() {
		return instance;
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	@Override
	public synchronized void close() {
		Log.d(Constants.LOG, "Closing database");
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable(TableArtist.NAME, TableArtist.COLUMN_ID,
				TableArtist.TYPE_COLUMN_ID, TableArtist.COLUMN_ANDROID_ID,
				TableArtist.TYPE_COLUMN_ANDROID_ID, TableArtist.COLUMN_MB_ID,
				TableArtist.TYPE_COLUMN_MB_ID, TableArtist.COLUMN_NAME,
				TableArtist.TYPE_COLUMN_NAME, TableArtist.COLUMN_DATE_CREATED,
				TableArtist.TYPE_COLUMN_DATE_CREATED));
		db.execSQL(createTable(TableRelease.NAME, TableRelease.COLUMN_ID,
				TableRelease.TYPE_COLUMN_ID, TableRelease.COLUMN_MB_ID,
				TableRelease.TYPE_COLUMN_MB_ID, TableRelease.COLUMN_NAME,
				TableRelease.TYPE_COLUMN_RELEASENAME,
				TableRelease.COLUMN_RELEASEDATE_RELEASED,
				TableRelease.TYPE_COLUMN_RELEASEDATE_RELEASED,
				TableRelease.COLUMN_RELEASEDATE_CREATED,
				TableRelease.TYPE_COLUMN_RELEASEDATE_CREATED,
				TableRelease.COLUMN_RELEASEARTWORK_PATH,
				TableRelease.TYPE_COLUMN_RELEASEARTWORK_PATH,
				TableRelease.COLUMN_FK_ID_ARTIST,
				TableRelease.TYPE_COLUMN_FK_ID_ARTIST_FK));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == Constants.APP_VERSION_0_1) {
			/*
			 * In v.2.1: Column releases.TYPE_COLUMN_RELEASEMB_ID now
			 * corresponds to MusicBrainz's "release group ID" instead of the
			 * "release id". In order to make sure to avoid inconsistencies and
			 * because a real migration is way too much effort at this early
			 * point, we're just going for a clean slate.
			 */
			db.execSQL(DATABASE_TABLE_DROP + TableRelease.NAME);
			db.execSQL(createTable(TableRelease.NAME, TableRelease.COLUMN_ID,
					TableRelease.TYPE_COLUMN_ID, TableRelease.COLUMN_MB_ID,
					TableRelease.TYPE_COLUMN_MB_ID, TableRelease.COLUMN_NAME,
					TableRelease.TYPE_COLUMN_RELEASENAME,
					TableRelease.COLUMN_RELEASEDATE_RELEASED,
					TableRelease.TYPE_COLUMN_RELEASEDATE_RELEASED,
					TableRelease.COLUMN_RELEASEDATE_CREATED,
					TableRelease.TYPE_COLUMN_RELEASEDATE_CREATED,
					TableRelease.COLUMN_RELEASEARTWORK_PATH,
					TableRelease.TYPE_COLUMN_RELEASEARTWORK_PATH,
					TableRelease.COLUMN_FK_ID_ARTIST,
					TableRelease.TYPE_COLUMN_FK_ID_ARTIST_FK));
		}
	}

	/**
	 * Facilitates creation of a createTable String
	 * 
	 * @param tableName
	 *            the table name
	 * @param columnsAndTypeTuples
	 *            a multiple of two (at least two) containing the column name
	 *            and the type (can contain additional constraints)
	 * @return an SQL string that creates the table
	 */
	protected String createTable(String tableName,
			String... columnsAndTypeTuples) {
		StringBuffer sql = new StringBuffer(DATABASE_TABLE_CREATE).append(
				tableName).append("(");
		int index = 0;
		sql.append(columnsAndTypeTuples[index++]).append(" ")
				.append(columnsAndTypeTuples[index++]);
		while (index < columnsAndTypeTuples.length) {
			sql.append(", ");
			sql.append(columnsAndTypeTuples[index++]).append(" ")
					.append(columnsAndTypeTuples[index++]);
		}
		sql.append(");");
		return sql.toString();
	}

	protected static void putIfNotNull(ContentValues values, String column,
			Object value) {
		if (value == null || values == null) {
			return;
		}
		/*
		 * Thanks for not providing a put(String,Object) method to the
		 * Map<String,Object>!! :-(
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

	/**
	 * Definition and basic mappings for table corresponding to {@link Release}
	 * entity.
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class TableRelease {

		public static final String NAME = "release";

		public static final String COLUMN_ID = BaseColumns._ID;
		public static final String TYPE_COLUMN_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
		public static final int INDEX_COLUMN_RELEASEID = 0;

		public static final String COLUMN_MB_ID = "mbId";
		public static final String TYPE_COLUMN_MB_ID = "INTEGER";
		public static final int INDEX_COLUMN_MB_ID = 1;

		public static final String COLUMN_NAME = "name";
		public static final String TYPE_COLUMN_RELEASENAME = "TEXT NOT NULL";
		public static final int INDEX_COLUMN_RELEASENAME = 2;

		public static final String COLUMN_RELEASEDATE_RELEASED = "dateReleased";
		public static final String TYPE_COLUMN_RELEASEDATE_RELEASED = "INTEGER";
		public static final int INDEX_COLUMN_RELEASEDATE_RELEASED = 3;

		public static final String COLUMN_RELEASEDATE_CREATED = "dateCreated";
		public static final String TYPE_COLUMN_RELEASEDATE_CREATED = "INTEGER NOT NULL";
		public static final int INDEX_COLUMN_RELEASEDATE_CREATED = 4;

		public static final String COLUMN_RELEASEARTWORK_PATH = "artworkPath";
		public static final String TYPE_COLUMN_RELEASEARTWORK_PATH = "TEXT";
		public static final int INDEX_COLUMN_RELEASEARTWORK_PATH = 5;

		public static final String COLUMN_FK_ID_ARTIST = "fkIdArtist";
		public static final String TYPE_COLUMN_FK_ID_ARTIST = "INTEGER";
		public static final String TYPE_COLUMN_FK_ID_ARTIST_FK = TYPE_COLUMN_FK_ID_ARTIST
				+ DATABASE_FOREIGN_KEY
				+ COLUMN_FK_ID_ARTIST
				+ DATABASE_REFERENCES + NAME + "(" + COLUMN_ID + ")";
		public static final int INDEX_COLUMN_FK_ID_ARTIST = 6;

		public static final String[] COLUMNS = { COLUMN_ID, COLUMN_MB_ID,
				COLUMN_NAME, COLUMN_RELEASEDATE_RELEASED,
				COLUMN_RELEASEDATE_CREATED, COLUMN_RELEASEARTWORK_PATH,
				COLUMN_FK_ID_ARTIST };

		public static final String COLUMNS_ALL = new StringBuilder(NAME)
				.append(".").append(COLUMN_ID).append(",").append(NAME)
				.append(".").append(COLUMN_MB_ID).append(",").append(NAME)
				.append(".").append(COLUMN_NAME).append(",").append(NAME)
				.append(".").append(COLUMN_RELEASEDATE_RELEASED).append(",")
				.append(NAME).append(".").append(COLUMN_RELEASEDATE_CREATED)
				.append(",").append(NAME).append(".")
				.append(COLUMN_RELEASEARTWORK_PATH).append(",").append(NAME)
				.append(".").append(COLUMN_FK_ID_ARTIST).toString();

		public static Long toId(Cursor cursor, int startIndex) {
			return cursor.getLong(startIndex + INDEX_COLUMN_RELEASEID);
		}

		public static Release toEntity(Cursor cursor, int startIndex) {
			Release release = new Release(DateUtils.loadDate(cursor, startIndex
					+ INDEX_COLUMN_RELEASEDATE_CREATED));
			release.setId(toId(cursor, startIndex));
			release.setArtworkPath(cursor.getString(startIndex
					+ INDEX_COLUMN_RELEASEARTWORK_PATH));
			release.setMusicBrainzId(cursor.getString(startIndex
					+ INDEX_COLUMN_MB_ID));
			release.setReleaseDate(DateUtils.loadDate(cursor, startIndex
					+ INDEX_COLUMN_RELEASEDATE_RELEASED));
			release.setReleaseName(cursor.getString(startIndex
					+ INDEX_COLUMN_RELEASENAME));

			return release;
		}

		public static ContentValues toContentValues(Release release) {
			ContentValues values = new ContentValues();
			putIfNotNull(values, COLUMN_MB_ID, release.getMusicBrainzId());
			putIfNotNull(values, COLUMN_NAME, release.getReleaseName());
			putIfNotNull(values, COLUMN_RELEASEDATE_RELEASED,
					DateUtils.persistDate(release.getReleaseDate()));
			putIfNotNull(values, COLUMN_RELEASEDATE_CREATED,
					DateUtils.persistDate(release.getDateCreated()));
			putIfNotNull(values, COLUMN_RELEASEARTWORK_PATH,
					release.getArtworkPath());
			putIfNotNull(values, COLUMN_FK_ID_ARTIST, release.getArtist()
					.getId());
			return values;
		}
	}

	/**
	 * Definition and basic mappings for table corresponding to {@link Artist}
	 * entity.
	 * 
	 * @author schnatterer
	 * 
	 */
	public static class TableArtist {
		public static final String NAME = "artist";

		public static final String COLUMN_ID = BaseColumns._ID;
		public static final String TYPE_COLUMN_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
		public static final int INDEX_COLUMN_ID = 0;

		public static final String COLUMN_ANDROID_ID = "androidId";
		public static final String TYPE_COLUMN_ANDROID_ID = "INTEGER";
		public static final int INDEX_COLUMN_ANDROID_ID = 1;

		public static final String COLUMN_MB_ID = "mbId";
		public static final String TYPE_COLUMN_MB_ID = "STRING";
		public static final int INDEX_COLUMN_MB_ID = 2;

		public static final String COLUMN_NAME = "name";
		public static final String TYPE_COLUMN_NAME = "TEXT NOT NULL";
		public static final int INDEX_COLUMN_NAME = 3;

		public static final String COLUMN_DATE_CREATED = "dateCreated";
		public static final String TYPE_COLUMN_DATE_CREATED = "INTEGER NOT NULL";
		public static final int INDEX_COLUMN_DATE_CREATED = 4;

		public static final String COLUMNS_ALL = new StringBuilder(NAME)
				.append(".").append(COLUMN_ID).append(",").append(NAME)
				.append(".").append(COLUMN_ANDROID_ID).append(",").append(NAME)
				.append(".").append(COLUMN_MB_ID).append(",").append(NAME)
				.append(".").append(COLUMN_NAME).append(",").append(NAME)
				.append(".").append(COLUMN_DATE_CREATED).toString();

		public static Long toId(Cursor cursor, int startIndex) {
			return cursor.getLong(startIndex + INDEX_COLUMN_ID);
		}

		public static Artist toEntity(Cursor cursor, int startIndex) {
			Artist artist = new Artist(DateUtils.loadDate(cursor, startIndex
					+ INDEX_COLUMN_DATE_CREATED));

			artist.setId(toId(cursor, startIndex));
			artist.setAndroidAudioArtistId(cursor.getLong(startIndex
					+ INDEX_COLUMN_ANDROID_ID));
			artist.setMusicBrainzId(cursor.getString(startIndex
					+ INDEX_COLUMN_MB_ID));
			artist.setArtistName(cursor.getString(startIndex
					+ INDEX_COLUMN_NAME));
			return artist;
		}

		public static ContentValues toContentValues(Artist artist) {
			ContentValues values = new ContentValues();
			putIfNotNull(values, COLUMN_ANDROID_ID,
					artist.getAndroidAudioArtistId());
			putIfNotNull(values, COLUMN_MB_ID, artist.getMusicBrainzId());
			putIfNotNull(values, COLUMN_NAME, artist.getArtistName());
			putIfNotNull(values, COLUMN_DATE_CREATED,
					DateUtils.persistDate(artist.getDateCreated()));
			return values;
		}
	}
}
