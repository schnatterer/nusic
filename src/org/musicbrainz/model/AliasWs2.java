package org.musicbrainz.model;

import org.musicbrainz.model.entity.listelement.DiscListWs2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Represents an alias for Artist, Works or Labels </p>
 * The alias locale is interesting mostly for transliterations and indicates
 * which locale is used for the alias value. To represent the locale,
 * iso-3166-2 codes like 'ja', 'en', or 'it' are used.</p>
 */
public class AliasWs2 
{
    
    private Log log = LogFactory.getLog(DiscListWs2.class);
    /**
     * The alias
     */
    private String value;

    /**
     * ISO-15924 script code
     */
    private String locale;

    /**
     * @return the getScript
     */
    public String getLocale() {
            return locale;
    }

    /**
     * @param getScript the getScript to set
     */
    public void setLocale(String locale) {
            this.locale = locale;
    }

    /**
     * @return the value
     */
    public String getValue() {
            return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
            this.value = value;
    }

}
