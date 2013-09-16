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
