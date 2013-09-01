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
package info.schnatterer.nusic.db.loader;

import info.schnatterer.nusic.db.dao.impl.ReleaseDaoSqlite;
import info.schnatterer.nusic.db.model.Release;

import java.util.Date;
import java.util.List;

import android.content.Context;

public class ReleaseLoader extends
		AbstractAsyncSqliteLoader<List<Release>, Release, ReleaseDaoSqlite> {

	private Date dateCreatedGt;

	/**
	 * @param context
	 * @param dateCreatedGt
	 *            the releases should all have a date created that is greater
	 *            than this
	 */
	public ReleaseLoader(Context context, Date dateCreatedGt) {
		super(context);
		this.dateCreatedGt = dateCreatedGt;
	}

	@Override
	protected ReleaseDaoSqlite createDao(Context context) {
		return new ReleaseDaoSqlite(context);
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		if (dateCreatedGt == null) {
			return getDao().findAll();
		} else {
			return getDao().findJustCreated(dateCreatedGt);
		}
	}
}
