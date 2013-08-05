package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.ArtistDao;
import info.schnatterer.newsic.db.dao.ReleaseDao;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.util.DateUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ArtistDaoSqlite extends AbstractSqliteDao<Artist> implements
		ArtistDao {

	public static final String COLUMNS_ALL = new StringBuilder(
			NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_ID).append(",")
			.append(NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_ANDROID_ID).append(",")
			.append(NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_MB_ID).append(",")
			.append(NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_NAME).append(",")
			.append(NewsicDatabase.TABLE_ARTIST).append(".")
			.append(NewsicDatabase.COLUMN_ARTIST_DATE_CREATED).toString();

	private ReleaseDaoSqlite releaseDao;

	public ArtistDaoSqlite(Context context) {
		super(context);
	}

	@Override
	public long save(Artist artist) throws DatabaseException {
		long ret = super.save(artist);
		getReleaseDao().saveOrUpdate(artist.getReleases(), false);
		return ret;
	}

	@Override
	public int update(Artist artist) throws DatabaseException {
		int ret = super.update(artist);
		getReleaseDao().saveOrUpdate(artist.getReleases());
		return ret;
	}

	@Override
	public long saveOrUpdate(Artist artist) throws DatabaseException {
		// Does artist exist?
		if (artist.getId() == null) {
			artist.setId(findByAndroidId(artist.getAndroidAudioArtistId()));
		}
		if (artist.getId() == null) {
			save(artist);
		} else {
			update(artist);
		}
		return artist.getId();
	}

	@Override
	public Long findByAndroidId(long androidId) throws DatabaseException {
		Cursor cursor = null;
		try {
			cursor = query(
					NewsicDatabase.TABLE_ARTIST,
					new String[] { NewsicDatabase.COLUMN_ARTIST_ID },
					NewsicDatabase.COLUMN_ARTIST_ANDROID_ID + " = " + androidId,
					null, null, null, null);
			if (!cursor.moveToFirst()) {
				return null;
			}
			return cursor.getLong(0);
		} catch (Throwable t) {
			throw new DatabaseException("Unable to find artist by android id:"
					+ androidId, t);
		} finally {
			closeCursor();
		}
	}

	@Override
	public Long toId(Cursor cursor, int startIndex) {
		return cursor.getLong(startIndex
				+ NewsicDatabase.INDEX_COLUMN_ARTIST_ID);
	}

	@Override
	public Artist toEntity(Cursor cursor, int startIndex) {
		Artist artist = new Artist();

		artist.setId(toId(cursor, startIndex));
		artist.setAndroidAudioArtistId(cursor.getLong(startIndex
				+ NewsicDatabase.INDEX_COLUMN_ARTIST_ANDROID_ID));
		artist.setMusicBrainzId(cursor.getString(startIndex
				+ NewsicDatabase.INDEX_COLUMN_ARTIST_MB_ID));
		artist.setArtistName(cursor.getString(startIndex
				+ NewsicDatabase.INDEX_COLUMN_ARTIST_NAME));
		artist.setDateCreated(DateUtils.loadDate(cursor, startIndex
				+ NewsicDatabase.INDEX_COLUMN_ARTIST_DATE_CREATED));
		return artist;
	}

	@Override
	public ContentValues toContentValues(Artist artist) {
		ContentValues values = new ContentValues();
		values.put(NewsicDatabase.COLUMN_ARTIST_ANDROID_ID,
				artist.getAndroidAudioArtistId());
		values.put(NewsicDatabase.COLUMN_ARTIST_MB_ID,
				artist.getMusicBrainzId());
		values.put(NewsicDatabase.COLUMN_ARTIST_NAME, artist.getArtistName());
		values.put(NewsicDatabase.COLUMN_ARTIST_DATE_CREATED,
				DateUtils.persistDate(artist.getDateCreated()));
		return values;
	}

	@Override
	public String getTableName() {
		return NewsicDatabase.TABLE_ARTIST;
	}

	@Override
	protected Long getId(Artist artist) {
		return artist.getId();
	}

	private ReleaseDao getReleaseDao() {
		if (releaseDao == null) {
			releaseDao = new ReleaseDaoSqlite(getContext());
		}
		return releaseDao;
	}

	@Override
	public void closeCursor() {
		super.closeCursor();
		if (releaseDao != null) {
			releaseDao.closeCursor();
		}
	}
}
