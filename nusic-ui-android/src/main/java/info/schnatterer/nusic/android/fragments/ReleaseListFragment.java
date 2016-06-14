/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.fragments;

import info.schnatterer.nusic.Constants.Loaders;
import info.schnatterer.nusic.android.activities.NusicWebView;
import info.schnatterer.nusic.android.adapters.ReleaseListAdapter;
import info.schnatterer.nusic.android.loaders.AsyncResult;
import info.schnatterer.nusic.android.loaders.ReleaseLoader;
import info.schnatterer.nusic.android.util.Toast;
import info.schnatterer.nusic.core.ArtistService;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.data.model.Release;
import info.schnatterer.nusic.ui.R;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.fragment.RoboFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * Fragment that loads a list of releases from the local database and displays
 * it.
 * 
 * Which releases are queried and which loader is used can be decided in the
 * intent that create the fragment using the following extra.
 * 
 * <ul>
 * <li>{@value #EXTRA_LOADER_ID} the ID of the underlying loader</li>
 * </ul>
 * 
 * @author schnatterer
 *
 */
public class ReleaseListFragment extends RoboFragment {
	private static final Logger LOG = LoggerFactory
			.getLogger(ReleaseListFragment.class);
	/**
	 * Key to the creating intent's extras that contains the {@link Loaders}<br/>
	 * See {@link #loaderId}.
	 */
	public static final String EXTRA_LOADER_ID = "nusic.intent.releaseList.loaderId";

	/** The loader that is connected to the data displayed in the fragment. */
	private int loaderId = -1;

	@Inject
	private ReleaseListAdapter releasesListViewAdapter;
	ListView releasesListView;
	private TextView releasesTextViewNoneFound;
	/** Progress animation when loading releases from db */
	private ProgressBar progressBar;
	@Inject
	private ReleaseService releaseService;
	@Inject
	private ArtistService artistService;
	@Inject
	private Provider<ReleaseLoader> releaseLoaderProvider;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.release_list_layout, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {

			loaderId = getArguments().getInt(EXTRA_LOADER_ID);

		} catch (Exception e) {
			LOG.warn(
					"Error reading arguments from bundle passed by parent activity",
					e);
		}

		releasesListView = (ListView) getView().findViewById(
				R.id.releasesListView);
		releasesTextViewNoneFound = (TextView) getView().findViewById(
				R.id.releasesTextViewNoneFound);
		progressBar = (ProgressBar) getView().findViewById(
				R.id.releasesProgressBar);

		progressBar.setVisibility(View.GONE);

		releasesTextViewNoneFound.setVisibility(View.GONE);

		registerForContextMenu(releasesListView);
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Release release = (Release) releasesListView
						.getItemAtPosition(position);
				Intent launchBrowser = new Intent("", Uri.parse(release
						.getMusicBrainzUri()), getActivity(),
						NusicWebView.class);
				startActivity(launchBrowser);
			}

		});
		releasesListView.setAdapter(releasesListViewAdapter);
		releasesListView.setOnScrollListener(new PauseOnScrollListener(
				releasesListViewAdapter.getImageLoader(), false, true));

		displayLoading();
		// Load releases from local db
		getActivity().getSupportLoaderManager().initLoader(loaderId, null,
				new ReleaseLoaderCallbacks());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if (v.getId() == R.id.releasesListView) {
		MenuInflater inflater = getActivity().getMenuInflater();
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Release release = (Release) releasesListView
				.getItemAtPosition(info.position);
		menu.setHeaderTitle(release.getArtistName() + " - "
				+ release.getReleaseName());

		inflater.inflate(R.menu.release_list_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// If this callback was invoked on the visible fragment instance
		if (getUserVisibleHint()) {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Release release = (Release) releasesListView
					.getItemAtPosition(info.position);
			try {
				if (item.getItemId() == R.id.releaseListMenuHideRelease) {
					displayLoading();
					release.setHidden(true);
					releaseService.update(release);
					getActivity().onContentChanged();
				} else if (item.getItemId() == R.id.releaseListMenuHideAllByArtist) {
					Artist artist = release.getArtist();
					artist.setHidden(true);
					artistService.update(artist);
					getActivity().onContentChanged();
				} else {
					return super.onContextItemSelected(item);
				}
			} catch (ServiceException e) {
				LOG.warn("Error hiding release/artist", e);
				Toast.toast(getActivity(), e.getLocalizedMessage());
			}
			return true; // Finish processing fragment instances
		} else {
			return false; // Pass the event to the next fragment
		}
	}

	protected void setReleases(List<Release> result) {
		releasesListViewAdapter.show(result);
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
	 * Handles callbacks from the loader manager for {@link ReleaseListFragment}
	 * .
	 * 
	 * @author schnatterer
	 * 
	 */
	private class ReleaseLoaderCallbacks implements
			LoaderManager.LoaderCallbacks<AsyncResult<List<Release>>> {

		private int loaderId;

		@Override
		public Loader<AsyncResult<List<Release>>> onCreateLoader(int id,
				Bundle args) {
			loaderId = id;
			ReleaseLoader releaseLoader = releaseLoaderProvider.get();
			releaseLoader.setLoaderId(loaderId);
			return releaseLoader;

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
				if (loaderId == Loaders.RELEASE_LOADER_JUST_ADDED) {
					releasesTextViewNoneFound
							.setText(R.string.MainActivity_noNewReleasesFound);
				} else {
					releasesTextViewNoneFound
							.setText(R.string.MainActivity_noReleasesFound);
				}
				return;
			}
			releasesTextViewNoneFound.setVisibility(View.GONE);
			setReleases(result.getData());
		}

		@Override
		public void onLoaderReset(Loader<AsyncResult<List<Release>>> result) {
			setReleases(null);
		}
	}
}
