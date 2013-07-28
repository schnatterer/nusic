package info.schnatterer.newsic.service;

import info.schnatterer.newsic.db.model.Release;

import java.util.Date;

/**
 * Provides access to the key-value-preferences of the app.
 * 
 * @author schnatterer
 * 
 */
public interface PreferencesService {
	/**
	 * Distinguishes different kinds of app starts: <li>
	 * <ul>
	 * First start ever ({@link #FIRST_TIME})
	 * </ul>
	 * <ul>
	 * First start in this version ({@link #FIRST_TIME_VERSION})
	 * </ul>
	 * <ul>
	 * Normal app start ({@link #NORMAL})
	 * </ul>
	 * 
	 * @author schnatterer
	 * 
	 */
	public enum AppStart {
		FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
	}

	/**
	 * Finds out started for the first time (ever or in the current version).
	 * 
	 * @return the type of app start
	 */
	AppStart checkAppStart();

	/**
	 * Resets all your preferences. Carefuly with that!
	 */
	void clearPreferences();

	/**
	 * Gets the last time the {@link Release}s have <b>successfully</b> been
	 * loaded from the internet
	 */
	Date getLastReleaseRefresh();

	/**
	 * Last time the {@link Release}s have <b>successfully</b> been loaded from
	 * the internet
	 */
	void setLastReleaseRefresh(Date date);

}
