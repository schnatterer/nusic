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
package info.schnatterer.nusic.util;

import info.schnatterer.nusic.Application;

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
	}

}
