package org.musicbrainz;

/**
 * All application specific exceptions extend this class.
 */

public class MBWS2Exception extends Exception {

    public MBWS2Exception() {
		super();
	}

	public MBWS2Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public MBWS2Exception(String message) {
		super(message);
	}

	public MBWS2Exception(Throwable cause) {
		super(cause);
	}

}
