package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.ArtistDao;
import info.schnatterer.newsic.db.dao.ReleaseDao;
import info.schnatterer.newsic.db.model.Artist;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ArtistDaoSqlite extends AbstractSqliteDao<Artist> implements
		ArtistDao {

	private ReleaseDao releaseDao;

	public ArtistDaoSqlite(Context context) {
		super(context);
	}

	public ArtistDaoSqlite(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public long save(Artist artist) throws DatabaseException {
		long ret = super.save(artist);
		getReleaseDao().saveOrUpdate(artist.getReleases());
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
		Long ret = artist.getId();
		// Does artist exist?
		if (getByAndroidId(artist.getAndroidAudioArtistId()) == null) {
			ret = save(artist);
		} else {
			update(artist);
		}
		return ret;
	}

	@Override
	public Long getByAndroidId(long androidId) throws DatabaseException {
		try {
			Cursor cursor = getDb()
					.query(NewsicDatabase.TABLE_ARTIST,
							new String[] { NewsicDatabase.COLUMN_ARTIST_DATE_CREATED },
							NewsicDatabase.COLUMN_ARTIST_ANDROID_ID + " = "
									+ androidId, null, null, null, null);
			if (!cursor.moveToFirst()) {
				return null;
			}
			return cursor.getLong(NewsicDatabase.INDEX_COLUMN_ARTIST_ID);
		} catch (Throwable t) {
			throw new DatabaseException("Unable to find artist by android id:"
					+ androidId, t);
		}
	}

	@Override
	public ContentValues toContentValues(Artist artist) {
		ContentValues values = new ContentValues();
		values.put(NewsicDatabase.COLUMN_ARTIST_ANDROID_ID,
				artist.getAndroidAudioArtistId());
		values.put(NewsicDatabase.COLUMN_ARTIST_NAME, artist.getArtistName());
		values.put(NewsicDatabase.COLUMN_ARTIST_DATE_CREATED,
				persistDate(artist.getDateCreated()));
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
			releaseDao = new ReleaseDaoSqlite(getDb());
		}
		return releaseDao;
	}

}
