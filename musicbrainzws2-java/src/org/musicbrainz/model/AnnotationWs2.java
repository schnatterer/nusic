package org.musicbrainz.model;

import org.musicbrainz.model.entity.EntityWs2;

 /*
 * <p>Annotation Text for any Entity </p>
 * 
 */
public class AnnotationWs2 extends EntityWs2 
{

    private String type;
    private String name;
    private String entity;
    private String text;
    
    public AnnotationWs2() {

    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String title) {
        this.name = title;
    }
  
    public String getEntity() {
        return entity;
    }
    public void setEntity(String entity) {
        this.entity = entity;
    }
  
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "annotation mbid: "+getEntity();
    }
	
}
