package org.musicbrainz.query.submission;

import org.musicbrainz.model.RatingsWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.model.entity.EntityWs2;
import org.musicbrainz.webservice.WebService;

/**
 * List of Tags to be submitted to the Web Service.
 * 
 **/
public class UserTagSubmissionWs2 extends SubmissionWs2{

   
    public UserTagSubmissionWs2(){
        super(TAG_POST);  
    }
    public UserTagSubmissionWs2(WebService ws){
        super(ws, TAG_POST);
    }
    
    @Override
    protected EntityElement setData(EntityWs2 entity, String entityType) {

        EntityElement el = new EntityElement();
        el.setEntityType(entityType);
        el.setId(entity.getId()); 
        
        for (TagWs2 tag : entity.getUserTags())
        {
            el.addTag(tag.getName());
        }
        return el;
    }
   
}
