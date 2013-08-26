package info.schnatterer.nusic.ui.fragments;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.db.loader.AsyncResult;
import info.schnatterer.nusic.db.loader.ReleaseLoader;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import info.schnatterer.nusic.ui.adapters.ReleaseListAdapter;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class ReleaseListFragment extends SherlockFragment {
	public enum ReleaseQuery {
		ALL, JUST_ADDED;
	}

	public static final String ARG_RELEASE_QUERY = "releaseQuery";
	public static final String ARG_LOADER_ID = "loaderId";

	private ReleaseQuery releaseQuery;
	private PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	private ListView releasesListView;
	private ReleaseListAdapter releasesListViewAdapter = null;
	private TextView releasesTextViewNoneFound;
	/** Progress animation when loading releases from db */
	private ProgressBar progressBar;
	private int loaderId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {

			releaseQuery = ReleaseQuery.valueOf(getArguments().getString(
					ARG_RELEASE_QUERY));
			loaderId = getArguments().getInt(ARG_LOADER_ID);

		} catch (Exception e) {
			Log.w(Constants.LOG,
					"Error reading arguments from bundle passed by parent activity",
					e);
		}

		View view = inflater.inflate(R.layout.release_list_layout, container,
				false);
		progressBar = (ProgressBar) view.findViewById(R.id.releasesProgressBar);
		progressBar.setVisibility(View.GONE);

		releasesTextViewNoneFound = (TextView) view
				.findViewById(R.id.releasesTextViewNoneFound);
		releasesTextViewNoneFound.setVisibility(View.GONE);

		releasesListView = (ListView) view.findViewById(R.id.releasesListView);

		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = releasesListView.getItemAtPosition(position);
				Release release = (Release) o;
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri
						.parse(release.getMusicBrainzUri()));
				startActivity(launchBrowser);
			}

		});
		releasesListViewAdapter = new ReleaseListAdapter(getActivity());
		releasesListView.setAdapter(releasesListViewAdapter);

		displayLoading();
		// Load releases from local db
		getActivity().getSupportLoaderManager().initLoader(loaderId, null,
				new ReleaseLoaderCallbacks());
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (isVisible()) {
			foreceLoad();
		}
	}

	protected void setReleases(List<Release> result) {
		releasesListViewAdapter.show(result);
	}

	public ReleaseQuery getReleaseQuery() {
		return releaseQuery;
	}

	/**
	 * Sets the type of releases that are queried from database and displayed.
	 * 
	 * @param releaseQuery
	 */
	public void setReleaseQuery(ReleaseQuery releaseQuery) {
		this.releaseQuery = releaseQuery;
	}

	/**
	 * Triggers reloading the releases from database. If they didn't change
	 * (that is {@link Loader#onContentChanged()} hasn't been called), no actual
	 * load is performed.
	 */
	public void foreceLoad() {
		displayLoading();
		getActivity().getSupportLoaderManager().getLoader(loaderId).forceLoad();
	}

	/**
	 * Marks content as changed, which leads to reloading on the next load.
	 */
	public void onContentChanged() {
		getActivity().getSupportLoaderManager().getLoader(loaderId)
				.onContentChanged();
	}

	/**
	 * Shows the loading animation.
	 */
	private void displayLoading() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressBar.setVisibility(View.VISIBLE);
				releasesTextViewNoneFound.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * Handles callbacks from {@link ReleaseLoader} that loads the
	 * {@link Release}s from the local database.
	 * 
	 * @author schnatterer
	 * 
	 */
	public class ReleaseLoaderCallbacks implements
			LoaderManager.LoaderCallbacks<AsyncResult<List<Release>>> {
		@Override
		public ReleaseLoader onCreateLoader(int id, Bundle bundle) {
			// if (id == RELEASE_DB_LOADER)
			switch (releaseQuery) {
			case ALL:
				return new ReleaseLoader(getActivity(), null);
			case JUST_ADDED:
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH,
						-preferencesService.getJustAddedTimePeriod());
				return new ReleaseLoader(getActivity(), cal.getTime());
			default:
				Log.w(Constants.LOG,
						"Unimplemented " + ReleaseQuery.class.getName()
								+ " enumeration: \"" + releaseQuery.name()
								+ "\"");
				return new ReleaseLoader(getActivity(), null);
			}
		}

		@Override
		public void onLoadFinished(Loader<AsyncResult<List<Release>>> loader,
				AsyncResult<List<Release>> result) {
			progressBar.setVisibility(View.GONE);

			if (result.getException() != null) {
				releasesTextViewNoneFound.setVisibility(View.VISIBLE);
				releasesTextViewNoneFound
						.setText(R.string.MainActivity_errorLoadingReleases);
				return;
			}
			if (result.getData() == null
					|| (result.getData() != null && result.getData().isEmpty())) {
				// Set the empty text
				releasesTextViewNoneFound.setVisibility(View.VISIBLE);
				if (releaseQuery == ReleaseQuery.JUST_ADDED) {
					releasesTextViewNoneFound
							.setText(R.string.MainActivity_noNewReleasesFound);
				} else {
					releasesTextViewNoneFound
							.setText(R.string.MainActivity_noReleasesFound);
				}
				return;
			}
			// if (releaseQuery == ReleaseQuery.JUST_ADDED) {
			// releasesTextViewNoneFound.setVisibility(View.VISIBLE);
			// releasesTextViewNoneFound.setText(String.format(getActivity()
			// .getString(R.string.MainActivity_JustAddedInformation),
			// preferencesService.getJustAddedTimePeriod()));
			// } else {
			releasesTextViewNoneFound.setVisibility(View.GONE);
			// }
			setReleases(result.getData());
		}

		@Override
		public void onLoaderReset(Loader<AsyncResult<List<Release>>> result) {
			setReleases(null);
		}
	}
}
