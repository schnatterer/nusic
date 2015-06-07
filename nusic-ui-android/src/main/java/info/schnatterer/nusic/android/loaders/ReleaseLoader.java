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

import info.schnatterer.nusic.Constants.Loaders;
import info.schnatterer.nusic.core.ReleaseService;
import info.schnatterer.nusic.data.model.Release;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * Creates an {@link android.content.AsyncTaskLoader} for {@link Release}s. The
 * method called in {@link #doLoadInBackground()} can be defined via
 * {@link #setLoaderId(int)}.
 * 
 * @author schnatterer
 *
 */
public class ReleaseLoader extends
		AbstractAsyncSqliteLoader<List<Release>, Release> {
	private static final Logger LOG = LoggerFactory
			.getLogger(ReleaseLoader.class);

	@Inject
	private ReleaseService releaseService;
	private int loaderId = Loaders.RELEASE_LOADER_ALL;

	@Inject
	public ReleaseLoader(Context context) {
		super(context);
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		switch (loaderId) {
		case Loaders.RELEASE_LOADER_ALL:
			return releaseService.findAllNotHidden();
		case Loaders.RELEASE_LOADER_JUST_ADDED:
			return releaseService.findJustCreated();
		case Loaders.RELEASE_LOADER_ANNOUNCED: {
			return releaseService.findAvailableToday(false);
		}
		case Loaders.RELEASE_LOADER_AVAILABLE: {
			return releaseService.findAvailableToday(true);
		}
		default:
			LOG.warn("Requested loader ID is not a defined release loader: "
					+ loaderId + ". Returning loader that loads all releases");
			return releaseService.findAllNotHidden();
		}
	}

	public void setLoaderId(int loaderId) {
		this.loaderId = loaderId;
	}
}
