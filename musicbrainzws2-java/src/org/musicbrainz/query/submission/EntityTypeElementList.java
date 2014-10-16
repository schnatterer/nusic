package org.musicbrainz.query.submission;

import java.util.List;
import java.util.ArrayList;
import org.musicbrainz.DomainsWs2;

/**
 * Each Submission could contain requests for 
 * entities by different Entity Types.
 * A EntityTypeElementList is the sub list for
 * a specific entity Type.
 * 
 **/
public class EntityTypeElementList extends DomainsWs2{

    /*
     * The entity (list) type. Must be one of 
     * LABELLIST 
     * ARTISTLIST
     * RELEASEGROUPLIST
     * WORKLIST
     * RECORDINGLIST
     */
    private String entityListType;
    /*
     * The List of Entity Elements.
     * 
     */
    private List<EntityElement> entityElementList = new ArrayList<EntityElement>();

    /**
     * @return the entityListType
     */
    public String getEntityListType() {
        return entityListType;
    }

    /**
     * @param entityListType the entityListType to set
     */
    public void setEntityListType(String entityListType) {
        this.entityListType = entityListType;
    }

    /**
     * @return the entityElementList
     */
    public List<EntityElement> getEntityElementList() {
        return entityElementList;
    }

    /**
     * @param entityElementList the entityElementList to set
     */
    public void setEntityElementList(List<EntityElement> entityElementList) {
        this.entityElementList = entityElementList;
    }
    public void addEntityElement (EntityElement entityElement) {

         if (!checkEntityType(entityElement.getEntityType()));
            // reject
         
         boolean found=false;
         for (EntityElement e: entityElementList)
         {
             if (e.getId().equals(entityElement.getId()))
             {
                 found=true;
                 e.setTagList(entityElement.getTagList());
                 e.setUserRating(entityElement.getUserRating());
                 continue;
             }
         }
         if (!found){
             entityElementList.add(entityElement);
         }
     }
     private boolean checkEntityType(String EntityType){
     
         if(entityListType.equals(LABELLIST) && EntityType.equals(LABEL)) return true;
         if(entityListType.equals(ARTISTLIST) && EntityType.equals(ARTIST)) return true;
         if(entityListType.equals(RELEASEGROUPLIST) && EntityType.equals(RELEASEGROUP)) return true;
         if(entityListType.equals(RECORDINGLIST) && EntityType.equals(RECORDING)) return true;
         if(entityListType.equals(WORKLIST) && EntityType.equals(WORK)) return true;
         return false;
     }
}
