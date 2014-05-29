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
package info.schnatterer.nusic.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;

public class QueryMusicMetadataServiceMusicBrainzMock extends
		QueryMusicMetadataServiceMusicBrainz {
	public QueryMusicMetadataServiceMusicBrainzMock() {
		super(null, null, null);
	}

	private String lastSearchText;
	private List<ReleaseWs2> mockedReleases;

	public String getLastSearchText() {
		return lastSearchText;
	}

	public List<ReleaseWs2> getMockedReleases() {
		return mockedReleases;
	}

	public void setMockedReleases(List<ReleaseWs2> mockedReleases) {
		this.mockedReleases = mockedReleases;
	}

	@Override
	protected org.musicbrainz.controller.Release createReleaseSearch(
			String userAgentName, String userAgentVersion,
			String userAgentContact) {
		return new org.musicbrainz.controller.Release() {
			@Override
			public boolean hasMore() {
				return false;
			}

			@Override
			public void search(String searchText) {
				super.search(searchText);
				lastSearchText = searchText;
			}

			@Override
			public List<ReleaseResultWs2> getFirstSearchResultPage()
					throws MBWS2Exception {
				List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
				for (ReleaseWs2 releaseWs2 : mockedReleases) {
					ReleaseResultWs2 result = new ReleaseResultWs2();
					result.setRelease(releaseWs2);
					result.getEntity().setIdUri("someId");
					ret.add(result);
				}

				return ret;
			}
		};
	}

	// @Override
	// protected List<ReleaseResultWs2> findReleases(String searchText) {
	// lastSearchText = searchText;
	// List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
	// for (ReleaseWs2 releaseWs2 : mockedReleases) {
	// ReleaseResultWs2 result = new ReleaseResultWs2();
	// result.setRelease(releaseWs2);
	// ret.add(result);
	// }
	//
	// return ret;
	// }
}