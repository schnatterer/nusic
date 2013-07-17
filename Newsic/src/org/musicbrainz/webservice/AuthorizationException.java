package org.musicbrainz.webservice;

/**
 * An security exception that is thrown when a user is
 * not authorized to access a particular resource.
 */
public class AuthorizationException extends WebServiceException {

	/**
	 * Default constructor
	 */
	public AuthorizationException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AuthorizationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AuthorizationException(Throwable cause) {
		super(cause);
	}

}
