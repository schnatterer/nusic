package info.schnatterer.nusic.test.service.impl;

import info.schnatterer.nusic.service.impl.QueryMusicMetadataServiceMusicBrainz;

import java.util.LinkedList;
import java.util.List;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;

public class QueryMusicMetadataServiceMusicBrainzMock extends
		QueryMusicMetadataServiceMusicBrainz {
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
	protected org.musicbrainz.controller.Release createReleaseSearch() {
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

//	@Override
//	protected List<ReleaseResultWs2> findReleases(String searchText) {
//		lastSearchText = searchText;
//		List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
//		for (ReleaseWs2 releaseWs2 : mockedReleases) {
//			ReleaseResultWs2 result = new ReleaseResultWs2();
//			result.setRelease(releaseWs2);
//			ret.add(result);
//		}
//
//		return ret;
//	}
}