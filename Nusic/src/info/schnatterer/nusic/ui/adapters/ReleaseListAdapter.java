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
package info.schnatterer.nusic.ui.adapters;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.DatabaseException;
import info.schnatterer.nusic.db.dao.ArtworkDao;
import info.schnatterer.nusic.db.dao.ArtworkDao.ArtworkType;
import info.schnatterer.nusic.db.dao.fs.ArtworkDaoFileSystem;
import info.schnatterer.nusic.db.model.Release;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReleaseListAdapter extends BaseAdapter {
	private static final int DEFAULT_ARTWORK = R.drawable.ic_launcher;

	private static transient Bitmap defaultArtwork = BitmapFactory
			.decodeResource(Application.getContext().getResources(),
					DEFAULT_ARTWORK);

	private static ArtworkDao artworkDao = new ArtworkDaoFileSystem();
	private List<Release> listData;
	private static LayoutInflater layoutInflater = null;

	public ReleaseListAdapter(Context context) {
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (listData != null) {
			return listData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (listData != null) {
			return listData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ReleaseListHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.release_list_row,
					parent, false);
			holder = new ReleaseListHolder(convertView);
			// Store holder in view
			convertView.setTag(holder);
		} else {
			// Extract holder from existing view
			holder = (ReleaseListHolder) convertView.getTag();
		}
		Release release = listData.get(position);
		if (release == null) {
			holder.artistView.get().setText(
					listData.get(position).getArtistName());
			return convertView;
		}
		holder.releaseNameView.get().setText(release.getReleaseName());
		holder.artistView.get().setText(release.getArtistName());
		Date releaseDate = release.getReleaseDate();
		if (releaseDate != null) {
			DateFormat dateFormat = DateFormat.getDateInstance();
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			holder.releaseDateView.get()
					.setText(dateFormat.format(releaseDate));
		} else {
			holder.releaseDateView.get().setText("");
		}
		// TODO use async loading here
		try {
			InputStream artwork = artworkDao.findByRelease(release,
					ArtworkType.SMALL);
			if (artwork != null) {
				holder.thumbnailView.get().setImageBitmap(
						BitmapFactory.decodeStream(artwork));
			} else {
				holder.thumbnailView.get().setImageBitmap(defaultArtwork);
			}
		} catch (DatabaseException e) {
			Log.w(Constants.LOG, "Unable to load artwork for release "
					+ release, e);
			holder.thumbnailView.get().setImageBitmap(defaultArtwork);
		}
		return convertView;
	}

	public void show(List<Release> listData) {
		this.listData = listData;
		notifyDataSetChanged();
	}

	public class ReleaseListHolder {
		WeakReference<TextView> releaseNameView;
		WeakReference<TextView> artistView;
		WeakReference<TextView> releaseDateView;
		WeakReference<ImageView> thumbnailView;

		public ReleaseListHolder(View view) {
			releaseNameView = new WeakReference<TextView>(
					(TextView) (TextView) view
							.findViewById(R.id.releaseListRowReleaseName));
			artistView = new WeakReference<TextView>(
					(TextView) (TextView) view
							.findViewById(R.id.releaseListRowArtistName));
			releaseDateView = new WeakReference<TextView>(
					(TextView) view
							.findViewById(R.id.releaseListRowReleaseDate));
			thumbnailView = new WeakReference<ImageView>(
					(ImageView) view.findViewById(R.id.releaseListRowThumbnail));
		}
	}

}