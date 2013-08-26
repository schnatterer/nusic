package info.schnatterer.nusic.test.service.impl;

import info.schnatterer.nusic.service.PreferencesService.AppStart;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;
import junit.framework.TestCase;

public class PreferencesServiceSharedPreferencesTest extends TestCase {
	private PreferencesServiceSharedPreferences service = new PreferencesServiceSharedPreferences(
			null) {
	};

	public void testCheckAppStart() {
		// First start
		int oldVersion = -1;
		int newVersion = 1;
		assertEquals("Unexpected result", AppStart.FIRST_TIME,
				service.checkAppStart(newVersion, oldVersion));

		// First start this version
		oldVersion = 1;
		newVersion = 2;
		assertEquals("Unexpected result", AppStart.FIRST_TIME_VERSION,
				service.checkAppStart(newVersion, oldVersion));

		// Normal start
		oldVersion = 2;
		newVersion = 2;
		assertEquals("Unexpected result", AppStart.NORMAL,
				service.checkAppStart(newVersion, oldVersion));
	}
}
