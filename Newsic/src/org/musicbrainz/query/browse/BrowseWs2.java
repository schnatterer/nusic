package org.musicbrainz.query.browse;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.filter.browsefilter.BrowseFilterWs2;
import org.musicbrainz.includes.IncludesWs2;
import org.musicbrainz.query.QueryWs2;
import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.wsxml.element.ListElement;

/* Implements Browse query in WS2.
 * 
 * Browse requests are a direct lookup of all the entities directly linked 
 * to another entity. (with directly linked I am referring to any 
 * relationship inherent in the database, so no ARs). For example, you may 
 * want to see all releases on netlabel ubiktune: 
 
 * /ws/2/release?label=47e718e1-7ee4-460c-b1cc-1192a841c6e5
 
 * Note that browse requests are not searches, in order to browse all 
 * the releases on the ubiktune label you will need to know the MBID of 
 * ubiktune
 * 
 *  Linked entities
 * 
 * The following list shows which linked entities you can use in a browse 
 * request: 
 * 
 * /ws/2/artist            recording, release, release-group, work
 * /ws/2/label             release
 * /ws/2/recording         artist, release
 * /ws/2/release           artist, label, recording, release-group
 * /ws/2/release-group     artist, release
 * /ws/2/work
 *
 * As a special case, release also allows track_artist, which is intended to 
 * allow you to browse various artist appearances for an artist. It will return
 * any release where the artist appears in the artist_credit for a track, but 
 * NOT in the artist_credit for the entire release (as those would already have
 * been returned in a request with artist=<MBID>). 
 * 
 * Release-groups can be filtered on type, and releases can be filtered on 
 * type and/or status. For example, if you want all the Live Bootleg releases 
 * by Metallica:
 * 
 *  /ws/2/release?artist=65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab&status=bootleg&type=live
 * 
 * Or all albums and EPs by Autechre: 
 * 
 * /ws/2/release-group?artist=410c9baf-5469-44f6-9852-826524b80c61&type=album|ep
 * 
 * Note the | in the filter, operating as a logical or.
 * 
 * Note that filtering is mandatory, if you don't specify type or status the release and release-group resources will not return any results.
 * 
 * Paging
 *
 * Browse requests are the only requests which support paging, any browse request 
 * supports an 'offset=' argument to get more results. Browse requests also 
 * support 'limit=', the default limit is 25, and you can increase that up to 
 * 100.
 * 
 * inc.
 * 
 * Just like with normal lookup requests, the server can be instructed to 
 * include more data about the entity using an 'inc=' argument. Supported values
 * for inc= are: 
 * 
 * /ws/2/artist            aliases
 * /ws/2/label             aliases
 * /ws/2/recording         artist-credits
 * /ws/2/release           artist-credits, labels
 * /ws/2/release-group     artist-credits
 * /ws/2/work              aliases
 * 
 * In addition to the inc= values listed above, all entities (except release)
 * support: 
 * 
 * tags, ratings, user-tags, user-ratings
 * 
 * Release Type and Status
 * 
 * Any query which includes release-groups in the results can be filtered to 
 * only include release groups of a certain type. Any query which includes 
 * releases in the results can be filtered to only include releases of a 
 * certain type and/or status. Valid values are: 
 * 
 * status     official, promotion, bootleg, pseudo-release
 * type       nat, album, single, ep, compilation, soundtrack, spokenword, 
 *            interview, audiobook, live, remix, other
 * 
 * Use Include to set the INC parameters, Filter to filter Status/Type.
*/

public class BrowseWs2 extends QueryWs2 {

    protected BrowseFilterWs2 filter;
    protected IncludesWs2 includes;
    protected ListElement listElement;
    
    protected BrowseWs2(BrowseFilterWs2 filter, IncludesWs2 includes)
    {
            super();
            this.filter=filter;
            this.includes=includes;
    }

    /**
     * Custom WebService Constructor
     *  
     * @param ws An implementation of {@link WebService}
   */
    protected BrowseWs2(WebService ws,BrowseFilterWs2 filter, IncludesWs2 includes)
    {
        super(ws);
        this.filter=filter;
        this.includes=includes;
    }
    public boolean hasMore()
    {
        if (listElement == null) return true;
        
        int count  = listElement.getCount() == null ? 0 : listElement.getCount().intValue();
        int offset =listElement.getOffset() == null ? 0 : listElement.getOffset().intValue();
        int limit =filter.getLimit()  == null ? 0 : filter.getLimit().intValue();
        
        // maybe the real one is lower, if we reached the end
        // but it does'nt matter in this context.
        
        int newOffset = offset+limit; 
        if (count >= newOffset) return true;
        return false;
    }
    
    protected Metadata getMetadata(String entity) throws MBWS2Exception
    {
            return getFromWebService(entity, "", includes, filter);
    } 
   
}
