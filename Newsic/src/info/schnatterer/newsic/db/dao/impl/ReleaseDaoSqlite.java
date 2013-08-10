package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.ReleaseDao;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.db.model.Release;
import info.schnatterer.newsic.util.DateUtils;

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
	private ArtistDaoSqlite artistDao;

	public static final String COLUMNS_ALL = new StringBuilder(
			NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_ID).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_MB_ID).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_NAME).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_DATE_RELEASED).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_DATE_CREATED).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_ARTWORK_PATH).append(",")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_FK_ID_ARTIST).toString();

	public static final String ORDER_BY_RELEASE_DATE_ASC = new StringBuilder(
			" ORDER BY ").append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_DATE_RELEASED)
			.append(" DESC").toString();

	public static final String QUERY_ALL = new StringBuilder("SELECT ")
			.append(COLUMNS_ALL).append(",")
			.append(ArtistDaoSqlite.COLUMNS_ALL).append(" FROM ")
			.append(NewsicDatabase.TABLE_RELEASE).append(" INNER JOIN ")
			.append(NewsicDatabase.TABLE_ARTIST).append(" ON ")
			.append(NewsicDatabase.TABLE_RELEASE).append(".")
			.append(NewsicDatabase.COLUMN_RELEASE_FK_ID_ARTIST).append("=")
			.append(NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_ID).toString();

	public static final String QUERY_ALL_ORDER_RELEASE_DATE_ASC = new StringBuilder(
			QUERY_ALL).append(ORDER_BY_RELEASE_DATE_ASC).toString();

	public static final String QUERY_BY_RELEASE_DATE_ORDER_BY_DATE_ASC = new StringBuilder(
			QUERY_ALL).append(" WHERE ").append(NewsicDatabase.TABLE_RELEASE)
			.append(".").append(NewsicDatabase.COLUMN_RELEASE_DATE_CREATED)
			.append(">").append(" ?").append(ORDER_BY_RELEASE_DATE_ASC)
			.toString();

	public ReleaseDaoSqlite(Context context) {
		super(context);
	}

	@Override
	public void saveOrUpdate(List<Release> releases) throws DatabaseException {
		saveOrUpdate(releases, true);
	}

	@Override
	public void saveOrUpdate(List<Release> releases, boolean saveArtist)
			throws DatabaseException {

		if (releases.size() == 0) {
			return;
		}

		for (Release release : releases) {
			try {
				if (release.getArtist() == null) {
					throw new DatabaseException(
							"Can't save release without artist");
				}
				/* Get existing artist */
				if (saveArtist) {
					if (release.getArtist().getId() == null) {
						Long existingArtist = getArtistDao().findByAndroidId(
								release.getArtist().getAndroidAudioArtistId());
						if (existingArtist == null) {
							getArtistDao().save(release.getArtist());
						}
					}
				}

				saveOrUpdate(release);
			} catch (DatabaseException databaseException) {
				// Rethrow
				throw databaseException;
			} catch (Throwable t) {
				throw new DatabaseException(
						"Unable to save release " + release, t);
			}
		}
	}

	@Override
	public long saveOrUpdate(Release release) throws DatabaseException {
		// Does release exist?
		if (release.getId() == null) {
			release.setId(findByMusicBrainzId(release.getMusicBrainzId()));
		}
		if (release.getId() == null) {
			save(release);
		} else {
			update(release);
		}
		return release.getId();
	}

	@Override
	public Long findByMusicBrainzId(String musicBrainzId)
			throws DatabaseException {
		try {
			Cursor cursor = query(NewsicDatabase.TABLE_RELEASE,
					new String[] { NewsicDatabase.COLUMN_RELEASE_ID },
					NewsicDatabase.COLUMN_RELEASE_MB_ID + " = '"
							+ musicBrainzId + "'", null, null, null, null);
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
		return executeQuey(QUERY_ALL_ORDER_RELEASE_DATE_ASC, null);
	}

	public List<Release> findNewlyCreated(Date gtDateCreated)
			throws DatabaseException {
		return executeQuey(QUERY_BY_RELEASE_DATE_ORDER_BY_DATE_ASC,
				new String[] { String.valueOf(gtDateCreated.getTime()) });
	}

	private List<Release> executeQuey(String sql, String[] selectionArgs)
			throws DatabaseException {
		List<Release> releases = new LinkedList<Release>();
		try {
			Cursor cursor = rawQuery(sql, selectionArgs);
			cursor.moveToFirst();
			Map<Long, Artist> artists = new HashMap<Long, Artist>();
			while (!cursor.isAfterLast()) {
				Artist artist = artists.get(getArtistDao().toId(cursor,
						NewsicDatabase.TABLE_RELEASE_COLUMNS.length));
				if (artist == null) {
					artist = getArtistDao().toEntity(cursor,
							NewsicDatabase.TABLE_RELEASE_COLUMNS.length);
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
		return cursor.getLong(startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_ID);
	}

	@Override
	public Release toEntity(Cursor cursor, int startIndex) {
		Release release = new Release(DateUtils.loadDate(cursor, startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_DATE_CREATED));
		release.setId(toId(cursor, startIndex));
		release.setArtworkPath(cursor.getString(startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_ARTWORK_PATH));
		release.setMusicBrainzId(cursor.getString(startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_MB_ID));
		release.setReleaseDate(DateUtils.loadDate(cursor, startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_DATE_RELEASED));
		release.setReleaseName(cursor.getString(startIndex
				+ NewsicDatabase.INDEX_COLUMN_RELEASE_NAME));

		return release;
	}

	@Override
	public ContentValues toContentValues(Release release) {
		ContentValues values = new ContentValues();
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_MB_ID,
				release.getMusicBrainzId());
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_NAME,
				release.getReleaseName());
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_DATE_RELEASED,
				DateUtils.persistDate(release.getReleaseDate()));
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_DATE_CREATED,
				DateUtils.persistDate(release.getDateCreated()));
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_ARTWORK_PATH,
				release.getArtworkPath());
		putIfNotNull(values, NewsicDatabase.COLUMN_RELEASE_FK_ID_ARTIST,
				release.getArtist().getId());
		return values;
	}

	@Override
	public String getTableName() {
		return NewsicDatabase.TABLE_RELEASE;
	}

	@Override
	protected Long getId(Release release) {
		return release.getId();
	}

	protected ArtistDaoSqlite getArtistDao() {
		if (artistDao == null) {
			artistDao = new ArtistDaoSqlite(getContext());
		}
		return artistDao;
	}

	@Override
	public void closeCursor() {
		super.closeCursor();
		if (artistDao != null) {
			artistDao.closeCursor();
		}
	}

}
