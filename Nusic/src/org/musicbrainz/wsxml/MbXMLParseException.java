package org.musicbrainz.wsxml;


/**
 * This exception occurs if the xml stream could not be parsed
 * (i.e if it is not valid).
 * 
 * @author Patrick Ruhkopf
 */
public class MbXMLParseException extends MbXMLException {

	public MbXMLParseException() {
		super();
	}

	public MbXMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public MbXMLParseException(String message) {
		super(message);
	}

	public MbXMLParseException(Throwable cause) {
		super(cause);
	}
}
