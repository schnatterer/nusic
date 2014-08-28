package org.musicbrainz.webservice;

/**
 * Server returned invalid data.
 */
public class ResponseException extends WebServiceException {

	/**
	 * 
	 */
	public ResponseException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResponseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ResponseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ResponseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
