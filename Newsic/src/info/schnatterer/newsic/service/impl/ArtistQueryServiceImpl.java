package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.R;
import info.schnatterer.newsic.db.model.Artist;
import info.schnatterer.newsic.service.ArtistQueryService;
import info.schnatterer.newsic.service.ServiceException;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.ArtistColumns;

public class ArtistQueryServiceImpl implements ArtistQueryService {

	private static final String ARTIST_SORT_ORDER = Audio.Artists.DEFAULT_SORT_ORDER;
	private static final String[] ARTIST_PROJECTION = ArtistProjection
			.getArtistProjection();
	private static final Uri ARTIST_URI = Audio.Artists.EXTERNAL_CONTENT_URI;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * info.schnatterer.newsic.service.impl.ArtistQueryService#getArtists
	 * (android.content.ContentResolver)
	 */
	@Override
	public List<Artist> getArtists(ContentResolver contentResolver)
			throws ServiceException {
		List<Artist> artists = new ArrayList<Artist>();
		Cursor cursor = null;
		try {
			cursor = contentResolver.query(ARTIST_URI, ARTIST_PROJECTION, null,
					null, ARTIST_SORT_ORDER);
			while (cursor.moveToNext()) {
				Artist artist = new Artist();
				artist.setAndroidAudioArtistId(cursor.getLong(ArtistProjection.ID.getIndex()));
				artist.setArtistName(cursor.getString(ArtistProjection.ARTIST
						.getIndex()));
				artists.add(artist);
			}
		} catch (Throwable t) {
			throw new ServiceException(
					R.string.ArtistQueryService_errorLoadingArtists, t);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return artists;
	}

	public enum ArtistProjection {
		ID(0, BaseColumns._ID), ARTIST(1, ArtistColumns.ARTIST);
		private static final String[] artistProjection = { ID.getColumnKey(),
				ARTIST.getColumnKey() };
		private int index;
		private String columnKey;

		private ArtistProjection(int index, String columnKey) {
			this.setIndex(index);
			this.setColumnKey(columnKey);
		}

		public int getIndex() {
			return index;
		}

		public String getColumnKey() {
			return columnKey;
		}

		private void setIndex(int index) {
			this.index = index;
		}

		private void setColumnKey(String columnKey) {
			this.columnKey = columnKey;
		}

		public static String[] getArtistProjection() {
			return artistProjection;
		}
	}
}
