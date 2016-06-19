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
package info.schnatterer.nusic.data;

import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;
import info.schnatterer.nusic.data.util.SqliteUtil;
import info.schnatterer.nusic.util.DateUtil;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.ContextSingleton;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Basic database abstraction. Handles execution of DDL scripts.
 * 
 * @author schnatterer
 * 
 */

@ContextSingleton
public class NusicDatabaseSqlite extends SQLiteOpenHelper {
    private static final Logger LOG = LoggerFactory
            .getLogger(NusicDatabaseSqlite.class);

    private static final String DATABASE_NAME = "nusic";

    /** Last app version that needed a database update. */
    private static final int DATABASE_VERSION = SqliteDatabaseVersion.V6;

    private static final String DATABASE_TABLE_CREATE = "CREATE TABLE ";
    private static final String DATABASE_TABLE_DROP = "DROP TABLE ";

    private static final String DATABASE_FOREIGN_KEY = "FOREIGN KEY(";
    private static final String DATABASE_REFERENCES = "REFERENCES ";

    @Inject
    private static Provider<Context> contextProvider;

    public NusicDatabaseSqlite() {
        super(contextProvider.get(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public synchronized void close() {
        LOG.debug("Closing database");
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableArtist.create());
        db.execSQL(TableRelease.create());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == SqliteDatabaseVersion.V1) {
            /*
             * In v.2.1: Column releases.TYPE_COLUMN_RELEASEMB_ID now
             * corresponds to MusicBrainz's "release group ID" instead of the
             * "release id". In order to make sure to avoid inconsistencies and
             * because a real migration is way too much effort at this early
             * point, we're just going for a clean slate.
             */
            db.execSQL(DATABASE_TABLE_DROP + TableRelease.NAME);
            db.execSQL(TableRelease.create());
        }
        if (oldVersion < SqliteDatabaseVersion.V4) {
            // Just append new columns, they can be null
            db.execSQL(addColumn(TableArtist.NAME,
                    TableArtist.COLUMN_IS_HIDDEN,
                    TableArtist.TYPE_COLUMN_IS_HIDDEN));
            db.execSQL(addColumn(TableRelease.NAME,
                    TableRelease.COLUMN_IS_HIDDEN,
                    TableRelease.TYPE_COLUMN_IS_HIDDEN));
        }
        if (oldVersion < SqliteDatabaseVersion.V5) {
            /*
             * Set all release dates to null. They are filled again on app
             * start.
             */
            ContentValues contentValues = new ContentValues();
            contentValues
                    .put(TableRelease.COLUMN_DATE_RELEASED, (Integer) null);
            db.update(TableRelease.NAME, contentValues, null, null);
        }
        if (oldVersion < SqliteDatabaseVersion.V6) {
            db.execSQL(addColumn(TableRelease.NAME,
                    TableRelease.COLUMN_COVERARTARCHIVE_ID,
                    TableRelease.TYPE_COLUMN_COVERARTARCHIVE_ID));
        }
        // When changing the database, don't forget to create a new version
    }

    /**
     * Adds a column at the end of a table.
     * 
     * @param tableName
     *            table where to add the new column
     * @param columnName
     *            new column name
     * @param columnType
     *            type of new column
     * 
     * @return the SQL string for appending a column to a table
     */
    private String addColumn(String tableName, String columnName,
            String columnType) {
        return new StringBuffer("ALTER TABLE ").append(tableName)
                .append(" ADD ").append(columnName).append(" ")
                .append(columnType).toString();
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
    static String createTable(String tableName, String... columnsAndTypeTuples) {
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

    public static interface SqliteDatabaseVersion {
        /**
         * Very first release of this app
         */
        int V1 = 1;
        /**
         * Update DB schema because release.musicbrainzId is now stores the
         * releaseGroupId.
         */
        int V3 = 3;
        /**
         * Added Artist.isHidden and Release.isHidden
         */
        int V4 = 4;
        /**
         * Reset {@link TableRelease#COLUMN_DATE_RELEASED} due to invalid values
         * (no UTC values)
         */
        int V5 = 5;
        /** Added column for release cover art archive ID. */
        int V6 = 6;
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

        public static final String COLUMN_DATE_RELEASED = "dateReleased";
        public static final String TYPE_COLUMN_RELEASEDATE_RELEASED = "INTEGER";
        public static final int INDEX_COLUMN_RELEASEDATE_RELEASED = 3;

        public static final String COLUMN_DATE_CREATED = "dateCreated";
        public static final String TYPE_COLUMN_RELEASEDATE_CREATED = "INTEGER NOT NULL";
        public static final int INDEX_COLUMN_RELEASEDATE_CREATED = 4;

        public static final String COLUMN_RELEASEARTWORK_PATH = "artworkPath";
        public static final String TYPE_COLUMN_RELEASEARTWORK_PATH = "TEXT";
        public static final int INDEX_COLUMN_RELEASEARTWORK_PATH = 5;

        public static final String COLUMN_FK_ID_ARTIST = "fkIdArtist";
        public static final String TYPE_COLUMN_FK_ID_ARTIST = "INTEGER";
        public static final String TYPE_COLUMN_FK_ID_CONSTRAINT_FK = DATABASE_FOREIGN_KEY
                + COLUMN_FK_ID_ARTIST + ")";
        public static final String TYPE_COLUMN_FK_ID_CONSTRAINT_REFERENCES = DATABASE_REFERENCES
                + NAME + "(" + COLUMN_ID + ")";
        public static final int INDEX_COLUMN_FK_ID_ARTIST = 6;

        public static final String COLUMN_IS_HIDDEN = "isHidden";
        public static final String TYPE_COLUMN_IS_HIDDEN = "INTEGER";
        public static final int INDEX_COLUMN_IS_HIDDEN = 7;

        public static final String COLUMN_COVERARTARCHIVE_ID = "coverArtArchiveId";
        public static final String TYPE_COLUMN_COVERARTARCHIVE_ID = "INTEGER";
        public static final int INDEX_COLUMN_COVERARTARCHIVE_ID = 8;

        public static final List<String> COLUMNS = Collections.unmodifiableList(
                Arrays.asList(COLUMN_ID, COLUMN_MB_ID,
                COLUMN_NAME, COLUMN_DATE_RELEASED, COLUMN_DATE_CREATED,
                COLUMN_RELEASEARTWORK_PATH, COLUMN_FK_ID_ARTIST,
                COLUMN_IS_HIDDEN, COLUMN_COVERARTARCHIVE_ID));

        public static final String COLUMNS_ALL = new StringBuilder(NAME)
                .append(".").append(COLUMN_ID).append(",").append(NAME)
                .append(".").append(COLUMN_MB_ID).append(",").append(NAME)
                .append(".").append(COLUMN_NAME).append(",").append(NAME)
                .append(".").append(COLUMN_DATE_RELEASED).append(",")
                .append(NAME).append(".").append(COLUMN_DATE_CREATED)
                .append(",").append(NAME).append(".")
                .append(COLUMN_RELEASEARTWORK_PATH).append(",").append(NAME)
                .append(".").append(COLUMN_FK_ID_ARTIST).append(",")
                .append(NAME).append(".").append(COLUMN_IS_HIDDEN).append(",")
                .append(NAME).append(".").append(COLUMN_COVERARTARCHIVE_ID)
                .toString();

        public static Long toId(Cursor cursor, int startIndex) {
            return cursor.getLong(startIndex + INDEX_COLUMN_RELEASEID);
        }

        public static Release toEntity(Cursor cursor, int startIndex) {
            Release release = new Release(SqliteUtil.loadDate(cursor,
                    startIndex + INDEX_COLUMN_RELEASEDATE_CREATED));
            release.setId(toId(cursor, startIndex));
            // release.setArtworkPath(cursor.getString(startIndex
            // + INDEX_COLUMN_RELEASEARTWORK_PATH));
            release.setMusicBrainzId(cursor.getString(startIndex
                    + INDEX_COLUMN_MB_ID));
            release.setReleaseDate(SqliteUtil.loadDate(cursor, startIndex
                    + INDEX_COLUMN_RELEASEDATE_RELEASED));
            release.setReleaseName(cursor.getString(startIndex
                    + INDEX_COLUMN_RELEASENAME));
            release.setHidden(SqliteUtil.loadBoolean(cursor,
                    INDEX_COLUMN_IS_HIDDEN));
            release.setCoverartArchiveId(cursor
                    .getLong(INDEX_COLUMN_COVERARTARCHIVE_ID));

            return release;
        }

        public static ContentValues toContentValues(Release release) {
            ContentValues values = new ContentValues();
            SqliteUtil.putIfNotNull(values, COLUMN_MB_ID,
                    release.getMusicBrainzId());
            SqliteUtil.putIfNotNull(values, COLUMN_NAME,
                    release.getReleaseName());
            SqliteUtil.putIfNotNull(values, COLUMN_DATE_RELEASED,
                    DateUtil.toLong(release.getReleaseDate()));
            SqliteUtil.putIfNotNull(values, COLUMN_DATE_CREATED,
                    DateUtil.toLong(release.getDateCreated()));
            // SqliteUtil.putIfNotNull(values, COLUMN_RELEASEARTWORK_PATH,
            // release.getArtworkPath());
            SqliteUtil.putIfNotNull(values, COLUMN_FK_ID_ARTIST, release
                    .getArtist().getId());
            SqliteUtil.putIfNotNull(values, COLUMN_IS_HIDDEN,
                    release.isHidden());
            SqliteUtil.putIfNotNull(values, COLUMN_COVERARTARCHIVE_ID,
                    release.getCoverartArchiveId());
            return values;
        }

        public static String create() {
            return createTable(NAME, COLUMN_ID, TYPE_COLUMN_ID, COLUMN_MB_ID,
                    TYPE_COLUMN_MB_ID, COLUMN_NAME, TYPE_COLUMN_RELEASENAME,
                    COLUMN_DATE_RELEASED, TYPE_COLUMN_RELEASEDATE_RELEASED,
                    COLUMN_DATE_CREATED, TYPE_COLUMN_RELEASEDATE_CREATED,
                    COLUMN_RELEASEARTWORK_PATH,
                    TYPE_COLUMN_RELEASEARTWORK_PATH, COLUMN_FK_ID_ARTIST,
                    TYPE_COLUMN_FK_ID_ARTIST, COLUMN_IS_HIDDEN,
                    TYPE_COLUMN_IS_HIDDEN, COLUMN_COVERARTARCHIVE_ID,
                    TYPE_COLUMN_COVERARTARCHIVE_ID,
                    // Constraints
                    TYPE_COLUMN_FK_ID_CONSTRAINT_FK,
                    TYPE_COLUMN_FK_ID_CONSTRAINT_REFERENCES);
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

        public static final String COLUMN_IS_HIDDEN = "isHidden";
        public static final String TYPE_COLUMN_IS_HIDDEN = "INTEGER";
        public static final int INDEX_COLUMN_IS_HIDDEN = 5;

        public static final String COLUMNS_ALL = new StringBuilder(NAME)
                .append(".").append(COLUMN_ID).append(",").append(NAME)
                .append(".").append(COLUMN_ANDROID_ID).append(",").append(NAME)
                .append(".").append(COLUMN_MB_ID).append(",").append(NAME)
                .append(".").append(COLUMN_NAME).append(",").append(NAME)
                .append(".").append(COLUMN_DATE_CREATED).append(",")
                .append(NAME).append(".").append(COLUMN_IS_HIDDEN).toString();

        public static Long toId(Cursor cursor, int startIndex) {
            return cursor.getLong(startIndex + INDEX_COLUMN_ID);
        }

        public static Artist toEntity(Cursor cursor, int startIndex) {
            Artist artist = new Artist(SqliteUtil.loadDate(cursor, startIndex
                    + INDEX_COLUMN_DATE_CREATED));

            artist.setId(toId(cursor, startIndex));
            artist.setAndroidAudioArtistId(cursor.getLong(startIndex
                    + INDEX_COLUMN_ANDROID_ID));
            artist.setMusicBrainzId(cursor.getString(startIndex
                    + INDEX_COLUMN_MB_ID));
            artist.setArtistName(cursor.getString(startIndex
                    + INDEX_COLUMN_NAME));
            artist.setHidden(SqliteUtil.loadBoolean(cursor,
                    INDEX_COLUMN_IS_HIDDEN));
            return artist;
        }

        public static String create() {
            return createTable(NAME, COLUMN_ID, TYPE_COLUMN_ID,
                    COLUMN_ANDROID_ID, TYPE_COLUMN_ANDROID_ID, COLUMN_MB_ID,
                    TYPE_COLUMN_MB_ID, COLUMN_NAME, TYPE_COLUMN_NAME,
                    COLUMN_DATE_CREATED, TYPE_COLUMN_DATE_CREATED,
                    COLUMN_IS_HIDDEN, TYPE_COLUMN_IS_HIDDEN);
        }

        public static ContentValues toContentValues(Artist artist) {
            ContentValues values = new ContentValues();
            SqliteUtil.putIfNotNull(values, COLUMN_ANDROID_ID,
                    artist.getAndroidAudioArtistId());
            SqliteUtil.putIfNotNull(values, COLUMN_MB_ID,
                    artist.getMusicBrainzId());
            SqliteUtil
                    .putIfNotNull(values, COLUMN_NAME, artist.getArtistName());
            SqliteUtil.putIfNotNull(values, COLUMN_DATE_CREATED,
                    DateUtil.toLong(artist.getDateCreated()));
            SqliteUtil
                    .putIfNotNull(values, COLUMN_IS_HIDDEN, artist.isHidden());
            return values;
        }
    }
}
