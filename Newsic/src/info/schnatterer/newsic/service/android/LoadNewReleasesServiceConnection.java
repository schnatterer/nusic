package info.schnatterer.newsic.service.android;

import info.schnatterer.newsic.service.android.LoadNewReleasesService.LoadNewReleasesServiceBinder;
import info.schnatterer.newsic.service.event.ArtistProgressListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

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
	 *            <code>true</code>
	 * @param updateOnlyIfNeccesary
	 *            does only matter if <code>startRightAway</code> is
	 *            <code>true</code>
	 * @return
	 */
	public static LoadNewReleasesServiceConnection startAndBind(
			Context context, boolean startRightAway,
			ArtistProgressListener artistProcessedListener,
			boolean updateOnlyIfNeccesary) {
		// Start service (to make sure it can run independently from the app)
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