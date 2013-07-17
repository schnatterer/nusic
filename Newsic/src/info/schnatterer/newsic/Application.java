package info.schnatterer.newsic;

import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class Application extends android.app.Application {

	private static Context context;
	private static Resources defaultResources;

	@Override
	public void onCreate() {
		super.onCreate();
		Application.context = getApplicationContext();
		Application.defaultResources = createDefaultResources();

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
	 * Returns a reference to the default resources.
	 * 
	 * @return
	 */
	public static Resources getDefaulResources() {
		return defaultResources;
	}

	private static Resources createDefaultResources() {
		Resources standardResources = Application.getContext().getResources();
		AssetManager assets = standardResources.getAssets();
		DisplayMetrics metrics = standardResources.getDisplayMetrics();
		Configuration config = new Configuration(
				standardResources.getConfiguration());
		config.locale = Locale.US;
		return defaultResources = new Resources(assets, metrics, config);
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
}
