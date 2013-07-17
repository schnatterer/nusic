package org.musicbrainz.webservice;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.musicbrainz.DomainsWs2;
import org.musicbrainz.query.submission.SubmissionException;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.MbXmlParser;
import org.musicbrainz.wsxml.MbXmlWriter;
import org.musicbrainz.wsxml.element.Metadata;
import org.musicbrainz.wsxml.impl.JDOMParserWs2;
import org.musicbrainz.wsxml.impl.JDOMWriterWs2;

/**
 * A default abstract web service implementation that provides common
 * properties of a web service client that can be extended.
 */
public abstract class DefaultWebServiceWs2 extends DomainsWs2 implements WebService {
	
    private Log log = LogFactory.getLog(DefaultWebServiceWs2.class);

    /**
     * Encoding used to encode url parameters
     */
    protected static final String URL_ENCODING = "UTF-8";
    
    /* Starting on May 16th, 2011 the MusicBrainz Web Service requires all 
     * requests to have a proper User-Agent header that identifies the 
     * application and the version of the application making the request. 
     * Please do no use generic User-Agent strings like 
     * “Java/1.6.0_24″ or “PHP/5.3.4″ -- they do not allow us to identify 
     * the application making the request. On November 16, 2011 we're going 
     * to start blocking requests with generic User-Agent strings 
     * 
     */
    protected static final String USERAGENT
       = "MusicBrainz-Java/2.01beta http://code.google.com/p/musicbrainzws2-java/";

     /**
     * The authentication scheme, could only be DIGEST.
     */
    protected static final String SCHEME = "digest";
    
    /**
     * A string that is used in the web service url 
     * @see DefaultWebServiceWs1#makeUrl(String, String, Map, Map, String, String)
     */
    protected static final String PATHPREFIX = "/ws";

    /**
     * The host, defaults to MAINHOST
     */
    private String host = MAINHOST;

       /**
     * The realm for authentication, defaults to AUTHREALM
     */
    private String realm = AUTHREALM;

    /**
     * The protocol, defaults to http
     */
    private String protocol = "http";

    /**
     * The port, defaults to null
     */
    private Integer port = null; 

    /**
     * XML parser used to consume the response stream
     */
    private MbXmlParser parser;
    /**
     * XML writer used to produce the body of POST requests.
     */
    private MbXmlWriter writer;

      /*All POST requests require authentication. You should authenticate using HTTP Digest,
     * use the same username and password you use to access the main 
     * http://musicbrainz.org website. The realm is "musicbrainz.org".

     POST requests should always include a 'client' parameter in the URL (not the body). 
     * The value of 'client' should be the ID of the client software submitting data. 
     * This has to be the application's name and version number, not that of a client 
     * library (client libraries should use HTTP's User-Agent header). 
     * The recommended format is "application-version", where version does not contain 
     * a - character.
     */
        /**
     * The client credential of your application
     * example.app-0.4.7
     */
   private String client="";

    /**
 * The username for your Musicbrainz account
 */
   private String username;
    /**
 * The password for your Musicbrainz account
 */
   private String password;

 /**
  * Default Constructor that uses {@link JDOMParserWs1}
  */
    public DefaultWebServiceWs2() {
            this.parser = new JDOMParserWs2();
            this.writer = new JDOMWriterWs2();
    }

    /**
     * Constructor to inject a custom parser and writer
     * @param parser XML parser used to consume the response stream
     * @param writer XML used to produce the body of POST requests
     */
    public DefaultWebServiceWs2(MbXmlParser  parser, MbXmlWriter  writer) {
            this.parser = parser;
            this.writer = writer;
    }

    /**
     * Sends a GET request to the specified url
     * 
     * @param url The URL
     * @return A populated {@link Metadata} object
     * @throws WebServiceException
     */
    protected abstract Metadata doGet(String url) throws WebServiceException, MbXMLException;

    /**
     * Sends a POST request to the specified url.
     * 
     * @param url
     * @param data Input stream of the data to post
     * @throws WebServiceException
     */
    protected abstract Metadata doPost(String url, Metadata md) throws WebServiceException, MbXMLException;

       /**
     * Sends a PUT request to the specified url.
     * 
     * @param url
     * @throws WebServiceException
     */
        protected abstract Metadata doPut (String url) throws WebServiceException, MbXMLException;

        /**
     * Sends a DELETE request to the specified url.
     * 
     * @param url
     * @throws WebServiceException
     */
        protected abstract Metadata doDelete (String url) throws WebServiceException, MbXMLException;


        /* (non-Javadoc)
     * @see org.musicbrainz.webservice.WebService#get(java.lang.String, java.lang.String, java.util.List, java.util.Map)
     */
    public Metadata get(String entity, String id, List<String> includeParams, Map<String, String> filterParams) throws WebServiceException, MbXMLException
    {
            String url = this.makeURL(entity, id, includeParams, filterParams);

            //log.debug("GET " + url);

            return doGet(url);
    }

    /* (non-Javadoc)
     * @see org.musicbrainz.webservice.WebService#post(java.lang.String, java.lang.String, java.io.InputStream)
     */
    public Metadata post(Metadata metadata) throws WebServiceException, MbXMLException 
    {
            if (metadata == null || metadata.getSubmissionWs2() == null) 
                throw new SubmissionException ("Empty Submission not allowed");
            
            String url = this.makeURLforPost(metadata.getSubmissionWs2().getSubmissionType(),getClient());

            //log.debug("POST " + url);

            return doPost(url, metadata);
    }
        /* (non-Javadoc)
     * @see org.musicbrainz.webservice.WebService#put(java.lang.String, java.lang.String, List<String>)
     */
        public Metadata put (String entity, String id, List<String> data) throws WebServiceException, MbXMLException{

                return doPut(buildRequest(entity,id,data));
        };
        /* (non-Javadoc)
     * @see org.musicbrainz.webservice.WebService#delete(java.lang.String, java.lang.String, List<String>)
     */
        public Metadata delete (String entity, String id, List<String> data) throws WebServiceException, MbXMLException{

                return doDelete(buildRequest(entity,id,data));
        }
        /**
     * Constructs a URL that can be used to query the web service. The url is made
     * up of the protocol, host, port, version, type, path and parameters.
     * 
     * @param entity The entity (i.e. type, e.g. 'artist') the request is targeting
     * @param id The id of the entity 
     * @param includeParams A list containing values for the 'inc' parameter (can be null)
     * @param filterParams Additional parameters depending on the entity (can be null)
     * 
     * @return An URL as String
     */
    protected String makeURLforPost(String submissionType,String client)
    {
            StringBuffer url = new StringBuffer();

            // append protocol, host and port
            url.append(this.protocol).append("://").append(this.getHost());
            if (this.port != null) url.append(":").append(this.port);

            // append path
            url.append(PATHPREFIX).append("/")
                    .append(WS_VERSION).append("/")
                             .append(submissionType).append("?client=")
                             .append(client);

            return url.toString();
    }
        private String  buildRequest(String entity, String id, List<String> data){

            String url = this.makeURL(entity, id, null, null);
            StringBuilder buf=new StringBuilder();

            buf.append(url);
            buf.append("/");
            boolean begin=true;
            for (String s : data)
            {
               if (!begin) buf.append(";");
               begin = false;
               buf.append(s);
            }
            buf.append("?client=").append(getClient());
            return buf.toString(); 
        }
    /**
     * Constructs a URL that can be used to query the web service. The url is made
     * up of the protocol, host, port, version, type, path and parameters.
     * 
     * @param entity The entity (i.e. type, e.g. 'artist') the request is targeting
     * @param id The id of the entity 
     * @param includeParams A list containing values for the 'inc' parameter (can be null)
     * @param filterParams Additional parameters depending on the entity (can be null)
     * 
     * @return An URL as String
     */
    protected String makeURL(String entity, String id, List<String> includeParams, Map<String, String> filterParams)
    {
            StringBuilder url = new StringBuilder();
            Map<String, String> urlParams = new HashMap<String, String>();

            // Type is not requested/allowed anymore in ws2.
            //urlParams.put("type", this.type);

                 // append filter params

            if (filterParams != null) urlParams.putAll(filterParams);		

            // append protocol, host and port
            url.append(this.protocol).append("://").append(this.getHost());
            if (this.port != null) url.append(":").append(this.port);

            // append path
            url.append(PATHPREFIX).append("/")
                    .append(WS_VERSION).append("/")
                    .append(entity).append("/")
                    .append(id);

                  // Handle COLLECTION sintax exception.

                  if (entity.equals(COLLECTION) && !id.isEmpty())
                  {
                      url.append("/"+RELEASES_INC);
                  }

            // build space separated include param
            if (includeParams != null) {
                    urlParams.put("inc", StringUtils.join(includeParams, "+"));
            }

            // append params
            url.append("?");
            Iterator<Entry<String, String>> it = urlParams.entrySet().iterator();
            while (it.hasNext()) {
                    Entry<String, String> e = it.next();
                    try {
                            url.append(e.getKey()).append("=").append(URLEncoder.encode(e.getValue(), URL_ENCODING)).append("&");
                    } catch (UnsupportedEncodingException ex) {
                            log.error("Internal Error: Could not encode url parameter " + e.getKey(), ex);
                    }
            }

            return url.substring(0, url.length()-1);
    }

    /**
     * @return the parser
     */
    public MbXmlParser getParser() {
            return parser;
    }

    /**
     * @param parser the parser to set
     */
    public void setParser(MbXmlParser parser) {
            this.parser = parser;
    }

        /**
         * @return the host
         */
        public String getHost() {
            return host;
        }

        /**
         * @param host the host to set
         */
        public void setHost(String host) {
            this.host = host;
        }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }
    /**
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return the writer
     */
    public MbXmlWriter getWriter() {
        return writer;
    }

    /**
     * @param writer the writer to set
     */
    public void setWriter(MbXmlWriter writer) {
        this.writer = writer;
    }
        
}
