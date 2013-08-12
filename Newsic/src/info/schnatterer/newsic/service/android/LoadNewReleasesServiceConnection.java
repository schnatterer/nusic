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

	private LoadNewReleasesServiceConnection(Context context,
			ArtistProgressListener artistProcessedListener,
			boolean updateOnlyIfNeccesary) {
		this.context = context;
		this.artistProcessedListener = artistProcessedListener;
		this.updateOnlyIfNeccesary = updateOnlyIfNeccesary;
	}

	/**
	 * Starts and binds the service, then executes the service method.
	 * 
	 * @param context
	 * @param artistProcessedListener
	 * @param updateOnlyIfNeccesary
	 * @return
	 */
	public static LoadNewReleasesServiceConnection startAndBind(
			Context context, ArtistProgressListener artistProcessedListener,
			boolean updateOnlyIfNeccesary) {
		// Start service (to make sure it can run independently from the app)
		Intent intent = new Intent(context, LoadNewReleasesService.class);
		context.startService(intent);
		// Now bind to service
		LoadNewReleasesServiceConnection connection = new LoadNewReleasesServiceConnection(
				context, artistProcessedListener, updateOnlyIfNeccesary);
		context.bindService(intent, connection, Context.BIND_NOT_FOREGROUND);
		return connection;
	}

	public void unbind() {
		context.unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		LoadNewReleasesServiceBinder binder = (LoadNewReleasesServiceBinder) service;
		LoadNewReleasesService loadNewReleasesService = binder.getService();
		loadNewReleasesService.refreshReleases(updateOnlyIfNeccesary,
				artistProcessedListener);
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
	}
};