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
package info.schnatterer.nusic.db.dao.sqlite;

import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.NusicDatabaseSqlite.TableArtist;
import info.schnatterer.nusic.db.NusicDatabaseSqlite.TableRelease;
import info.schnatterer.nusic.db.dao.ReleaseDao;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ReleaseDaoSqlite extends AbstractSqliteDao<Release> implements
		ReleaseDao {

	public static final String ORDER_BY_RELEASE_DATE_ASC = new StringBuilder(
			" ORDER BY ").append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_RELEASEDATE_RELEASED).append(" DESC")
			.toString();

	public static final String QUERY_ALL = new StringBuilder("SELECT ")
			.append(TableRelease.COLUMNS_ALL).append(",")
			.append(TableArtist.COLUMNS_ALL).append(" FROM ")
			.append(TableRelease.NAME).append(" INNER JOIN ")
			.append(TableArtist.NAME).append(" ON ").append(TableRelease.NAME)
			.append(".").append(TableRelease.COLUMN_FK_ID_ARTIST).append("=")
			.append(TableArtist.NAME).append(".").append(TableArtist.COLUMN_ID)
			.toString();

	public static final String QUERY_ALL_ORDER_RELEASE_DATE_ASC = new StringBuilder(
			QUERY_ALL).append(ORDER_BY_RELEASE_DATE_ASC).toString();

	public static final String QUERY_BY_RELEASE_DATE_ORDER_BY_DATE_ASC = new StringBuilder(
			QUERY_ALL).append(" WHERE ").append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_RELEASEDATE_CREATED).append(">")
			.append(" ?").append(ORDER_BY_RELEASE_DATE_ASC).toString();

	public ReleaseDaoSqlite(Context context) {
		super(context);
	}

	@Override
	public Long findByMusicBrainzId(String musicBrainzId)
			throws DatabaseException {
		try {
			Cursor cursor = query(TableRelease.NAME,
					new String[] { TableRelease.COLUMN_ID },
					TableRelease.COLUMN_MB_ID + " = '" + musicBrainzId + "'",
					null, null, null, null);
			if (!cursor.moveToFirst()) {
				return null;
			}
			return cursor.getLong(0);
		} catch (Throwable t) {
			throw new DatabaseException(
					"Unable to find release by MusicBrainz id:" + musicBrainzId,
					t);
		} finally {
			closeCursor();
		}
	}

	@Override
	public List<Release> findAll() throws DatabaseException {
		return executeQuery(QUERY_ALL_ORDER_RELEASE_DATE_ASC, null);
	}

	@Override
	public List<Release> findJustCreated(Date gtDateCreated)
			throws DatabaseException {
		return executeQuery(QUERY_BY_RELEASE_DATE_ORDER_BY_DATE_ASC,
				new String[] { String.valueOf(gtDateCreated.getTime()) });
	}

	private List<Release> executeQuery(String sql, String[] selectionArgs)
			throws DatabaseException {
		List<Release> releases = new LinkedList<Release>();
		try {
			Cursor cursor = rawQuery(sql, selectionArgs);
			cursor.moveToFirst();
			Map<Long, Artist> artists = new HashMap<Long, Artist>();
			while (!cursor.isAfterLast()) {
				Artist artist = artists.get(TableArtist.toId(cursor,
						TableRelease.COLUMNS.length));
				if (artist == null) {
					artist = TableArtist.toEntity(cursor,
							TableRelease.COLUMNS.length);
				}
				Release release = toEntity(cursor, 0);
				release.setArtist(artist);
				artist.getReleases().add(release);
				releases.add(release);
				cursor.moveToNext();
			}
		} catch (Throwable t) {
			throw new DatabaseException("Unable to find all releases", t);
		} finally {
			closeCursor();
		}
		return releases;
	}

	@Override
	public Long toId(Cursor cursor, int startIndex) {
		return TableRelease.toId(cursor, startIndex);
	}

	@Override
	public Release toEntity(Cursor cursor, int startIndex) {
		return TableRelease.toEntity(cursor, startIndex);
	}

	@Override
	public ContentValues toContentValues(Release release) {
		return TableRelease.toContentValues(release);
	}

	@Override
	public String getTableName() {
		return TableRelease.NAME;
	}

	@Override
	protected Long getId(Release release) {
		return release.getId();
	}

}
