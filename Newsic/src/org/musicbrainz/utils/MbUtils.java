package org.musicbrainz.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains helper functions to make common tasks easier.
 * 
 * @author Patrick Ruhkopf
 */
public class MbUtils {
	
	/**
	 * Converts a space separated list of relative types to
	 * absolute URIs, see {@link MbUtils#convertTypeToURI(String, String)}  
	 * 
	 * @param typeStr A space separated lsit of types
	 * @param namespace The namespace of the URI
	 * @return An absolute URI string
	 */
	public static String convertTypesToURI(String typeStr, String namespace) 
	{
		if (typeStr == null) {
			return null;
		}
		
		String[] types = typeStr.split(" ");
		if ((types == null) || (types.length < 1)) {
			return  MbUtils.convertTypeToURI(typeStr, namespace);
		}
		else {
			StringBuffer ret = new StringBuffer();
			for (String t : types) {
				ret.append(MbUtils.convertTypeToURI(t, namespace)).append(" ");
			}
			return ret.substring(0, ret.length() -1);
		}
	}
	
	/**
	 * This method returns an absolute URI using the given type
	 * and namespace. If the type is already an absolute URI, it
	 * is returned unchanged.
	 * 
	 * @param type The type
	 * @param namespace The namespace
	 *  
	 * @return An absolute URI
	 */
	public static String convertTypeToURI(String type, String namespace)
	{
		if (type == null || type.equals(""))
			return null;
		
		URI absolute;
		try {
			absolute = new URI(type);
		} catch (URISyntaxException e) {
			return namespace + type;
		}
		if (absolute.getScheme() == null) {
			return namespace + type;
		}
		
		// may be already an absolute URI
		return type;
	}
	
	/**
	 * Makes an absolute URI from the relative id. If the given
	 * id is already an URI, it will be returned unchanged.
	 * 
	 * @param id A relative id
	 * @param resType For example <em>track</em> (without namespace!)
	 * 
	 * @return An absolute URI
	 */
	public static String convertIdToURI(String id, String resType)
	{
		URI absolute;
		try {
			absolute = new URI(id);
		} catch (URISyntaxException e) {
			return "http://musicbrainz.org/" + resType.toLowerCase() + "/" + id;
		}
		if (absolute.getScheme() == null) {
			return "http://musicbrainz.org/" + resType.toLowerCase() + "/" + id;
		}
		
		// may be already an absolute URI
		return id;
	}
	
	
	/**
	 * Extract the UUID part from a MusicBrainz identifier.
	 * 
	 * This function takes a MusicBrainz ID (an absolute URI) as the input
	 * and returns the UUID part of the URI, thus turning it into a relative
	 * URI. If <code>uriStr</code> is null or a relative URI, then it is
	 * returned unchanged.
	 * 
	 * The <code>resType</code> parameter can be used for error checking.
	 * Set it to 'artist', 'release', or 'track' to make sure 
	 * <code>uriStr</code> is a syntactically valid MusicBrainz identifier
	 * of the given resource type. If it isn't, an 
	 * <code>IllegalArgumentException</code> exception is raised. This error
	 * checking only works if <code>uriStr</code> is an absolute URI, of course.
	 * 
	 * Example:
	 * >>>  MBUtils.extractUuid('http://musicbrainz.org/artist/c0b2500e-0cef-4130-869d-732b23ed9df5', 'artist')
	 * 'c0b2500e-0cef-4130-869d-732b23ed9df5'
	 * 
	 * @param uriStr A string containing a MusicBrainz ID (an URI), or null
	 * @param resType A string containing a resource type
	 * @return A String containing a relative URI or null
	 * @throws URISyntaxException 
	 */
	public static String extractUuid(String uriStr, String resType)
	{
		if (uriStr == null) {
			return null;
		}
		
		URI uri;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			return uriStr; // not really a valid URI, probably the UUID
		}
		if (uri.getScheme() == null) {
			return uriStr; // not really a valid URI, probably the UUID
		}
		
		if (!"http".equals(uri.getScheme()) || !"musicbrainz.org".equals(uri.getHost())) {
			throw new IllegalArgumentException(uri.toString() + " is no MB ID");
		}
		
        String regex = "^/(label|artist|release-group|release|recording|work|collection)/([^/]*)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(uri.getPath());
        if(m.matches()) 
        {
        	if (resType == null) {
        		return m.group(2);
        	}
        	else {
        		if(resType.equals(m.group(1))) {
        			return m.group(2);
        		}
        		else {
        			throw new IllegalArgumentException("expected '" + resType + "' Id");
        		}
        	}
        }
		else {
			throw new IllegalArgumentException("'" + uriStr + " is no valid MB id");
		}
	}
	
	/**
	 * This method will extract a type from an URI. 
	 * 
	 * This is a helper like extractFragment (see python sources) that
	 * works just a little different but should suit our needs.
	 * 
	 * @param typeUri The URI containing the type (or the type itself)
	 * @return A type (extracted from the URI or left unchanged)
	 */
	public static String extractTypeFromURI (String typeUri)
	{
		String[] f = typeUri.split("#");
		if (f == null || f.length < 2)
			return typeUri;
		else
			return f[1];
	}
        /**
	 * This method will extract a resType from an URI. 
	 * 
	
	 * @param idUri The URI containing the Id
	 * @return A resType (extracted from the URI or left unchanged)
	 */
	public static String extractResTypeFromURI (String idUri)
	{
		String s = idUri.replace("http://musicbrainz.org/", "");
                
                      String[] f = s.split("/");
		if (f == null || f.length < 2)
			return idUri;
		else
			return f[0];
	}
}
