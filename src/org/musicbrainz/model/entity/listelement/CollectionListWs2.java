package org.musicbrainz.model.entity.listelement;

import org.musicbrainz.wsxml.element.ListElement;
import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.model.entity.CollectionWs2;


/**
 * A list of Collections
 * 
 */
public class CollectionListWs2 extends ListElement{

    protected List<CollectionWs2> collections = new ArrayList<CollectionWs2>();

	/**
	 * @return the collections
	 */
	public List<CollectionWs2> getCollections() {
		return collections;
	}

	/**
	 * @param collections the collections to set
	 */
	public void setCollection(List<CollectionWs2> collections) {
		this.collections = collections;
	}
	
	/**
	 * Convenience method to adds an collection to the list.
	 * 
	 * This will create a new <code>ArrayList</code> if {@link #collections} is null.
	 * 
	 * @param collection
	 */
	public void addCollection(CollectionWs2 collection) 
	{
		if (collections == null) {
			collections = new ArrayList<CollectionWs2>();
		}
		collections.add(collection);
	}
           public void addAllCollections(List<CollectionWs2> collectionList) 
           {
                if (collections == null) {
                        collections = new ArrayList<CollectionWs2>();
                }

                collections.addAll(collectionList);
           }
}
