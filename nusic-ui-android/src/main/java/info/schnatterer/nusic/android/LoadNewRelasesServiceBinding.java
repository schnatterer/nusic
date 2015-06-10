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
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.android.service.LoadNewReleasesService;
import info.schnatterer.nusic.android.service.LoadNewReleasesServiceConnection;
import info.schnatterer.nusic.android.util.Toast;
import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.SyncReleasesService;
import info.schnatterer.nusic.core.event.ArtistProgressListener;
import info.schnatterer.nusic.data.model.Artist;
import info.schnatterer.nusic.ui.R;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Holds the binding to the {@link LoadNewReleasesService} via
 * {@link LoadNewReleasesServiceConnection}. Allows for executing the service
 * method {@link SyncReleasesService#refreshReleases(boolean)} and visualizes
 * its result in a {@link ProgressDialog}.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewRelasesServiceBinding {
	private static final Logger LOG = LoggerFactory
			.getLogger(LoadNewRelasesServiceBinding.class);

	/**
	 * Context in which the {@link #progressDialog} is displayed
	 */
	private Activity activity;

	private ProgressDialog progressDialog = null;
	private List<Artist> errorArtists;
	private int totalArtists = 0;

	private LoadNewReleasesServiceConnection loadNewReleasesServiceConnection = null;
	private ProgressListener artistProcessedListener = new ProgressListener();

	private boolean isDataChanged = false;

	/**
	 * Executes {@link SyncReleasesService#refreshReleases(boolean)} within
	 * {@link LoadNewReleasesService} in a separate thread.
	 * 
	 * @param activity
	 *            activity that is used to display the {@link ProgressDialog}
	 * @param updateOnlyIfNeccesary
	 *            launch the service but do the update only
	 * @return <code>true</code> if refresh was started. <code>false</code> if
	 *         already in progress.
	 */
	public boolean refreshReleases(Activity activity,
			boolean updateOnlyIfNeccesary) {
		// Make sure the progress dialog is bound to any new activty
		updateActivity(activity);

		if (loadNewReleasesServiceConnection != null
				&& loadNewReleasesServiceConnection.isBound()) {
			/*
			 * Execute the service method, if not running already. Pass/update
			 * listener.
			 */
			LOG.debug(
					"Service already bound. Calling refreshReleases()");
			return loadNewReleasesServiceConnection.getLoadNewReleasesService()
					.refreshReleases(updateOnlyIfNeccesary,
							artistProcessedListener);
		} else {
			// Start service and bind to it
			LOG.debug("Service not bound. Binding");
			loadNewReleasesServiceConnection = startAndBindService(activity,
					updateOnlyIfNeccesary);
			return true;
		}
	}

	public boolean isRunning() {
		if (loadNewReleasesServiceConnection != null
				&& loadNewReleasesServiceConnection.isBound()) {
			return loadNewReleasesServiceConnection.getLoadNewReleasesService()
					.isRunning();
		} else {
			LOG.warn("Service unexpectedly not bound, assuming it's not running");
			return false;
		}
	}

	/**
	 * Start {@link LoadNewReleasesService}, then binds to it. In doing so, the
	 * service can hopefully linger on after the unbinding (if it still is
	 * running).
	 * 
	 * @param packageContext
	 *            some context start the service from
	 * @param updateOnlyIfNeccesary
	 * @return
	 */
	private LoadNewReleasesServiceConnection startAndBindService(
			Context packageContext, Boolean updateOnlyIfNeccesary) {
		boolean startRightAway = false;
		boolean updateOnlyIfNeccesaryPrimitive = true;
		if (updateOnlyIfNeccesary != null) {
			startRightAway = true;
			updateOnlyIfNeccesaryPrimitive = updateOnlyIfNeccesary;
		}

		errorArtists = new LinkedList<Artist>();
		totalArtists = 0;

		return LoadNewReleasesServiceConnection.startAndBind(packageContext,
				startRightAway, artistProcessedListener,
				updateOnlyIfNeccesaryPrimitive);
	}

	/**
	 * This should be called whenever when the application is
	 * paused/destroyed/stopped by the system. Don't forget to call
	 * {@link #bindService()}.
	 */
	public void unbindService() {
		if (loadNewReleasesServiceConnection != null) {
			LOG.debug("Unbinding service");
			loadNewReleasesServiceConnection.unbind();
			loadNewReleasesServiceConnection = null;
		}
	}

	/**
	 * Sets a new {@link Activity} for the {@link ProgressDialog}.
	 * 
	 * @param newActivity
	 *            can be <code>null</code>
	 */
	public void updateActivity(Activity newActivity) {
		if (this.activity != newActivity) {
			this.activity = newActivity;
			hideProgressDialog();
		}
	}

	/**
	 * Shows the context dialog, if there is any progress going on. Useful if
	 * the dialog might have been hidden.
	 */
	public void showDialog() {
		if (progressDialog != null) {
			progressDialog.show();
		}
	}

	public void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * Set the progress of the {@link ProgressDialog}.
	 * 
	 * @param progress
	 * @param max
	 */
	private void setProgress(int progress, int max) {
		if (progressDialog == null) {
			// Try to show the dialog is shown
			progressDialog = showDialog(progress, max);
		}
		if (progressDialog != null) {
			progressDialog.setProgress(progress);
		}
	}

	/**
	 * This should only be called from from the main thread (e.g. from
	 * {@link #onProgressUpdate(Object...)}).
	 * 
	 * @param progress
	 * @param max
	 * @return
	 */
	private ProgressDialog showDialog(int progress, int max) {
		ProgressDialog dialog = null;
		if (activity != null) {
			dialog = new ProgressDialog(activity);
			dialog.setMessage(activity
					.getString(R.string.LoadNewReleasesBinding_CheckingArtists));
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(max);
			dialog.setProgress(progress);
			dialog.show();
		}
		return dialog;
	}

	/**
	 * Handles updating the {@link ProgressDialog}.
	 * 
	 * @author schnatterer
	 * 
	 */
	private class ProgressListener implements ArtistProgressListener {
		@Override
		public void onProgressStarted(final int nEntities) {
			totalArtists = nEntities;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = showDialog(0, nEntities);
				}
			});
		}

		@Override
		public void onProgress(final Artist entity, final int progress,
				final int max, Throwable potentialException) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (entity != null) {
						setProgress(progress, max);
					}
				}
			});
			if (potentialException != null) {
				errorArtists.add(entity);
			}
		}

		@Override
		public void onProgressFinished(Boolean result) {
			notifyListeners(result);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideProgressDialog();
					if (errorArtists != null && errorArtists.size() > 0) {
						Toast.toast(
								activity,
								R.string.LoadNewReleasesBinding_finishedWithErrors,
								errorArtists.size(), totalArtists);
					}
				}
			});
		}

		@Override
		public void onProgressFailed(Artist entity, int progress, int max,
				Boolean result, final Throwable potentialException) {
			notifyListeners(result);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideProgressDialog();
					if (potentialException != null) {
						if (potentialException instanceof ServiceException) {
							Toast.toast(
									activity,
									activity.getString(R.string.LoadNewReleasesBinding_errorFindingReleases)
											+ potentialException
													.getLocalizedMessage());
						} else {
							Toast.toast(
									activity,
									activity.getString(R.string.LoadNewReleasesBinding_errorFindingReleasesGeneric)
											+ potentialException.getClass()
													.getSimpleName());
						}
					}
				}
			});
		}

		private void runOnUiThread(Runnable runnable) {
			if (activity != null) {
				activity.runOnUiThread(runnable);
			}

		}
	}

	/**
	 * Checks if the service has finished and changed data since the last call
	 * of this method. If so returns <code>true</code> and resets the variable.
	 * So all subsequent calls to the method will return <code>false</code>
	 * until the next change of data.
	 * 
	 * This is somewhat opposed to the listener mechanism, however this can be
	 * useful to check if the service has been active while the activity was
	 * paused.
	 * 
	 * @return
	 */
	public boolean checkDataChanged() {
		if (isDataChanged) {
			isDataChanged = false;
			return true;
		}
		return false;
	}

	protected void notifyListeners(Boolean resultChanged) {
		boolean primitiveResult = true;
		LOG.debug(
				"Service: Notifying activity if result changed. ResultChanged="
						+ resultChanged + ". Activity=" + activity);
		// Be defensive: Only if explicitly nothing changed
		if (resultChanged != null && resultChanged.equals(false)) {
			primitiveResult = false;
		}
		isDataChanged = primitiveResult;
		if (isDataChanged && activity != null) {
			activity.onContentChanged();
		}
	}
}
