package info.schnatterer.newsic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Application extends android.app.Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		Application.context = getApplicationContext();
	}

	/**
	 * Returns the application context.
	 * 
	 * @return
	 */
	public static Context getContext() {
		return Application.context;
	}

	/**
	 * Used to determine if there is an active data connection and what type of
	 * connection it is if there is one
	 * 
	 * @return True if there is an active data connection, false otherwise.
	 */
	public static final boolean isOnline() {
		boolean state = false;
		// final boolean onlyOnWifi =
		// PreferenceUtils.getInstance(context).onlyOnWifi();

		/* Monitor network connections */
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

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
			// if (!onlyOnWifi) {
			state = mbobileNetwork.isConnectedOrConnecting();
			// }
		}

		/* Other networks */
		final NetworkInfo activeNetwork = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetwork != null) {
			// if (!onlyOnWifi) {
			state = activeNetwork.isConnectedOrConnecting();
			// }
		}

		return state;
	}

	/**
	 * Error message for {@link Throwable}s that might not contain a localized
	 * error message.
	 * 
	 * Tells the user that something unexpected has happened in his language and
	 * adds the name of the exception
	 * 
	 * @param t
	 * @return
	 */
	public static String createGenericErrorMessage(Throwable t) {
		return String.format(context.getString(R.string.GenericError), t
				.getClass().getSimpleName());
	}

	public static void toast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toast(int stringId) {
		Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG)
				.show();
	}

	public static void toast(String message, Object... args) {
		toast(String.format(message, args));
	}

	public static void toast(int stringId, Object... args) {
		toast(String.format(context.getString(stringId), args));
	}
}
