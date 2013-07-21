package info.schnatterer.newsic.util;

import info.schnatterer.newsic.Application;

import java.util.Locale;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public final class DefaultLocale {
	public DefaultLocale() {
		// Don't instantiate
	}

	public static final Locale DEFAULT_LOCALE = Locale.US;

	// private static Resources resourcesUs = createDefaultResources();
	//
	// /**
	// * Creates a resource instance in the "default" resources (device
	// * independent) that can be used to log error message (R.string.*)
	// * independent of the current locale of the device.
	// *
	// * @return
	// */
	// private static Resources createDefaultResources() {
	// Resources standardResources = Application.getContext().getResources();
	// AssetManager assets = standardResources.getAssets();
	// DisplayMetrics metrics = standardResources.getDisplayMetrics();
	// Configuration config = new Configuration(
	// standardResources.getConfiguration());
	// config.locale = Locale.US;
	// Resources resources = new Resources(assets, metrics, config);
	// return resources;
	// }
	//
	// private static Resources createDeviceSpecificResources() {
	// Resources standardResources = Application.getContext().getResources();
	// AssetManager assets = standardResources.getAssets();
	// DisplayMetrics metrics = standardResources.getDisplayMetrics();
	// Resources resources = new Resources(assets, metrics,
	// standardResources.getConfiguration());
	// return resources;
	// }

	/**
	 * Returns a string in the "default" resources (device independent) that can
	 * be used to log error message (R.string.*) independet of the current
	 * locale of the device.
	 * 
	 * <b>Note</b>: As there seems to be no way to get a string in a differnent
	 * loacle this code changes the locale of the app to default, gets the
	 * strings and then changes back. Better use this carefully, e.g. only to
	 * output localized exception messages in the default language.
	 * 
	 * @return
	 */
	public static String getStringInDefaultLocale(int resId) {
		Resources currentResources = Application.getContext().getResources();
		AssetManager assets = currentResources.getAssets();
		DisplayMetrics metrics = currentResources.getDisplayMetrics();
		Configuration config = new Configuration(
				currentResources.getConfiguration());
		config.locale = DEFAULT_LOCALE;
		/*
		 * Note: This (temporiarily) changes the devices locale!
		 * 
		 * TODO find a better way to get the string in the specific locale
		 * http:/
		 * /stackoverflow.com/questions/17771531/android-how-to-get-string-
		 * in-specific-locale-without-changing-the-current-local
		 */
		Resources defaultLocaleResources = new Resources(assets, metrics,
				config);
		String string = defaultLocaleResources.getString(resId);
		// Restore device-specific locale
		new Resources(assets, metrics, currentResources.getConfiguration());
		return string;
		// String string = createDefaultResources().getString(resId);
		// createDeviceSpecificResources();
		// return string;
	}

}
