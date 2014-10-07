/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.android.loaders;

import info.schnatterer.nusic.data.model.Release;
import info.schnatterer.nusic.logic.ReleaseService;
import info.schnatterer.nusic.logic.impl.ReleaseServiceImpl;

import java.util.List;

import android.content.Context;

public class ReleaseLoaderAvailable extends
		AbstractAsyncSqliteLoader<List<Release>, Release> {

	private ReleaseService releaseService;
	/**
	 * <code>true</code> if releases available today should be display.
	 * Otherwise <code>false</code>.
	 */
	private boolean isAvailable;

	/**
	 * @param context
	 * @param isAvailable
	 *            <code>true</code> if releases available today should be
	 *            display. Otherwise <code>false</code>.
	 */
	public ReleaseLoaderAvailable(Context context, boolean isAvailable) {
		super(context);
		releaseService = new ReleaseServiceImpl(context);
		this.isAvailable = isAvailable;
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		return releaseService.findAvailableToday(isAvailable);
	}
}
