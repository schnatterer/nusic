package org.musicbrainz.model.entity.listelement;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.WorkWs2;
import org.musicbrainz.wsxml.element.ListElement;


/**
 * A list of Works
 */
public class WorkListWs2 extends ListElement{

	private List<WorkWs2> works = new ArrayList<WorkWs2>();

	/**
	 * @return the works
	 */
	public List<WorkWs2> getWorks() {
		return works;
	}

	/**
	 * @param works the works to set
	 */
	public void setWorks(List<WorkWs2> works) {
		this.works = works;
	}

	public void addWorks(WorkWs2 work) 
	{
		if (works == null) {
			works = new ArrayList<WorkWs2>();
		}
		
		works.add(work);
	}
           public void addAllWorks(List<WorkWs2> WorkList) 
	{
                if (works == null) {
                        works = new ArrayList<WorkWs2>();
                }

                works.addAll(WorkList);
	}
}
