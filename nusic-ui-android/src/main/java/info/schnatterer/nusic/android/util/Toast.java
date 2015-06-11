/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * Convenience Wrapper for {@link android.widget.Toast}.
 * 
 * @author schnatterer
 *
 */
public class Toast {
	private static final Logger LOG = LoggerFactory.getLogger(Toast.class);

	private Toast() {
	}

	/**
	 * Prints a toast to the screen.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param text
	 *            The text to show. Can be formatted text.
	 */
	public static void toast(Context context, String text) {
		LOG.info("Toast: " + text);
		android.widget.Toast.makeText(context, text,
				android.widget.Toast.LENGTH_LONG).show();
	}

	/**
	 * Prints a toast to the screen, using a localized string from the
	 * application's package's default string table.
	 * 
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param stringId
	 *            Resource id for the string
	 */
	public static void toast(Context context, int stringId) {
		toast(context, context.getString(stringId));
	}

	/**
	 * Prints a toast to the screen, using a localized formatted string.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param message
	 *            the format string (see {@link java.util.Formatter#format})
	 * @param args
	 *            the list of arguments passed to the formatter. If there are
	 *            more arguments than required by {@code format}, additional
	 *            arguments are ignored.
	 */
	public static void toast(Context context, String message, Object... args) {
		toast(context, String.format(message, args));
	}

	/**
	 * Prints a toast to the screen using a localized string from the
	 * application's package's default string table that is formatted.
	 * 
	 * @param context
	 *            The context to use. Usually your android.app.Application or
	 *            android.app.Activity object.
	 * @param stringId
	 *            Resource id for the string
	 * @param args
	 *            the list of arguments passed to the formatter. If there are
	 *            more arguments than required by {@code format}, additional
	 *            arguments are ignored.
	 */
	public static void toast(Context context, int stringId, Object... args) {
		toast(context, String.format(context.getString(stringId), args));
	}
}
