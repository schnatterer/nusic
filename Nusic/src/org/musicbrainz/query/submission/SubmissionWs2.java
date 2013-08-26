package org.musicbrainz.query.submission;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.DomainsWs2;
import org.musicbrainz.model.RatingsWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.utils.MbUtils;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;

/**
 * A Submission is a list to be submitted
 * to the web service. This, actualy, can be a list
 * of user tags or user ratings.
 * 
 **/
public abstract class SubmissionWs2 extends DomainsWs2{

    private List<EntityTypeElementList> submissionList = new ArrayList<EntityTypeElementList>();
    private SubmissionQueryWs2 query;
    private WebService ws;
    private String submissionType;
    
    public SubmissionWs2(String submissionEntityType){
        this.submissionType=submissionEntityType;
        query = new SubmissionQueryWs2();
    }
     public SubmissionWs2(WebService ws, String submissionEntityType){
        this.ws = ws;
        this.submissionType=submissionEntityType;
        query = new SubmissionQueryWs2(ws);
    }
    
    public Metadata submit() throws WebServiceException, MbXMLException{
        
        Metadata md = new Metadata();
        md.setSubmissionWs2(this);
        
        return query.post(md);
    }
    
    public void addEntity(EntityWs2 entity) throws SubmissionException {
        
        String entityType = MbUtils.extractResTypeFromURI(entity.getIdUri());
        
        if (!entityType.equals(LABEL)&&
             !entityType.equals(ARTIST)&&
             !entityType.equals(RELEASEGROUP)&&
             !entityType.equals(RECORDING)&&
             !entityType.equals(WORK)) 
        
             throw new SubmissionException("Invalid or not allowed EntityType "+entityType);
        
        EntityElement el = setData(entity, entityType);

        String EntityListType = getEntityListType(entityType);

        EntityTypeElementList entityTypeElementList = new EntityTypeElementList();
        entityTypeElementList.setEntityListType(EntityListType);
        entityTypeElementList.addEntityElement(el);

        addEntityTypeElementList(entityTypeElementList);

    }
    /*
     * Set data to be posted.
     * Here sets both tags and rating, specific subclasses only sets
     * appropriate arguments.
     */
    // tobe Overidden
    protected EntityElement setData(EntityWs2 entity, String entityType) {

        EntityElement el = new EntityElement();
        el.setEntityType(entityType);
        el.setId(entity.getId()); 
        for (TagWs2 tag : entity.getUserTags())
        {
            el.addTag(tag.getName());
        }
        RatingsWs2 rating = entity.getUserRating();
        //rating must be postet in percent ratio.
        el.setUserRating(Math.round(rating.getAverageRating()/5*100));
        
        return el;
    }
    private String getEntityListType(String entityType){
    
         if(entityType.equals(LABEL)) return LABELLIST;
         if(entityType.equals(ARTIST)) return ARTISTLIST;
         if(entityType.equals(RELEASEGROUP)) return RELEASEGROUPLIST;
         if(entityType.equals(RECORDING)) return RECORDINGLIST;
         if(entityType.equals(WORK)) return WORKLIST;
         return "";
    }
    /**
     * @param entityTypeElementList the entityTypeElementList to add
     */
    private void addEntityTypeElementList (EntityTypeElementList toAdd) {
         
         boolean found=false;
         for (EntityTypeElementList inList: submissionList)
         {
             if (inList.getEntityListType().equals(toAdd.getEntityListType()))
             {
                 for (EntityElement e : toAdd.getEntityElementList())
                 {
                    inList.addEntityElement(e);
                 }
                 found=true;
                 continue;
             }
         }
         if (!found){
             submissionList.add(toAdd);
         }
    }

    /**
     * @return the submissionList
     */
    public List<EntityTypeElementList> getSubmissionList() {
        return submissionList;
    }

    /**
     * @return the entity
     */
    public String getSubmissionType() {
        return submissionType;
    }
}
