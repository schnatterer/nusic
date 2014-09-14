package org.musicbrainz.query.submission;

import java.util.List;
import java.util.ArrayList;
/**
 * Each Submission could contain requests for 
 * many entities.
 * An EntityElement is the specific entity
 * request description.
 * 
 **/
public class EntityElement {

    /*
     * The entity type. Must be one of 
     * LABEL 
     * ARTIST
     * RELEASEGROUP
     * WORK
     * RECORDING
     * 
     */
    private String entityType;
    /*
     * The entity id
     */
    private String id;
    /*
     * The tag list for the entity.
     * Notes that this wil replace the current tag list,
     * so be sure to include ALL the tags you want apply to this
     * entity.
     */
    private List<String> tagList = new ArrayList<String>();
        /*
     * The new rating for the entity.
     * Notes that the rating to be submitted range from 0 to 100.
     */
    private int userRating = 0;

    /**
     * @return the entityTipe
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityTipe the entityTipe to set
     */
    public void setEntityType(String entityTipe) {
        this.entityType = entityTipe;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the tagList
     */
    public List<String> getTagList() {
        return tagList;
    }

    /**
     * @param tagList the tagList to set
     */
    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
    public void addTag(String tag){
        
        this.tagList.add(tag);
    }

    /**
     * @return the userRating
     */
    public int getUserRating() {
        return userRating;
    }
    /**
     * @param userRating the userRating to set
     */
    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

}
