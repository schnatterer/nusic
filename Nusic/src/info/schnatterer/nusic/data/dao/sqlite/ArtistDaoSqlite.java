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
import info.schnatterer.nusic.data.NusicDatabaseSqlite.TableArtist;
import info.schnatterer.nusic.data.dao.ArtistDao;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.util.SqliteUtil;

import javax.inject.Inject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ArtistDaoSqlite extends AbstractSqliteDao<Artist> implements
		ArtistDao {

	// TODO DI Remove constructor
	@Inject
	public ArtistDaoSqlite(Context context) {
		super(context);
	}

	@Override
	public Artist findByAndroidId(long androidId) throws DatabaseException {
		try {
			Cursor cursor = findCursorByAndroidId(androidId, new String[] {
					TableArtist.COLUMN_ID, TableArtist.COLUMN_DATE_CREATED });
			if (!cursor.moveToFirst()) {
				return null;
			}
			Artist artist = new Artist(SqliteUtil.loadDate(cursor, 1));
			artist.setId(cursor.getLong(0));
			return artist;
		} finally {
			closeCursor();
		}
	}

	@Override
	public Long findIdByAndroidId(Long androidId) throws DatabaseException {
		try {
			Cursor cursor = findCursorByAndroidId(androidId,
					new String[] { TableArtist.COLUMN_ID });
			if (!cursor.moveToFirst()) {
				return null;
			}
			return cursor.getLong(0);
		} finally {
			closeCursor();
		}
	}

	@Override
	public Long toId(Cursor cursor, int startIndex) {
		return TableArtist.toId(cursor, startIndex);
	}

	@Override
	public Artist toEntity(Cursor cursor, int startIndex) {
		return TableArtist.toEntity(cursor, startIndex);
	}

	@Override
	public ContentValues toContentValues(Artist artist) {
		return TableArtist.toContentValues(artist);
	}

	@Override
	public String getTableName() {
		return TableArtist.NAME;
	}

	@Override
	protected Long getId(Artist artist) {
		return artist.getId();
	}

	@Override
	public void setIsHiddenFalse() throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TableArtist.COLUMN_IS_HIDDEN, SqliteUtil.FALSE);
		update(contentValues, null, null);
	}

	/**
	 * Queries a specific artists ID and returns the cursor or <code>null</code>
	 * if none present. <b>Make sure to close the cursor when finished!</b>
	 * 
	 * @param androidId
	 * @param columns
	 * @return the cursor or <code>null</code>, if none found
	 * @throws DatabaseException
	 */
	private Cursor findCursorByAndroidId(long androidId, String[] columns)
			throws DatabaseException {
		Cursor cursor = null;
		try {
			cursor = query(TableArtist.NAME, columns,
					TableArtist.COLUMN_ANDROID_ID + " = " + androidId, null,
					null, null, null);
			return cursor;
		} catch (Throwable t) {
			throw new DatabaseException("Unable to find artist by android id:"
					+ androidId, t);
		}
	}
}
