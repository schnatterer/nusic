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
package info.schnatterer.nusic.db.loader;

import static info.schnatterer.nusic.util.DateUtil.tomorrowMidnightUtc;
import info.schnatterer.nusic.db.dao.sqlite.ReleaseDaoSqlite;
import info.schnatterer.nusic.db.model.Release;

import java.util.List;

import android.content.Context;

public class ReleaseLoaderAvailable extends
		AbstractAsyncSqliteLoader<List<Release>, Release, ReleaseDaoSqlite> {

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
		this.isAvailable = isAvailable;
	}

	@Override
	protected ReleaseDaoSqlite createDao(Context context) {
		return new ReleaseDaoSqlite(context);
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		if (isAvailable) {
			return getDao().findByReleaseDateLessThan(tomorrowMidnightUtc());
		} else {
			return getDao().findByReleaseDateGreaterThanEqual(
					tomorrowMidnightUtc());
		}
	}
}
