package info.schnatterer.newsic.service.impl;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.service.ConnectivityService;
import info.schnatterer.newsic.service.PreferencesService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityServiceAndroid implements ConnectivityService {
	private static ConnectivityServiceAndroid instance;
	//private Set<ConnectivityChangedListener> connectivityChangedListeners = new HashSet<ConnectivityChangedListener>();
	private PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	/**
	 * Creates a {@link ConnectivityServiceAndroid}.
	 */
	protected ConnectivityServiceAndroid() {
	}

	/**
	 * 
	 * @return A singleton of this class
	 */
	public static final ConnectivityServiceAndroid getInstance() {
		if (instance == null) {
			instance = new ConnectivityServiceAndroid();
		}
		return instance;
	}

	@Override
	public boolean isOnline() {
		boolean state = false;
		final boolean isOnlyOnWifi = preferencesService.isUseOnlyWifi();

		/* Monitor network connections */
		final ConnectivityManager connectivityManager = (ConnectivityManager) Application
				.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		/* Wi-Fi connection */
		final NetworkInfo wifiNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null) {
			state = wifiNetwork.isConnectedOrConnecting();
		}

		/* Mobile data connection */
		final NetworkInfo mbobileNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mbobileNetwork != null) {
			if (!isOnlyOnWifi) {
				state = mbobileNetwork.isConnectedOrConnecting();
			}
		}

		/* Other networks */
		final NetworkInfo activeNetwork = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetwork != null) {
			if (!isOnlyOnWifi) {
				state = activeNetwork.isConnectedOrConnecting();
			}
		}

		return state;
	}

//	@Override
//	public void registerConnectivityChangeListener(
//			ConnectivityChangedListener connectivtyChangedListener) {
//		connectivityChangedListeners.add(connectivtyChangedListener);
//	}
//
//	@Override
//	public void unregisterConnectivityChangeListener(
//			ConnectivityChangedListener connectivtyChangedListener) {
//		connectivityChangedListeners.remove(connectivtyChangedListener);
//	}

//	private void onConnectivtyChanged(boolean isOnline) {
//		for (ConnectivityChangedListener connectivtyChangedListener : connectivityChangedListeners) {
//			if (isOnline) {
//				connectivtyChangedListener.onConnectionEstablished();
//			} else {
//				connectivtyChangedListener.onConnectionLost();
//			}
//		}
//	}
//
//	public class ConnectivityChangedReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(final Context context, final Intent intent) {
//			onConnectivtyChanged(isOnline());
//		}
//	}
}
