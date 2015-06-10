/* Copyright (C) 2015 Johannes Schnatterer
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *  
 * This file is part of nusic.
 * 
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.core.impl;

import info.schnatterer.nusic.core.ServiceException;
import info.schnatterer.nusic.core.i18n.CoreMessageKey;

import java.util.Locale;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for all exceptions thrown by services. Adds
 * internationalization features, facilitating the message handling for display
 * messages.
 * 
 * @author schnatterer
 * 
 */
public class AndroidServiceException extends ServiceException {
	private static final Logger LOG = LoggerFactory
			.getLogger(AndroidServiceException.class);
	static final long serialVersionUID = 1L;

	public static final Locale DEFAULT_LOCALE = Locale.ROOT;

	private CoreMessageKey messageKey;
	private Object[] args = null;

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the (technical) detail message. The detail message is saved
	 *            for later retrieval by the {@link #getMessage()} method.
	 * @param messageKey
	 *            the id to a localized message intended to be displayed to the
	 *            user. Passing and invalid id might result in
	 *            <code>message</code> being displayed to the user.
	 */
	public AndroidServiceException(String message, CoreMessageKey messageKey) {
		super(message);
		this.messageKey = messageKey;
	}

	/**
	 * Convenience method for
	 * {@link AndroidServiceException#AndroidServiceException(String, CoreMessageKey)}
	 * , where the technical exception message is put out in the default locale.
	 * 
	 * @param messageKey
	 * @param t
	 */
	public AndroidServiceException(CoreMessageKey messageKey) {
		this(getStringInDefaultLocale(messageKey), messageKey);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the (technical) detail message (which is saved for later
	 *            retrieval by the {@link #getMessage()} method).
	 * @param CoreMessageKey
	 *            the id to a localized message intended to be displayed to the
	 *            user. Passing and invalid id might result in
	 *            <code>message</code> being displayed to the user.
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public AndroidServiceException(String message, CoreMessageKey messageKey,
			Throwable cause) {
		super(message, cause);
		this.messageKey = messageKey;
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the (technical) detail message (which is saved for later
	 *            retrieval by the {@link #getMessage()} method).
	 * @param messageKey
	 *            the id to a localized message intended to be displayed to the
	 *            user. Passing and invalid id might result in
	 *            <code>message</code> being displayed to the user.
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param args
	 *            a list of arguments used for formatting the localized string.
	 *            If there are more arguments than required by format,
	 *            additional arguments are ignored.
	 */
	public AndroidServiceException(String message, CoreMessageKey messageKey,
			Throwable cause, Object... args) {
		super(message, cause);
		this.args = args;
		this.messageKey = messageKey;
	}

	/**
	 * Convenience method for
	 * {@link AndroidServiceException#AndroidServiceException(String, CoreMessageKey, Throwable)}
	 * , where the technical exception message is put out in the default locale.
	 * 
	 * @param messageId
	 * @param t
	 */
	public AndroidServiceException(CoreMessageKey messageKey, Throwable cause) {
		this(getStringInDefaultLocale(messageKey), messageKey, cause);
	}

	/**
	 * Convenience method for
	 * {@link AndroidServiceException#AndroidServiceException(String, CoreMessageKey, Throwable, Object...)}
	 * , where the technical exception message is put out in the default locale.
	 * 
	 * @param messageId
	 * @param t
	 */
	public AndroidServiceException(CoreMessageKey messageKey, Throwable cause,
			Object... args) {
		this(String.format(Locale.US, getStringInDefaultLocale(messageKey),
				args), messageKey, cause, args);
	}

	/**
	 * Returns string in default locale
	 * 
	 * @param messageKey
	 * @return
	 */
	private static String getStringInDefaultLocale(CoreMessageKey messageKey) {
		return CoreMessageKey.getBundle(DEFAULT_LOCALE).getString(
				messageKey.get());
	}

	@Override
	public String getLocalizedMessage() {
		if (messageKey == null) {
			return super.getLocalizedMessage();
		}
		String localizedString = null;
		try {
			localizedString = CoreMessageKey.getBundle().getString(
					messageKey.get());

		} catch (MissingResourceException e) {
			LOG.warn("No translation for key: " + messageKey, e);
		}
		if (args != null) {
			localizedString = String.format(Locale.US, localizedString, args);
		}
		if (localizedString != null && !localizedString.isEmpty()) {
			return localizedString;
		} else {
			return super.getLocalizedMessage();
		}

	}

	public CoreMessageKey getMessageKey() {
		return messageKey;
	}
}
