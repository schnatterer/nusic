package org.musicbrainz.model.searchresult;

import org.musicbrainz.model.entity.EntityWs2;

/**
 * Represents a search result.
 */
public abstract class SearchResultWs2 
{
    /**
     * The score indicates how good this result matches the search
     * parameters. The higher the value, the better the match.
     * The score is a number between 0 and 100.
     */
    private Integer score;
        /**
     * The Entity in the result.
     */
       private EntityWs2 entity;

    /**
     * @return the score
     */
    public Integer getScore() {
            return score;
    }
    /**
     * @param score the score to set
     */
    public void setScore(Integer score) {
            this.score = score;
    }

    /**
     * @return the entity
     */
    public EntityWs2 getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(EntityWs2 entity) {
        this.entity = entity;
    }	
}
