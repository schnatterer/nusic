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
package info.schnatterer.nusic.service.impl;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.service.ArtistQueryService;
import info.schnatterer.nusic.service.ServiceException;
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
	 * info.schnatterer.nusic.service.impl.ArtistQueryService#getArtists
	 * (android.content.ContentResolver)
	 */
	@Override
	public Artist[] getArtists(ContentResolver contentResolver)
			throws ServiceException {
		Cursor cursor = null;
		Artist[] artists  = null;
		try {
			cursor = contentResolver.query(ARTIST_URI, ARTIST_PROJECTION, null,
					null, ARTIST_SORT_ORDER);
			artists = new Artist[cursor.getCount()];
			int i = 0;
			while (cursor.moveToNext()) {
				Artist artist = new Artist();
				artist.setAndroidAudioArtistId(cursor.getLong(ArtistProjection.ID.getIndex()));
				artist.setArtistName(cursor.getString(ArtistProjection.ARTIST
						.getIndex()));
				artists[i++] = artist;
			}
		} catch (Throwable t) {
			throw new ServiceException(
					R.string.ServiceException_errorLoadingArtists, t);
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
