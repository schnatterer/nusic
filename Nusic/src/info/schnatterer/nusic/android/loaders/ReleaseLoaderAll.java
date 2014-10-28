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

import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.data.model.Release;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;

public class ReleaseLoaderAll extends
		AbstractAsyncSqliteLoader<List<Release>, Release> {

	@Inject
	public ReleaseLoaderAll(Context context) {
		super(context);
	}

	@Inject
	private ReleaseService releaseService;

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		return releaseService.findAllNotHidden();
	}
}
