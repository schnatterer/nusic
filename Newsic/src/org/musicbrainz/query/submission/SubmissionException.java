package org.musicbrainz.query.submission;

import org.musicbrainz.wsxml.MbXMLException;


/**
 * A submission exception indicates that the web service
 * submission (post) request was invalid (e.g. invalir entity type).
 */
public class SubmissionException extends MbXMLException {

	public SubmissionException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SubmissionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SubmissionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SubmissionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
