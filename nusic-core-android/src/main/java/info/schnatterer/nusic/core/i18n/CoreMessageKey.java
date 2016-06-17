/**
 * Copyright (C) 2013 Johannes Schnatterer
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
package info.schnatterer.nusic.core.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Definition of all available error messages. Access to the localization bundle
 * is provided via {@link #getBundle(Locale)}.
 * 
 * @author schnatterer
 *
 */
public enum CoreMessageKey {
    ERROR_WRITING_TO_DB("ServiceException_errorWritingToDb"),

    ERROR_READING_FROM_DB("ServiceException_errorReadingFromDb"),

    ERROR_QUERYING_MUSIC_BRAINZ("ServiceException_errorQueryingMusicBrainz"),

    ERROR_FINDING_RELEASE_ARTIST("ServiceException_errorFindingReleasesArtist"),

    ERROR_LOADING_ARTISTS("ServiceException_errorLoadingArtists");

    public static final String CORE_BUNDLE_NAME = "CoreBundle";
    /**
     * Fully qualified core bundle name.
     */
    public static final String CORE_BUNDLE_NAME_FQ = CoreMessageKey.class
            .getPackage().getName() + "." + CORE_BUNDLE_NAME;
    private String messageKey;

    private CoreMessageKey(String msgKey) {
        this.messageKey = msgKey;
    }

    public String get() {
        return messageKey;
    }

    /**
     * Finds the named {@code ResourceBundle} for the specified {@code Locale}
     * and the caller {@code ClassLoader}.
     *
     * @param bundleName
     *            the name of the {@code ResourceBundle}.
     * @param locale
     *            the {@code Locale}.
     * @return the requested resource bundle.
     * @throws MissingResourceException
     *             if the resource bundle cannot be found.
     */
    public static ResourceBundle getBundle(Locale loc)
            throws MissingResourceException {
        return ResourceBundle.getBundle(CORE_BUNDLE_NAME_FQ, loc);
    }

    /**
     * Finds the named resource bundle for the default {@code Locale} and the
     * caller's {@code ClassLoader}.
     *
     * @param bundleName
     *            the name of the {@code ResourceBundle}.
     * @return the requested {@code ResourceBundle}.
     * @throws MissingResourceException
     *             if the {@code ResourceBundle} cannot be found.
     */
    public static ResourceBundle getBundle() throws MissingResourceException {
        return ResourceBundle.getBundle(CORE_BUNDLE_NAME_FQ);
    }
}