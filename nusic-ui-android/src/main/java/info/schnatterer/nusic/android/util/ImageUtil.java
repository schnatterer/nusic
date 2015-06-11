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

import info.schnatterer.nusic.Constants;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

public class ImageUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ImageUtil.class);

	private ImageUtil() {
	}

	public static Bitmap createScaledBitmap(InputStream inputStream,
			Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return createScaledBitmapLegacy(inputStream, context);
		} else {
			return createScaledBitmapModern(inputStream, context);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static Bitmap createScaledBitmapModern(InputStream inputStream,
			Context context) {
		Bitmap artwork = BitmapFactory.decodeStream(inputStream);
		if (artwork != null) {
			artwork = Bitmap.createScaledBitmap(
					artwork,
					(int) context.getResources().getDimension(
							android.R.dimen.notification_large_icon_width),
					(int) context.getResources().getDimension(
							android.R.dimen.notification_large_icon_height),
					false);
		} else {
			LOG.debug(
					"Unable to read bitmap from stream. Stream null?");
		}
		return artwork;
	}

	private static Bitmap createScaledBitmapLegacy(InputStream inputStream,
			Context context) {
		/*
		 * As we don't know the size of the notification icon bellow API lvl 11,
		 * theses devices will just use the standard icon.
		 */
		return null;
	}
}
