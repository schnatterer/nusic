package info.schnatterer.newsic.test.service.impl;

import info.schnatterer.newsic.service.impl.ReleaseInfoServiceMusicBrainz;

import java.util.LinkedList;
import java.util.List;

import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;

public class ReleaseInfoServiceMusicBrainzMock extends
		ReleaseInfoServiceMusicBrainz {
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

	protected List<ReleaseResultWs2> findReleases(String searchText) {
		lastSearchText = searchText;
		List<ReleaseResultWs2> ret = new LinkedList<ReleaseResultWs2>();
		for (ReleaseWs2 releaseWs2 : mockedReleases) {
			ReleaseResultWs2 result = new ReleaseResultWs2();
			result.setRelease(releaseWs2);
			ret.add(result);
		}

		return ret;
	}
}