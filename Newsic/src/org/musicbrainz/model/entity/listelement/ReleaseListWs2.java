package org.musicbrainz.model.entity.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.ReleaseWs2;
import org.musicbrainz.wsxml.element.ListElement;


/**
 * A list of Releases
 */
public class ReleaseListWs2 extends ListElement{

	private List<ReleaseWs2> releases = new ArrayList<ReleaseWs2>();

	/**
	 * @return the releases
	 */
	public List<ReleaseWs2> getReleases() {
		return releases;
	}

	/**
	 * @param releases the releases to set
	 */
	public void setReleases(List<ReleaseWs2> releases) {
		this.releases = releases;
	}
	
	/**
	 * Adds a release to the list.
	 * 
	 * It will also create and set new ArrayList if
	 * {@link #releases} is null.
	 * 
	 * @param release The release to add
	 */
	public void addRelease(ReleaseWs2 release) 
	{
		if (releases == null) {
			releases = new ArrayList<ReleaseWs2>();
		}
		
		releases.add(release);
	}
           public void addAllReleases(List<ReleaseWs2> releaseList) 
	{
                if (releases == null) {
                        releases = new ArrayList<ReleaseWs2>();
                }

                releases.addAll(releaseList);
	}
}
