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
import info.schnatterer.nusic.db.util.SqliteUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ReleaseDaoSqlite extends AbstractSqliteDao<Release> implements
		ReleaseDao {

	public static final String ORDER_BY_RELEASE_DATE = new StringBuilder(
			" ORDER BY ").append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_DATE_RELEASED).toString();

	public static final String ORDER_BY_RELEASE_DATE_DESC = new StringBuilder(
			ORDER_BY_RELEASE_DATE).append(" DESC").toString();

	public static final String ORDER_BY_RELEASE_DATE_ASC = new StringBuilder(
			ORDER_BY_RELEASE_DATE).append(" ASC").toString();

	public static final String QUERY_ALL = new StringBuilder("SELECT ")
			.append(TableRelease.COLUMNS_ALL).append(",")
			.append(TableArtist.COLUMNS_ALL).append(" FROM ")
			.append(TableRelease.NAME).append(" INNER JOIN ")
			.append(TableArtist.NAME).append(" ON ").append(TableRelease.NAME)
			.append(".").append(TableRelease.COLUMN_FK_ID_ARTIST).append("=")
			.append(TableArtist.NAME).append(".").append(TableArtist.COLUMN_ID)
			.toString();

	public static final String QUERY_NOT_HIDDEN = new StringBuilder(QUERY_ALL)
			.append(" WHERE (").append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_IS_HIDDEN).append(" IS NULL OR ")
			.append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_IS_HIDDEN).append("!=")
			.append(SqliteUtil.TRUE).append(") AND (")
			.append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_DATE_RELEASED).append(" IS NOT NULL")
			.append(") AND (").append(TableArtist.NAME).append(".")
			.append(TableArtist.COLUMN_IS_HIDDEN).append(" IS NULL OR ")
			.append(TableArtist.NAME).append(".")
			.append(TableArtist.COLUMN_IS_HIDDEN).append("!=")
			.append(SqliteUtil.TRUE).append(")").toString();

	public static final String QUERY_NOT_HIDDEN_ORDER_BY_RELEASE_DATE_DESC = new StringBuilder(
			QUERY_NOT_HIDDEN).append(ORDER_BY_RELEASE_DATE_DESC).toString();

	public static final String QUERY_BY_DATE_CREATED = new StringBuilder(
			QUERY_NOT_HIDDEN).append(" AND ").append(TableRelease.NAME)
			.append(".").append(TableRelease.COLUMN_DATE_CREATED).append(">")
			.append(" ?").append(ORDER_BY_RELEASE_DATE_DESC).toString();

	private static final String QUERY_BY_RELEASE_DATE_BASE = new StringBuilder(
			QUERY_NOT_HIDDEN).append(" AND ").append(TableRelease.NAME)
			.append(".").append(TableRelease.COLUMN_DATE_RELEASED).toString();

	private static final String QUERY_BY_RELEASE_DATE_GTE_BASE = new StringBuilder(
			QUERY_BY_RELEASE_DATE_BASE).append(" >=").append(" ?").toString();

	private static final String QUERY_BY_RELEASE_DATE_GTE_ORDER_BY_RELEASE_DATE_ASC = new StringBuilder(
			QUERY_BY_RELEASE_DATE_GTE_BASE).append(ORDER_BY_RELEASE_DATE_ASC)
			.toString();

	private static final String QUERY_BY_RELEASE_DATE_GTE_ORDER_BY_RELEASE_DATE_DESC = new StringBuilder(
			QUERY_BY_RELEASE_DATE_GTE_BASE).append(ORDER_BY_RELEASE_DATE_DESC)
			.toString();

	private static final String QUERY_BY_RELEASE_DATE_RANGE = new StringBuilder(
			QUERY_BY_RELEASE_DATE_GTE_BASE).append(" AND ")
			.append(TableRelease.NAME).append(".")
			.append(TableRelease.COLUMN_DATE_RELEASED).append(" <")
			.append(" ?").append(ORDER_BY_RELEASE_DATE_DESC).toString();

	public ReleaseDaoSqlite(Context context) {
		super(context);
	}

	@Override
	public Release findByMusicBrainzId(String musicBrainzId)
			throws DatabaseException {
		try {
			Cursor cursor = query(TableRelease.NAME, new String[] {
					TableRelease.COLUMN_ID, TableRelease.COLUMN_DATE_CREATED },
					TableRelease.COLUMN_MB_ID + " = '" + musicBrainzId + "'",
					null, null, null, null);
			if (!cursor.moveToFirst()) {
				return null;
			}
			Release release = new Release(SqliteUtil.loadDate(cursor, 1));
			release.setId(cursor.getLong(0));
			return release;
		} catch (Throwable t) {
			throw new DatabaseException(
					"Unable to find release by MusicBrainz id:" + musicBrainzId,
					t);
		} finally {
			closeCursor();
		}
	}

	@Override
	public List<Release> findByDateCreatedGreaterThanAndIsHiddenNotTrue(
			long gtDateCreated) throws DatabaseException {
		return executeQuery(QUERY_BY_DATE_CREATED,
				new String[] { String.valueOf(gtDateCreated) });
	}

	@Override
	public List<Release> findByReleaseDateGreaterThanEqualsAndIsHiddenNotTrueSortByReleaseDateAsc(
			long gtEqReleaseDate) throws DatabaseException {
		return executeQuery(
				QUERY_BY_RELEASE_DATE_GTE_ORDER_BY_RELEASE_DATE_ASC,
				new String[] { String.valueOf(gtEqReleaseDate) });
	}

	@Override
	public List<Release> findByReleaseDateGreaterThanEqualsAndIsHiddenNotTrueSortByReleaseDateDesc(
			long gtEqReleaseDate) throws DatabaseException {
		return executeQuery(
				QUERY_BY_RELEASE_DATE_GTE_ORDER_BY_RELEASE_DATE_DESC,
				new String[] { String.valueOf(gtEqReleaseDate) });
	}

	@Override
	public List<Release> findByReleaseDateGreaterThanEqualsAndReleaseDateLessThanAndIsHiddenNotTrue(
			long gtEqReleaseDate, long ltRealaseDate) throws DatabaseException {
		return executeQuery(
				QUERY_BY_RELEASE_DATE_RANGE,
				new String[] { String.valueOf(gtEqReleaseDate),
						String.valueOf(ltRealaseDate) });
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

	@Override
	public void setIsHiddenFalse() throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TableRelease.COLUMN_IS_HIDDEN, SqliteUtil.FALSE);
		update(contentValues, null, null);
	}
}
