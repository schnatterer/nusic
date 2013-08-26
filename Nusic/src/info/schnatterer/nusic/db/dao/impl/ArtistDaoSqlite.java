package info.schnatterer.nusic.db.dao.impl;

import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.NusicDatabase;
import info.schnatterer.nusic.db.dao.ArtistDao;
import info.schnatterer.nusic.db.dao.ReleaseDao;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.util.DateUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ArtistDaoSqlite extends AbstractSqliteDao<Artist> implements
		ArtistDao {

	public static final String COLUMNS_ALL = new StringBuilder(
			NusicDatabase.TABLE_ARTIST).append(".")
			.append(NusicDatabase.COLUMN_ARTIST_ID).append(",")
			.append(NusicDatabase.TABLE_ARTIST).append(".")
			.append(NusicDatabase.COLUMN_ARTIST_ANDROID_ID).append(",")
			.append(NusicDatabase.TABLE_ARTIST).append(".")
			.append(NusicDatabase.COLUMN_ARTIST_MB_ID).append(",")
			.append(NusicDatabase.TABLE_ARTIST).append(".")
			.append(NusicDatabase.COLUMN_ARTIST_NAME).append(",")
			.append(NusicDatabase.TABLE_ARTIST).append(".")
			.append(NusicDatabase.COLUMN_ARTIST_DATE_CREATED).toString();

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
					NusicDatabase.TABLE_ARTIST,
					new String[] { NusicDatabase.COLUMN_ARTIST_ID },
					NusicDatabase.COLUMN_ARTIST_ANDROID_ID + " = " + androidId,
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
				+ NusicDatabase.INDEX_COLUMN_ARTIST_ID);
	}

	@Override
	public Artist toEntity(Cursor cursor, int startIndex) {
		Artist artist = new Artist(DateUtils.loadDate(cursor, startIndex
				+ NusicDatabase.INDEX_COLUMN_ARTIST_DATE_CREATED));

		artist.setId(toId(cursor, startIndex));
		artist.setAndroidAudioArtistId(cursor.getLong(startIndex
				+ NusicDatabase.INDEX_COLUMN_ARTIST_ANDROID_ID));
		artist.setMusicBrainzId(cursor.getString(startIndex
				+ NusicDatabase.INDEX_COLUMN_ARTIST_MB_ID));
		artist.setArtistName(cursor.getString(startIndex
				+ NusicDatabase.INDEX_COLUMN_ARTIST_NAME));
		return artist;
	}

	@Override
	public ContentValues toContentValues(Artist artist) {
		ContentValues values = new ContentValues();
		putIfNotNull(values, NusicDatabase.COLUMN_ARTIST_ANDROID_ID,
				artist.getAndroidAudioArtistId());
		putIfNotNull(values, NusicDatabase.COLUMN_ARTIST_MB_ID,
				artist.getMusicBrainzId());
		putIfNotNull(values, NusicDatabase.COLUMN_ARTIST_NAME,
				artist.getArtistName());
		putIfNotNull(values, NusicDatabase.COLUMN_ARTIST_DATE_CREATED,
				DateUtils.persistDate(artist.getDateCreated()));
		return values;
	}

	@Override
	public String getTableName() {
		return NusicDatabase.TABLE_ARTIST;
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
