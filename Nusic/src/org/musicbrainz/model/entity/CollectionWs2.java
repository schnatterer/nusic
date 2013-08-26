package org.musicbrainz.model.entity;

import java.util.List;

import org.musicbrainz.model.entity.listelement.ReleaseListWs2;

/**
 * <p>Represents a Collection.</p>
 * 
 */
public class CollectionWs2 extends EntityWs2 {
	
	
    private String name;
    private String editor;

    /**
    * The list of releases from this artist.

    */
    private ReleaseListWs2 releaseList = new ReleaseListWs2();

    /**
    * @return the name
    */
    public String getName() {
    return name;
    }
    /**
    * @param name the name to set
    */
    public void setName(String name) {
    this.name = name;
    }
    /**
    * @return the editor
    */
    public String getEditor() {
    return editor;
    }

    /**
    * @param editor the editor to set
    */
    public void setEditor(String editor) {
    this.editor = editor;
    }
    /**
    * Gets the underlying <code>List</clode> of releases.
    * 
    * @return the releases
    */
    public List<ReleaseWs2> getReleases() {
        return ( releaseList == null ? null : releaseList.getReleases());
    }
    /**
    * Sets the underlying <code>List</clode> of releases.
    * 
    * Note: This will implicitly create a new {@link #releaseList}
    * if it is null.
    * 
    * @param releases the releases to set
    */
    public void setReleases(List<ReleaseWs2> releases) 
    {
        if (releaseList == null) {
                releaseList = new ReleaseListWs2();
        }

        this.releaseList.setReleases(releases);
    }
    /**
    * @return the releaseList
    */
    public ReleaseListWs2 getReleaseList() {
        return releaseList;
    }

    /**
    * @param releaseList the releaseList to set
    */
    public void setReleaseList(ReleaseListWs2 releaseList) {
        this.releaseList = releaseList;
    }
    /**
    * <p>Adds a release to the underlying <code>List</clode>
    * of releases.</p>
    * 
    * <p><em>Note: This will implicitly create a new {@link #releaseList}
    * if it is null.</em></p>
    * 
    * @param release The {@link ReleaseWs1} to add.
    */
    public void addRelease(ReleaseWs2 release) 
    {
        if (releaseList == null) {
                releaseList = new ReleaseListWs2();
        } 
        releaseList.addRelease(release);
    }

    @Override
    public String toString() {
        return getEditor()+" - "+getName();
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CollectionWs2)) {
            return false;
        }
        CollectionWs2 other = (CollectionWs2) object;
        if (this.getIdUri().equals(other.getIdUri()))
        {
            return true;
        }

        return false;
    }
    
}
