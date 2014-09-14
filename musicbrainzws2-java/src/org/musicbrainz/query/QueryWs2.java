package org.musicbrainz.query;


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.musicbrainz.MBWS2Exception;
import org.musicbrainz.filter.FilterWs2;
import org.musicbrainz.includes.IncludesWs2;
import org.musicbrainz.webservice.WebService;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.webservice.impl.HttpClientWebServiceWs2;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.DomainsWs2;

//import com.sun.jmx.mbeanserver.MetaData;

/**
 * <p>A simple interface to the MusicBrainz web service version 2.</p>
 * 
 * <p>This is a facade which provides a simple interface to the MusicBrainz
 * web service version 2. It hides all the details like fetching data from a server,
 * parsing the XML and creating an object tree. Using this class, you can
 * request data by ID or search the collection of all resources
 * (artists, releases, or tracks) to retrieve those matching given
 * criteria. </p>
 *
 */
 public class QueryWs2 extends DomainsWs2{

	
	/**
	 * A logger
	 */    
	protected Log log = LogFactory.getLog(QueryWs2.class);
        /**
	 * The web service implementation to use
	 */
	private WebService ws;	
	
	/**
	 * Default Constructor
	 */
	protected QueryWs2()
	{
                this.ws = new HttpClientWebServiceWs2();
	}
        
	/**
	 * Custom WebService Constructor
	 *  
	 * @param ws An implementation of {@link WebService}
	 */
	protected QueryWs2(WebService ws)
	{
		this.ws = ws;
	}

	/**
	 * Uses the {@link WebService} instance to make a GET request and
	 * returns a {@link MetaData} object.
	 * 
	 * @param entity The entity
	 * @param id The id
	 * @param includes {@link Includes}
	 * @param filter {@link Filter}
	 * @return A {@link MetaData} object.
	 * @throws MBWS2Exception
	 */
        
	protected Metadata getFromWebService(String entity, String id, IncludesWs2 includes, FilterWs2 filter) throws MBWS2Exception
	{
		return getWs().get(entity, id, (includes == null ? null : includes.createIncludeTags()), (filter == null ? null : filter.createParameters()));
	}

            protected Metadata postToWebService (Metadata md) throws WebServiceException, MbXMLException{
                
                return getWs().post(md);
            }
             protected Metadata putToWebService (String entity, String id, List<String> data) throws WebServiceException, MbXMLException{
                
                return getWs().put(entity, id, data);
            }
             protected Metadata deleteFromWebService (String entity, String id, List<String> data) throws WebServiceException, MbXMLException{
                
                return getWs().delete(entity, id, data);
            }
	/**
	 * @return the ws
	 */
	protected WebService getWs() {
		return ws;
	}

	/**
	 * @param ws the ws to set
	 */
	protected void setWs(WebService ws) {
		this.ws = ws;
	}
}
