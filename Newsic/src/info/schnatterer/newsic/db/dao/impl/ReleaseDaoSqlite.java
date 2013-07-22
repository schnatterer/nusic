package info.schnatterer.newsic.db.dao.impl;

import info.schnatterer.newsic.db.DatabaseException;
import info.schnatterer.newsic.db.NewsicDatabase;
import info.schnatterer.newsic.db.dao.ArtistDao;
import info.schnatterer.newsic.db.dao.ReleaseDao;
import info.schnatterer.newsic.db.model.Release;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReleaseDaoSqlite extends AbstractSqliteDao<Release> implements
		ReleaseDao {
	private ArtistDao artistDao;

	public ReleaseDaoSqlite(Context context) {
		super(context);
	}

	public ReleaseDaoSqlite(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public void saveOrUpdate(List<Release> releases) throws DatabaseException {
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
				Long existingArtist = getArtistDao().getByAndroidId(
						release.getArtist().getAndroidAudioArtistId());
				if (existingArtist == null) {
					getArtistDao().save(release.getArtist());
				}
				// Does release exist?
				if (getByMusicBrainzId(release.getMusicBrainzId()) == null) {
					save(release);
				} else {
					update(release);
				}
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
	public Long getByMusicBrainzId(String musicBrainzId)
			throws DatabaseException {
		try {
			Cursor cursor = getDb().query(
					NewsicDatabase.TABLE_RELEASE,
					new String[] { NewsicDatabase.COLUMN_RELEASE_ID },
					NewsicDatabase.COLUMN_RELEASE_MB_ID + " = '"
							+ musicBrainzId + "'", null, null, null, null);
			if (!cursor.moveToFirst()) {
				return null;
			}
			return cursor.getLong(NewsicDatabase.INDEX_COLUMN_RELEASE_ID);
		} catch (Throwable t) {
			throw new DatabaseException(
					"Unable to find release by MusicBrainz id:" + musicBrainzId,
					t);
		}
	}

	@Override
	public ContentValues toContentValues(Release release) {
		ContentValues values = new ContentValues();
		values.put(NewsicDatabase.COLUMN_RELEASE_MB_ID,
				release.getMusicBrainzId());
		values.put(NewsicDatabase.COLUMN_RELEASE_NAME, release.getReleaseName());
		values.put(NewsicDatabase.COLUMN_RELEASE_DATE_RELEASED,
				persistDate(release.getReleaseDate()));
		values.put(NewsicDatabase.COLUMN_RELEASE_DATE_CREATED,
				persistDate(release.getDateCreated()));
		values.put(NewsicDatabase.COLUMN_RELEASE_ARTWORK_PATH,
				release.getArtworkPath());
		values.put(NewsicDatabase.COLUMN_RELEASE_FK_ID_ARTIST, release
				.getArtist().getId());
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

	protected ArtistDao getArtistDao() {
		if (artistDao == null) {
			artistDao = new ArtistDaoSqlite(getDb());
		}
		return artistDao;
	}

}
