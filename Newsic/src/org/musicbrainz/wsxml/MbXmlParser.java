package org.musicbrainz.wsxml;

import java.io.InputStream;

import org.musicbrainz.wsxml.element.Metadata;

/**
 * A simple parser interface to parse XML responses of the MusicBrainz's
 * web service. The {@link DefaultWebServiceWs2} class uses {@link JDOMParserWs2} as a default
 * {@link MbXmlParser} implementation. However, you can write your own
 * implementation and inject it to the {@link DefaultWebServiceWs2} using 
 * particolar constructor: {@link DefaultWebServiceWs2#DefaultWebServiceWs2(MbXmlParser)}.
 * 
 * @see Metadata
 */
public interface MbXmlParser {
	
	/**
	 * Parses xml from the input stream and returns a populated
	 * {@link Metadata} object.
	 * 
	 * @param inputStream The xml stream
	 * @return A populated {@link Metadata} object.
	 * 
	 * @throws MbXMLException A {@link MbXMLParseException} is thrown 
	 * if the xml could not be parsed. In all other exceptional cases
	 * a general {@link MbXMLException} is thrown. 
	 */
	public Metadata parse(InputStream inputStream) throws MbXMLException;
}
