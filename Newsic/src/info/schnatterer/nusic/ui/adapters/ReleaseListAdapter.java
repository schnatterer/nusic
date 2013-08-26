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
		// thumbnailView.setImageDrawable(listData.get(position)
		// .getThumbnail());
		return convertView;
	}

	public void show(List<Release> listData) {
		this.listData = listData;
		notifyDataSetChanged();
	}

}