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
package info.schnatterer.nusic.android.service;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.logic.ReleaseRefreshService;
import info.schnatterer.nusic.android.service.LoadNewReleasesService.LoadNewReleasesServiceBinder;
import info.schnatterer.nusic.logic.event.ArtistProgressListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Defines callbacks for service binding, passed to bindService().
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceConnection implements ServiceConnection {
	private Context context = null;
	private ArtistProgressListener artistProcessedListener = null;
	private boolean updateOnlyIfNeccesary = false;
	private LoadNewReleasesService loadNewReleasesService = null;
	private boolean startRightAway;

	private LoadNewReleasesServiceConnection(Context context,
			boolean startRightAway,
			ArtistProgressListener artistProcessedListener,
			boolean updateOnlyIfNeccesary) {
		this.context = context;
		this.startRightAway = startRightAway;
		this.artistProcessedListener = artistProcessedListener;
		this.updateOnlyIfNeccesary = updateOnlyIfNeccesary;
	}

	/**
	 * Starts and binds the service, then executes the service method.
	 * 
	 * @param context
	 * @param startRightAway
	 *            calls
	 *            {@link LoadNewReleasesService#refreshReleases(boolean, ArtistProgressListener)}
	 *            right after the service is started and bound
	 * @param artistProcessedListener
	 *            does only matter if <code>startRightAway</code> is
	 *            <code>true</code><br/>
	 * @param updateOnlyIfNeccesary
	 *            (does only matter if <code>startRightAway</code> is
	 *            <code>true</code>)<br/>
	 * 
	 *            if <code>true</code> the refresh is only done when
	 *            {@link ReleaseRefreshService#isUpdateNeccesarry()} returns
	 *            <code>true</code>. Otherwise, the refresh is done at any case.
	 * 
	 * @return
	 */
	public static LoadNewReleasesServiceConnection startAndBind(
			Context context, boolean startRightAway,
			ArtistProgressListener artistProcessedListener,
			boolean updateOnlyIfNeccesary) {
		// Start service (to make sure it can run independent of the app)
		Intent intent = new Intent(context, LoadNewReleasesService.class);
		context.startService(intent);
		// Now bind to service
		LoadNewReleasesServiceConnection connection = new LoadNewReleasesServiceConnection(
				context, startRightAway, artistProcessedListener,
				updateOnlyIfNeccesary);
		context.bindService(intent, connection, Context.BIND_NOT_FOREGROUND);
		return connection;
	}

	public void unbind() {
		context.unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.d(Constants.LOG,"Service connected.");
		LoadNewReleasesServiceBinder binder = (LoadNewReleasesServiceBinder) service;
		loadNewReleasesService = binder.getService();
		if (startRightAway) {
			loadNewReleasesService.refreshReleases(updateOnlyIfNeccesary,
					artistProcessedListener);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
	}

	/**
	 * @return the bound instance of the service. Might be <code>null</code> if
	 *         not bound. See {@link #isBound()}.
	 */
	public LoadNewReleasesService getLoadNewReleasesService() {
		return loadNewReleasesService;
	}

	public boolean isBound() {
		return loadNewReleasesService != null;
	}

};