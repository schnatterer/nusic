package org.mc2;

/**
 * All application specific exceptions extend this class.
 */

public class MC2Exception extends Exception {

    public MC2Exception() {
		super();
	}

	public MC2Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public MC2Exception(String message) {
		super(message);
	}

	public MC2Exception(Throwable cause) {
		super(cause);
	}

}
