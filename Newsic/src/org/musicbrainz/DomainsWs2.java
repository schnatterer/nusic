package org.musicbrainz;

import java.util.regex.Pattern;
import org.jdom.Namespace;
import org.musicbrainz.model.RelationWs2;
import org.musicbrainz.model.entity.EntityWs2;


/**
 * <p>The domains for MusicBrainz XML Web Service/Version 2 .</p>
 * 
 */
public abstract class DomainsWs2 {

   // Connection parameters, used by DefaultWebServiceWS2.
    
    /**
    * A string containing the web service version to use
    */
    public static final String WS_VERSION = "2"; 

    public static final String MAINHOST = "musicbrainz.org";
    public static final String ANNOTATIONHOST = "search.musicbrainz.org";
   	
    public static final String AUTHREALM = "musicbrainz.org";

    /**
    * Default namespace prefix for all MusicBrainz metadata.
    */ 
    public static final String NS_MMD_2_PREFIX = "http://musicbrainz.org/ns/mmd-2.0#";
    /**
    * Namespace prefix for relations.
    */
    public static final String NS_REL_2_PREFIX= "http://musicbrainz.org/ns/rel-2.0#";
    /**
    * Namespace prefix for MusicBrainz extensions.
    */
    public static final String NS_EXT_2_PREFIX = "http://musicbrainz.org/ns/ext#-2.0";
   
    public static final Namespace NS_MMD_2 = Namespace.getNamespace(NS_MMD_2_PREFIX);
    public static final Namespace NS_EXT_2 = Namespace.getNamespace(EntityWs2.NS_EXT_2_PREFIX);

    
    // Strings in the XML, used in parse and write.
    public static final String METADATA = "metadata";
    
    public static final String SCORE = "score";
    public static final String COUNT = "count";
    public static final String OFFSET = "offset";
    
    public static final String ENTITY = "entity";
    public static final String TARGETTYPE = "target-type";

    public static final String LABEL = "label";
    public static final String ARTIST = "artist";
    public static final String RELEASEGROUP = "release-group";
    public static final String RELEASE = "release";
    public static final String WORK = "work";
    public static final String RECORDING = "recording";
    public static final String COLLECTION = "collection";
    public static final String DISC = "disc";
    public static final String ANNOTATION = "annotation";
    public static final String RELATION = "relation";
    
    public static final String MESSAGE = "message";

    public static final String ARTISTCREDIT = "artist-credit";
    public static final String NAMECREDIT = "name-credit";
    public static final String LABELINFO = "label-info";
    public static final String ALIAS = "alias";
    public static final String DISCID = "discid";
    public static final String ISRC = "isrc";
    public static final String PUID = "puid";
    public static final String ISWC = "iswc";
    public static final String MEDIUM = "medium";
    public static final String TRACK = "track";
    public static final String TEXTREPRESENTATION = "text-representation";
    public static final String LIFESPAN = "life-span";
    public static final String TAG = "tag";
    public static final String RATING = "rating";
    public static final String USERRATING = "user-rating";
    public static final String USERTAG = "user-tag";
    
    public static final String LABELLIST = "label-list";
    public static final String ARTISTLIST = "artist-list";
    public static final String RELEASEGROUPLIST = "release-group-list";
    public static final String RELEASELIST = "release-list";
    public static final String WORKLIST = "work-list";
    public static final String RECORDINGLIST = "recording-list";
    public static final String ANNOTATIONLIST = "annotation-list";
    public static final String COLLECTIONLIST = "collection-list";
    public static final String RELATIONLIST = "relation-list";
    
    public static final String ALIASLIST = "alias-list";
    public static final String LABELINFOLIST = "label-info-list";
    public static final String ISRCLIST = "isrc-list";
    public static final String PUIDLIST = "puid-list";
    public static final String DISCLIST = "disc-list";
    public static final String TRACKLIST = "track-list";
    public static final String MEDIUMLIST = "medium-list";
    public static final String ATTRIBUTELIST = "attribute-list";
    public static final String TAGLIST = "tag-list";
    public static final String USERTAGLIST = "user-tag-list";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SORTNAME = "sort-name";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String DISAMBIGUATION = "disambiguation";
    
    public static final String ASIN = "asin";
    public static final String BARCODE = "barcode";
    public static final String CATALOGNUMBER = "catalog-number";
    public static final String COUNTRY = "country";
    public static final String DATE = "date";
    public static final String FIRSTRELEASEDATE = "first-release-date";
    public static final String FORMAT = "format";
    public static final String GENDER = "gender";
    public static final String IPI = "ipi";
    public static final String JOINPHRASE = "joinphrase";
    public static final String LABELCODE = "label-code";
    public static final String LENGTH = "length";
    public static final String PACKAGING = "packaging";
    public static final String POSITION = "position";
    public static final String QUALITY = "quality";
    public static final String STATUS = "status";
    public static final String SECTORS = "sectors";
    public static final String TRACKCOUNT = "track-count";
    
    public static final String TARGET = "target";
    public static final String DIRECTION = "direction";
    public static final String BEGIN = "begin";
    public static final String END = "end";
    
    public static final String LANGUAGE = "language";
    public static final String SCRIPT = "script";
    public static final String LOCALE = "locale";
    
    public static final String TEXT = "text";
    public static final String EDITOR = "editor";
    public static final String VOTESCOUNT = "votes-count";

    protected static final Pattern localePattern = Pattern.compile("^[a-z]{2}$");
    protected static final Pattern scriptPattern = Pattern.compile("^[A-Z][a-z]{3}$");
    protected static final Pattern languagePattern = Pattern.compile("^[A-Z]{3}$");
    protected static final Pattern countryPattern = Pattern.compile("^[A-Z]{2}$");
    protected static final Pattern ipiPattern = Pattern.compile("^[0-9]{11}$");
    protected static final Pattern directionPattern = Pattern.compile("^\\s*(" + RelationWs2.DIR_BOTH + "|" + RelationWs2.DIR_FORWARD + "|" + RelationWs2.DIR_BACKWARD + ")\\s*$");
    
    // Valid Filter strings for queries:
    
    public static final String LIMIT_FILTER = "limit";
    public static final String OFFSET_FILTER = "offset";
    public static final String QUERY_FILTER = "query";
    
    // Valid Inc parameters strings for queries:
    
    public static final String ARTISTCREDITS_INC = "artist-credits";
    public static final String ARTISTRELS_INC = "artist-rels";
    public static final String LABELRELS_INC = "label-rels";
    public static final String RATINGS_INC= "ratings";
    public static final String RECORDINGLEVELRELS_INC = "recording-level-rels";
    public static final String RECORDINGRELS_INC = "recording-rels";
    public static final String RELEASEGROUPRELS_INC = "release-group-rels";
    public static final String RELEASERELS_INC= "release-rels";
    public static final String TAGS_INC = "tags";
    public static final String URLRELS_INC = "url-rels";
    public static final String USERRATINGS_INC = "user-ratings";
    public static final String USERTAGS_INC = "user-tags";
    public static final String WORKLEVELRELS_INC = "work-level-rels";
    public static final String WORKRELS_INC = "work-rels";

    public static final String ALIASES_INC = "aliases";
    public static final String DISCIDS_INC= "discids";
    public static final String ISRCS_INC = "isrcs";
    public static final String LABELS_INC = "labels";
    public static final String MEDIA_INC= "media";
    public static final String PUIDS_INC = "puids";
    public static final String RECORDINGS_INC= "recordings";
    public static final String RELEASEGROUPS_INC= "release-groups";
    public static final String RELEASES_INC = "releases";
    public static final String VARIOUSARTISTS_INC = "various-artists";
    public static final String WORKS_INC = "works";
   
    // Valide related Entities for Browses
    
    // Maybe we should rename the parameter and modify controllers
    // according to new names. i.e. REL_LABEL...
    
    //public static final String LABEL = "label";
    //public static final String ARTIST = "artist";
    //public static final String RELEASEGROUP = "release-group";
    //public static final String RELEASE = "release";
    //public static final String RECORDING = "recording";
    //public static final String COLLECTION = "collection";
    // WORK and ANNOTATION are  not valid.
    // Special case for release.
    public static final String TRACKARTIST = "track_artist";
    
    // Valid POST request entities
    
    public static final String TAG_POST = "tag";
    public static final String RATING_POST = "rating";

}
