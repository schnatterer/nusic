/* Copyright (C) 2013 Johannes Schnatterer
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

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.service;

import info.schnatterer.nusic.Application;
import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.util.DefaultLocale;

import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

/**
 * Wrapper class for all exceptions thrown by services. Adds
 * internationalization features, facilitating the message handling for display
 * messages.
 * 
 * @author schnatterer
 * 
 */
public class ServiceException extends Exception {
	static final long serialVersionUID = 1L;

	private int localizedMessageId;
	private Object[] args = null;

	@Override
	public String getLocalizedMessage() {
		try {
			String localizedString = Application.getContext().getString(
					localizedMessageId);
			if (args != null) {
				localizedString = String.format(Locale.US, localizedString,
						args);
			}
			if (!TextUtils.isEmpty(localizedString)) {
				return localizedString;
			} else {
				return super.getLocalizedMessage();
			}
		} catch (Throwable t) {
			Log.e(Constants.LOG, "Failure to localize Message", t);
			return super.getLocalizedMessage();
		}
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 * 
	 * @param message
	 *            the (technical) detail message. The detail message is saved
	 *            for later retrieval by the {@link #getMessage()} method.
	 * @param localizedMessageId
	 *            the id to a localized message intended to be displayed to the
	 *            user. Passing and invalid id might result in
	 *            <code>message</code> being displayed to the user.
	 */
	public ServiceException(String message, int localizedMessageId) {
		super(message);
		this.localizedMessageId = localizedMessageId;
	}

	/**
	 * Convenience method for
	 * {@link ServiceException#ServiceException(String, int)}, where the
	 * technical exception message is put out in the default locale.
	 * 
	 * @param messageId
	 * @param t
	 */
	public ServiceException(int messageId) {
		this(DefaultLocale.getStringInDefaultLocale(messageId), messageId);
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
	 * @param localizedMessageId
	 *            the id to a localized message intended to be displayed to the
	 *            user. Passing and invalid id might result in
	 *            <code>message</code> being displayed to the user.
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public ServiceException(String message, int localizedMessageId,
			Throwable cause) {
		super(message, cause);
		this.localizedMessageId = localizedMessageId;
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
	 * @param localizedMessageId
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
	public ServiceException(String message, int localizedMessageId,
			Throwable cause, Object... args) {
		super(message, cause);
		this.args = args;
		this.localizedMessageId = localizedMessageId;
	}

	/**
	 * Convenience method for
	 * {@link ServiceException#ServiceException(String, int, Throwable)}, where
	 * the technical exception message is put out in the default locale.
	 * 
	 * @param messageId
	 * @param t
	 */
	public ServiceException(int messageId, Throwable cause) {
		this(DefaultLocale.getStringInDefaultLocale(messageId), messageId,
				cause);
	}

	/**
	 * Convenience method for
	 * {@link ServiceException#ServiceException(String, int, Throwable, Object...)}
	 * , where the technical exception message is put out in the default locale.
	 * 
	 * @param messageId
	 * @param t
	 */
	public ServiceException(int messageId, Throwable cause, Object... args) {
		this(String.format(Locale.US,
				DefaultLocale.getStringInDefaultLocale(messageId), args),
				messageId, cause, args);
	}

	public int getLocalizedMessageId() {
		return localizedMessageId;
	}
}
