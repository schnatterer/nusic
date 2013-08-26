package org.musicbrainz.model;

import java.util.*;

//import org.apache.commons.lang3.StringUtils;

import org.musicbrainz.DomainsWs2;
import org.musicbrainz.model.entity.EntityWs2;
//import org.musicbrainz.utils.MbUtils;


/**
 * <p>Represents a relation between two Entities.</p>
 * 
 * <p>There may be an arbitrary number of relations between all first
 * class objects in MusicBrainz. The RelationWs1 itself has multiple
 * attributes, which may or may not be used for a given relation
 * type.</p>
 * 
 * <p>Note that a {@link RelationWs1} object only contains the target but not
 * the source end of the relation.</p>
 * 
 */
public class RelationWs2 extends DomainsWs2{
	
	public RelationWs2() {}
		
	/**
	 * Relation target types
	 */
	public static final String TO_ARTIST = NS_REL_2_PREFIX + "artist";
           public static final String TO_LABEL = NS_REL_2_PREFIX + "label";
           public static final String TO_RECORDING = NS_REL_2_PREFIX + "recording";
	public static final String TO_RELEASE = NS_REL_2_PREFIX + "release";
           public static final String TO_RELEASE_GROUP = NS_REL_2_PREFIX + "release_group";
	public static final String TO_URL = NS_REL_2_PREFIX + "url";
           public static final String TO_WORK = NS_REL_2_PREFIX + "work";
          
	/**
	 * Relation reading directions
	 */
	public static final String DIR_BOTH = "both";
	public static final String DIR_FORWARD = "forward";
	public static final String DIR_BACKWARD = "backward";
           
           /*
         *  Attributes not on/off
         */
            
            public static final String ATTR_DESCRIPTION = "description";
            public static final String ATTR_LICENSE = "license";
            public static final String ATTR_POSITION = "position"; //proposal RFC-315
            
            public static final String ATTR_ORCHESTRA = "orchestra";
            public static final String ATTR_VOCAL = "vocal";
            public static final String ATTR_INSTRUMENT = "instrument";

	/**
	 * The relation's tpye
	 */
	private String type; 
	
	/**
	 * The target's id
	 */
	private String targetId;
	
	/**
	 * The target's type
	 */
	private String targetType;
	
	/**
	 * One of {@link RelationWs1#DIR_FORWARD}, {@link RelationWs1#DIR_BACKWARD} or {@link RelationWs1#DIR_BOTH} 
	 */
	private String direction = DIR_BOTH;
	
	/**
	 * A list of attributes (URIs)
	 */
	private List<String> attributes;

	/**
	 * The begin date
	 */
	private String beginDate;

	/**
	 * The end date
	 */
	private String endDate;
	/**
	 * The target entity
	 */
	private EntityWs2 target;
            /**
	 * The description of the relation, calculatet on
         * the Type and the Attributes.
	 */
	private String description;

           private List<String> attrValList;
           private List<String> attrKeyList;
           private List<String> wordList;

    /**
     * @return the attributes
     */
    public List<String> getAttributes() {
            return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<String> attributes) {
            this.attributes = attributes;
    }
    /**
     * @return the beginDate
     */
    public String getBeginDate() {
            return beginDate;
    }

    /**
     * @param beginDate the beginDate to set
     */
    public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
    }

    /**
     * @return the direction
     */
    public String getDirection() {
            return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(String direction) {
            this.direction = direction;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
            return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
            this.endDate = endDate;
    }
    /**
     * @return the relationType
     */
    public String getType() {
            return type;
    }

    /**
     * Note: This setter will prepend a (default) URI unless
     * the parameter <code>type</code> is not already an URI.
     * 
     * @param type the relationType to set
     */
    public void setType(String type) {
               this.type = type;
               
    }

    /**
     * @return the target
     */
    public EntityWs2 getTarget() {
            return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(EntityWs2 target) {
            this.target = target;
    }

    /**
     * Returns the targetId
     * 
     * @return the targetId
     */
    public String getTargetId() {
            return targetId;
    }

    /**
     * @param targetId the targetId to set
     */
    public void setTargetId(String targetId) {
            this.targetId = targetId;
    }

    /**
     * @return the targetType
     */
    public String getTargetType() {
            return targetType;
    }

    /**
     * @param targetType the targetType to set
     */
    public void setTargetType(String targetType) {
            this.targetType = targetType;
    }
    /* For a wyle, MB returned Type and Attributes
    * not decoded and we tried to build a meaning
    * description form that.
    * 
    * not used anymore.
   
    public String getDescription() {
        if (description == null)
        {
            calcDescription();
            description ="";
        }
        return description;
    }
    public void calcDescription(){
    
           attrValList= new ArrayList<String>();
           attrKeyList= new ArrayList<String>();
           
           description="";
           
           if (getType()!=null)
           {    
                wordList  = getWordList(
                        MbUtils.extractTypeFromURI(getType()));
                
                if (wordList==null)
                {
                    description= "# Attributes/Type mismatch";
                }
                description = StringUtils.join(wordList, " ");
           }
       }
       private List<String> getWordList(String type)
       {
           wordList = new ArrayList<String>();
           attrKeyList = new ArrayList<String>();
           attrValList = new ArrayList<String>();

           if (getAttributes() != null)
           { 
              attrValList.addAll(getAttributes());
           }

           String word="";
           String key="";

           boolean isOpenKey = false;

           int i=0;
           while (i<type.length())
           {
               String c = type.substring(i,i+1);
               
               if (c.equals("_"))
               {
                   c = " ";
               }

               if (c.equals("{"))
               {
                   if (isOpenKey) return null;

                   if (!word.equals("") && !word.equals(" ")&& !word.equals("_"))
                   {
                       word = word.trim();
                       wordList.add(word);
                       word = "";
                       
                   }
                   isOpenKey=true;
               }
               else if (c.equals("}"))
               {
                   if (!isOpenKey) 
                       return null;
                   if (key.equals(""))
                       return null;
                   if (word.equals("")) 
                       return null;

                   closeKey(key);

                   key="";
                   isOpenKey=false;
                   word = "";
               }
               else
               {
                   if (isOpenKey)
                   {
                       key=key+c;
                   }
                   word=word+c;
               }
               i++;
           }
           if (!word.equals("") && !word.equals(" ")&& !word.equals("_"))
           {
               if (isOpenKey) {return null;}
               word = word.trim();
               wordList.add(word);
               word = "";
               
           }
           if (attrValList.isEmpty() && attrKeyList.isEmpty())
           {
               return wordList;
           }
            else if (attrKeyList.size()==1 && attrValList.isEmpty())
           { 
               wordList.remove(attrKeyList.get(0));
               return wordList;
           }
           else if (attrKeyList.size()==1 && attrValList.size()==1)
           {
                String k = attrKeyList.get(0);
                String v = attrValList.get(0);

                word="";

               if (k.contains(":"))
               {
                   String[] res1 = k.split("\\:");

                   String[] res2 = res1[1].split("\\|");
                   if (res2.length == 2)
                   {
                       word = res2[1];
                   }
                   else
                   {
                       word = res2[0];
                   }
               }
               else
               {
                   word= v;
               }
               if (word.contains("%"))
               {
                   word= word.replace("%", v);
               }
               word = word.trim();
               wordList.set(wordList.indexOf(k), word);
               return wordList;
           }
           return null;
       }
       private void closeKey(String key){

           String word="";
           String attrname="";
           String valIfTrue="";
           String valIfFalse="";

           String[] res1 = key.split("\\:");

           attrname = res1[0];
           if (!getOpenAttributes().contains(attrname))
           {
               if (res1.length == 2)
               {
                   String[] res2 = res1[1].split("\\|");
                   valIfTrue = res2[0];

                   if (res2.length == 2)
                   {
                       valIfFalse = res2[1];
                   }
               }

               if (attrValList.contains(attrname))
               {
                   word = valIfTrue;
                   attrValList.remove(attrname);
               }
               else
               {
                   word = valIfFalse;
               }
               if (!word.equals("") && !word.equals(" ")&& !word.equals("_"))
               {
                   word = word.trim();
                   wordList.add(word);
               }
           }
           else
           {
               wordList.add(key);
               attrKeyList.add(key);
           }
       }
       private List<String> getOpenAttributes(){
                 
                List<String> openAttributes = new ArrayList<String>(0);
                
                openAttributes.add(ATTR_ORCHESTRA);
                openAttributes.add(ATTR_VOCAL);
                openAttributes.add(ATTR_INSTRUMENT);
                openAttributes.add(ATTR_DESCRIPTION);
                openAttributes.add(ATTR_LICENSE);
                openAttributes.add(ATTR_POSITION);
                                
                return openAttributes;
        }
       // End of CalcDescription.
       */
}
