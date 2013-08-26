package info.schnatterer.newsic.service;



public interface ConnectivityService {
	/**
	 * Used to determine if there is an active data connection and what type of
	 * connection it is if there is one.
	 * 
	 * Queries {@link PreferencesService#isUseOnlyWifi()} to see if a mobile
	 * data connection can be used.
	 * 
	 * @return <code>true</code> if there is an active data connection.
	 *         Otherwise <code>false</code>.
	 */
	boolean isOnline();

//	void registerConnectivityChangeListener(
//			ConnectivityChangedListener connectivtyChangedListener);
//
//	void unregisterConnectivityChangeListener(
//			ConnectivityChangedListener connectivtyChangedListener);

}
