package org.musicbrainz.model;

import org.musicbrainz.model.entity.listelement.DiscListWs2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**

 */
public class RatingsWs2 
{
    
    private Log log = LogFactory.getLog(DiscListWs2.class);

    /**
     * the numebr of people who votes
     */
    private Long votesCount=0L;
    /**
     * the average rating
     */
    private Float averageRating=0F;

    /**
     * @return the votesCount
     */
    public Long getVotesCount() {
        return votesCount;
    }

    /**
     * @param votesCount the votesCount to set
     */
    public void setVotesCount(Long votesCount) {
        this.votesCount = votesCount;
    }

    /**
     * @return the averageRating
     */
    public Float getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating the averageRating to set
     */
    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }
    
}
