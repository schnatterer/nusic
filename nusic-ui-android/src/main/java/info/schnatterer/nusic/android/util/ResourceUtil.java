/* Copyright (C) 2015 Johannes Schnatterer
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
package info.schnatterer.nusic.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageManager;

public class ResourceUtil {
	private static final Logger LOG = LoggerFactory
			.getLogger(ResourceUtil.class);

	/** Name of the resource type "string" as in <code>@string/...</code> */
	public static final String DEF_TYPE_STRING = "string";

	/**
	 * Returns the string value of a string resource (e.g. defined in
	 * <code>values.xml</code>).
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param name
	 * 
	 * @return the value of the string resource or <code>null</code> if no
	 *         resource found for id
	 */
	public static String getStringByName(Context context, String name) {
		int resourceId = getResourceId(context, DEF_TYPE_STRING, name);
		if (resourceId != 0) {
			return context.getString(resourceId);
		} else {
			return null;
		}
	}

	/**
	 * Finds the numeric id of a string resource (e.g. defined in
	 * <code>values.xml</code>).
	 * 
	 * @param defType
	 *            Optional default resource type to find, if "type/" is not
	 *            included in the name. Can be null to require an explicit type.
	 *            e.g. {@link #DEF_TYPE_STRING}
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param name
	 *            the name of the desired resource
	 * @return the associated resource identifier. Returns 0 if no such resource
	 *         was found. (0 is not a valid resource ID.)
	 */
	public static int getResourceId(Context context, String defType, String name) {
		return context.getResources().getIdentifier(name, defType,
				context.getPackageName());
	}

	/**
	 * Reads the human readable version name from the package manager.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @return the version or <code>ErrorReadingVersion</code> if an error
	 *         Occurs.
	 */
	public static String createVersionName(Context context) {
		String versionName;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			LOG.warn("Unable to read version name", e);
			versionName = "ErrorReadingVersion";
		}
		return versionName;
	}
}
