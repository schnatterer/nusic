package org.musicbrainz.model.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.musicbrainz.DomainsWs2;
import org.musicbrainz.model.AliasWs2;
import org.musicbrainz.model.RatingsWs2;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.model.TagWs2;
import org.musicbrainz.model.RelationListWs2;
import org.musicbrainz.model.searchresult.AnnotationResultWs2;
import org.musicbrainz.query.search.readysearches.AnnotationSearchbyEntityId;
import org.musicbrainz.utils.MbUtils;
import org.musicbrainz.webservice.WebService;

/**
 * <p>A first-level MusicBrainz class.</p>
 * 
 * <p>All entities in MusicBrainz have unique IDs (which are absolute URIs)
 * and may have any number of {@link RelationWs2}s to other entities.
 * This class is abstract and should not be instantiated.</p>
 * 
 * <p>Relations are differentiated by their target type, that means,
 * where they link to. MusicBrainz currently supports four target types
 * (artists, releases, tracks, and URLs) each identified using a URI.
 * To get all relations with a specific target type, you can use
 * {@link EntityWs1#getRelations(String, String, List, String)} and pass
 * one of the following constants as the parameter:</p>
 * <ul>
 *   <li>{@link RelationWs2#TO_ARTIST}</li>
 *   <li>{@link RelationWs2#TO_LABEL}</li>
 *   <li>{@link RelationWs2#TO_RELEASE_GROUP}</li>
 *   <li>{@link RelationWs2#TO_RELEASE}</li>
 *   <li>{@link RelationWs2#TO_RECORDING}</li>
 *   <li>{@link RelationWs2#TO_WORK}</li>
 *   <li>{@link RelationWs2#TO_URL}</li>
 * </ul>
 * 
 * @see RelationWs2
 */

public abstract class EntityWs2 extends DomainsWs2 {

    /**
    * The ID of the special "Various Artists" artist.
    */
    public static final String VARIOUS_ARTISTS_ID = "http://musicbrainz.org/artist/89ad4ac3-39f7-470e-963a-56509c546377";
    
    /**  
    * The data quality definition in MB.
    */
    public static final String QUALITY_LOW = "low";
    public static final String QUALITY_NORMAL = "normal";
    public static final String QUALITY_HIGH = "high";
    /**
     * The entity's idUri
     */
    private String idUri;

        /**
     * The list of aliases for this entitty (Artists, Label and Works
     * at present time).
     */
    private List<AliasWs2> aliases = new ArrayList<AliasWs2>();

    /**
     * The entity's relations
     */
    private RelationListWs2 relationList = new RelationListWs2();

    private String annotation=null;

    /**
 * The list of tags for this entity (not for releases
 * at present time).
 */
    private List<TagWs2> tags = new ArrayList<TagWs2>();

   /**   
 * The rating people give to this entity (not for releases
 * at present time).
 */
   private RatingsWs2 rating;
/**
 * The list of tags postet by the user for this entity (not for releases
 * at present time). Must be logged in to ask for.
 */
    private List<TagWs2> userTags = new ArrayList<TagWs2>();

   /**   
 * The rating the user give to this entity (not for releases
 * at present time). Must be logged in to ask for.
 */
   private RatingsWs2 userRating;
    /**
     * @return the idUri
     */
    public String getIdUri() {
            return idUri;
    }
       public String getId() {

           String resType = MbUtils.extractResTypeFromURI(getIdUri());
           String idStr = MbUtils.extractUuid(getIdUri(), resType);
           return idStr;
    }
    /**
     * @param idUri the idUri to set
     */
    public void setIdUri(String id) {
            this.idUri = id;
    }	
    /**
     * @return the aliases
     */
    public List<AliasWs2> getAliases() {
            return aliases;
    }
    /**
     * @param aliases the aliases to set
     */
    public void setAliases(List<AliasWs2> aliases) {
            this.aliases = aliases;
    }
        public void addAlias(AliasWs2 alias) {
            aliases.add(alias);
    }
    /**
 * @return the tags
 */
    public List<TagWs2> getTags() {
            return tags;
    }
    /**
     * @param tags the tags to set
     */
    public void setTags(List<TagWs2> tags) {
            this.tags = tags;
    }

    public void addTag (TagWs2 tag) {
            tags.add(tag);
    }
     /**
 * @return the userTags
 */
    public List<TagWs2> getUserTags() {
            return userTags;
    }
    /**
     * @param tags the tags to set
  */
    public void setUserTags(List<TagWs2> userTags) {
            this.userTags = userTags;
    }

    public void addUserTag (TagWs2 userTag) {
            userTags.add(userTag);
    }
    /**
     * @return the relations
     */
    public RelationListWs2 getRelationList() {
            return relationList;
    }

    /**
     * @param relationList the relation list to set
     */
    public void setRelationList(RelationListWs2 relationList) {
            this.relationList = relationList;
    }

    /**
     * Returns a list of relations.
     * 
     * You may use the <code>relationType</code> parameter to further restrict
     * the selection. If it is set, only relations with the given relation
     * type are returned. The <code>requiredAttributes</code> sequence
     * lists attributes that have to be part of all returned relations.
     * 
     * If <code>direction</code> is set, only relations with the given reading
     * direction are returned. You can use the {@link RelationWs1#DIR_FORWARD},
     * {@link RelationWs1#DIR_BACKWARD}, and {@link RelationWs1#DIR_BOTH} constants
     * for this.
     * 
     * @param targetType The target relation type (can be null)
     * @param relationType The relation type (can be null)
     * @param requiredAttributes Attributes that are required for every relation (can be null)
     * @param direction The direction of the relation (can be null)
     * @return A list of relations
     */
    public List<RelationWs2> getRelations(String targetType, String relationType, List<String> requiredAttributes, String direction)
    {
            List<RelationWs2> allRelations = new ArrayList<RelationWs2>();
            if (relationList == null)
                    return allRelations;

            for (RelationWs2 relation : relationList.getRelations())
            {
                    // filter target type
                    if (targetType != null) {
                            if (!targetType.equals(relation.getTargetType())) {
                                    continue;
                            }
                    }

                    // filter directions
                    if (direction != null) {
                            if (!direction.equals(relation.getDirection())) {
                                    continue;
                            }
                    }

                    // filter relation type
                    if (relationType != null) {
                            if (!relationType.equals(relation.getType())) {
                                    continue;
                            }
                    }

                    // filter attributes
                    if (requiredAttributes != null) {
                            for (String attr : requiredAttributes) {
                                    if (!relation.getAttributes().contains(attr)) {
                                            continue;
                                    }
                            }
                    }

                    // if no filter applied, add to result list
                    allRelations.add(relation);
            }

            return allRelations;
    }
    public String getAnnotation(WebService ws){

        if (annotation != null) return annotation;

        List<String> annotations = new ArrayList<String>(0);

        AnnotationSearchbyEntityId q;

        if (ws == null) q = new AnnotationSearchbyEntityId(getId());
        else  q = new AnnotationSearchbyEntityId(ws, getId());

        List <AnnotationResultWs2> annRes = q.getFullList();

        for (Iterator <AnnotationResultWs2> i = annRes.iterator(); i.hasNext();)
        {
            if (!annotations.isEmpty())
            {
                annotations.add("\n"+"---"+"\n");
            }
            AnnotationResultWs2 r = i.next();

            if (r.getAnnotation().getText()!=null && !r.getAnnotation().getText().isEmpty())
                annotations.add(r.getAnnotation().getText()+"\n");

        }
        annotation = Arrays.toString(annotations.toArray()).trim();
        annotation = annotation.substring(1);
        annotation = annotation.substring(0, annotation.length()-1).trim();

        return annotation;
    }

    /**
     * @return the rating
     */
    public RatingsWs2 getRating() {
        if (rating == null) rating= new RatingsWs2();
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(RatingsWs2 rating) {
        this.rating = rating;
    }
    
    /**
     * @return the userRating
     */
    public RatingsWs2 getUserRating() {
        if (userRating == null) userRating= new RatingsWs2();
        return userRating;
    }

    /**
     * @param userRating the userRating to set
     */
    public void setUserRating(RatingsWs2 userRating) {
        this.userRating = userRating;
    }
    
}
