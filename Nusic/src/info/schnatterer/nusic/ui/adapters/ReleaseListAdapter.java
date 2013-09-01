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

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.model.Release;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReleaseListAdapter extends BaseAdapter {
	private Context context;
	private List<Release> listData;
	private static LayoutInflater layoutInflater = null;

	public ReleaseListAdapter(Context context) {
		// this.listData = listData;
		this.context = context;
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
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.release_list_row,
					null);
		}
		TextView releaseNameView = (TextView) convertView
				.findViewById(R.id.releaseListRowReleaseName);
		TextView artistView = (TextView) convertView
				.findViewById(R.id.releaseListRowArtistName);
		TextView releaseDateView = (TextView) convertView
				.findViewById(R.id.releaseListRowReleaseDate);
		ImageView thumbnailView = (ImageView) convertView
				.findViewById(R.id.releaseListRowThumbnail);

		Release release = listData.get(position);
		if (release == null) {
			artistView.setText(listData.get(position).getArtistName());
			return convertView;
		}
		releaseNameView.setText(release.getReleaseName());
		artistView.setText(release.getArtistName());
		Date releaseDate = release.getReleaseDate();
		if (releaseDate != null) {
			releaseDateView.setText(DateFormat.getDateFormat(context).format(
					releaseDate));
		} else {
			releaseDateView.setText("");
		}
		thumbnailView.setImageBitmap(release.getArtwork());
		return convertView;
	}

	public void show(List<Release> listData) {
		this.listData = listData;
		notifyDataSetChanged();
	}

}