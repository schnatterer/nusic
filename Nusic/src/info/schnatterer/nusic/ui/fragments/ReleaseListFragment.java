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
package info.schnatterer.nusic.ui.fragments;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.Constants.Loaders;
import info.schnatterer.nusic.R;
import info.schnatterer.nusic.application.NusicApplication;
import info.schnatterer.nusic.db.model.Artist;
import info.schnatterer.nusic.db.model.Release;
import info.schnatterer.nusic.service.ArtistService;
import info.schnatterer.nusic.service.ReleaseRefreshService;
import info.schnatterer.nusic.service.ReleaseService;
import info.schnatterer.nusic.service.ServiceException;
import info.schnatterer.nusic.service.impl.ArtistServiceImpl;
import info.schnatterer.nusic.service.impl.ReleaseRefreshServiceImpl;
import info.schnatterer.nusic.service.impl.ReleaseServiceImpl;
import info.schnatterer.nusic.ui.adapters.ReleaseListAdapter;
import info.schnatterer.nusic.ui.loaders.AsyncResult;
import info.schnatterer.nusic.ui.loaders.ReleaseLoaderAll;
import info.schnatterer.nusic.ui.loaders.ReleaseLoaderAvailable;
import info.schnatterer.nusic.ui.loaders.ReleaseLoaderJustCreated;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
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

import com.actionbarsherlock.app.SherlockFragment;
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
public class ReleaseListFragment extends SherlockFragment {
	/**
	 * Key to the creating intent's extras that contains the {@link Loaders}<br/>
	 * See {@link #loaderId}.
	 */
	public static final String EXTRA_LOADER_ID = "nusic.intent.releaseList.loaderId";

	/** The loader that is connected to the data displayed in the fragment. */
	private int loaderId;

	private ListView releasesListView;
	private ReleaseListAdapter releasesListViewAdapter = null;
	private TextView releasesTextViewNoneFound;
	/** Progress animation when loading releases from db */
	private ProgressBar progressBar;
	private ReleaseRefreshService releaseRefreshService;
	private ReleaseService releaseService;
	private ArtistService artistService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {

			loaderId = getArguments().getInt(EXTRA_LOADER_ID);

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

		registerForContextMenu(releasesListView);
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Release release = (Release) releasesListView
						.getItemAtPosition(position);
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri
						.parse(release.getMusicBrainzUri()));
				startActivity(launchBrowser);
			}

		});
		releasesListViewAdapter = new ReleaseListAdapter(getActivity());
		releasesListView.setAdapter(releasesListViewAdapter);
		releasesListView.setOnScrollListener(new PauseOnScrollListener(
				releasesListViewAdapter.getImageLoader(), false, true));

		displayLoading();
		// Load releases from local db
		getActivity().getSupportLoaderManager().initLoader(loaderId, null,
				new ReleaseLoaderCallbacks());
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if (v.getId() == R.id.releasesListView) {
		MenuInflater inflater = getSherlockActivity().getMenuInflater();
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
				switch (item.getItemId()) {
				case R.id.releaseListMenuHideRelease:
					release.setHidden(true);
					getReleaseService().update(release);
					getActivity().onContentChanged();
					break;
				case R.id.releaseListMenuHideAllByArtist:
					Artist artist = release.getArtist();
					artist.setHidden(true);
					getArtistService().update(artist);
					getActivity().onContentChanged();
					break;
				default:
					return super.onContextItemSelected(item);
				}
			} catch (ServiceException e) {
				Log.w(Constants.LOG, "Error hiding release/artist", e);
				NusicApplication.toast(e.getLocalizedMessageId());
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
	 * Marks content as changed, which leads to reloading on the next load.
	 */
	public void onContentChanged() {
		if (isVisible()) {
			displayLoading();
		}
		getActivity().getSupportLoaderManager().getLoader(loaderId)
				.onContentChanged();
	}

	protected ReleaseRefreshService getReleaseRefreshService() {
		if (releaseRefreshService == null) {
			releaseRefreshService = new ReleaseRefreshServiceImpl(
					getSherlockActivity());
		}

		return releaseRefreshService;
	}

	protected ReleaseService getReleaseService() {
		if (releaseService == null) {
			releaseService = new ReleaseServiceImpl(getSherlockActivity());
		}

		return releaseService;
	}

	protected ArtistService getArtistService() {
		if (artistService == null) {
			artistService = new ArtistServiceImpl(getSherlockActivity());
		}

		return artistService;
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
			switch (loaderId) {
			case Loaders.RELEASE_LOADER_ALL:
				return new ReleaseLoaderAll(getActivity());
			case Loaders.RELEASE_LOADER_JUST_ADDED:
				return new ReleaseLoaderJustCreated(getActivity());
			case Loaders.RELEASE_LOADER_ANNOUNCED:
				return new ReleaseLoaderAvailable(getActivity(), false);
			case Loaders.RELEASE_LOADER_AVAILABLE:
				return new ReleaseLoaderAvailable(getActivity(), true);
			default:
				Log.w(Constants.LOG,
						"Requested loader ID is not a defined release loader: "
								+ loaderId
								+ ". Returning loader that loads all releases");
				return new ReleaseLoaderAll(getActivity());
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
