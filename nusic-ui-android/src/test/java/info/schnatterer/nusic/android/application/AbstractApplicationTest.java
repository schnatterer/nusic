/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.schnatterer.nusic.android.application.AbstractApplication.AppStart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
// Test is faster without dependency Injection!
public class AbstractApplicationTest {

	private AbstractApplicationUnderTest abstractApplication = new AbstractApplicationUnderTest();

	@Test
	public void testHandleAppVersionFirstStart() {
		final int expectedCurrentVersionCode = 1;
		abstractApplication
				.setMockedLastVersionCode(AbstractApplication.DEFAULT_LAST_APP_VERSION);
		abstractApplication
				.setMockedCurrentVersionCode(expectedCurrentVersionCode);

		abstractApplication.handleAppVersion();

		assertEquals("getAppStart() returned unexpected result ",
				AppStart.FIRST, AbstractApplication.getAppStart());

		assertTrue("onFirstCreate() was not called",
				abstractApplication.isOnFirstCreate());
		assertEquals("onUpgrade() was called unexpectedly",
				AbstractApplication.DEFAULT_LAST_APP_VERSION,
				abstractApplication.getAcutalOldVersion());
		assertEquals("onUpgrade() was called unexpectedly",
				AbstractApplication.DEFAULT_LAST_APP_VERSION,
				abstractApplication.getActualNewVersion());

		assertEquals(
				"Last app version wasn't updated",
				expectedCurrentVersionCode,
				Robolectric.application.getSharedPreferences(
						AbstractApplication.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE).getInt(
						AbstractApplication.KEY_LAST_APP_VERSION, -1));
	}

	@Test
	public void testHandleAppVersionUpgrade() {
		final int expectedLastVersionCode = 0;
		final int expectedCurrentVersionCode = 2;

		abstractApplication.setMockedLastVersionCode(expectedLastVersionCode);
		abstractApplication
				.setMockedCurrentVersionCode(expectedCurrentVersionCode);

		abstractApplication.handleAppVersion();

		assertEquals("getAppStart() returned unexpected result ",
				AppStart.UPGRADE, AbstractApplication.getAppStart());

		assertFalse("onFirstCreate() was called unexpectedly",
				abstractApplication.isOnFirstCreate());

		assertEquals("Unexpected last version code returned",
				expectedLastVersionCode,
				abstractApplication.getAcutalOldVersion());
		assertEquals("Unexpected current version code returned",
				expectedCurrentVersionCode,
				abstractApplication.getActualNewVersion());

		assertEquals(
				"Last app version wasn't updated",
				expectedCurrentVersionCode,
				Robolectric.application.getSharedPreferences(
						AbstractApplication.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE).getInt(
						AbstractApplication.KEY_LAST_APP_VERSION, -1));
	}

	@Test
	public void testHandleAppVersionNormal() {
		final int expectedLastVersionCode = 2;
		final int expectedCurrentVersionCode = 2;

		abstractApplication.setMockedLastVersionCode(expectedLastVersionCode);
		abstractApplication
				.setMockedCurrentVersionCode(expectedCurrentVersionCode);

		abstractApplication.handleAppVersion();

		assertEquals("getAppStart() returned unexpected result ",
				AppStart.NORMAL, AbstractApplication.getAppStart());

		assertFalse("onFirstCreate() was called unexpectedly",
				abstractApplication.isOnFirstCreate());
		assertEquals("onUpgrade() was called unexpectedly",
				AbstractApplication.DEFAULT_LAST_APP_VERSION,
				abstractApplication.getAcutalOldVersion());
		assertEquals("onUpgrade() was called unexpectedly",
				AbstractApplication.DEFAULT_LAST_APP_VERSION,
				abstractApplication.getActualNewVersion());

		assertEquals(
				"Last app version was overwritten unexpectedly",
				expectedCurrentVersionCode,
				Robolectric.application.getSharedPreferences(
						AbstractApplication.SHARED_PREFERENCES_NAME,
						Context.MODE_PRIVATE).getInt(
						AbstractApplication.KEY_LAST_APP_VERSION, -1));
	}

	/**
	 * Implementation of {@link #AbstractApplicationTest()} that facilitates
	 * testing.
	 * 
	 * @author schnatterer
	 *
	 */
	private static class AbstractApplicationUnderTest extends
			AbstractApplication {
		private PackageInfo packageInfo;

		private int acutalOldVersion = DEFAULT_LAST_APP_VERSION;

		private int actualNewVersion = DEFAULT_LAST_APP_VERSION;

		private boolean isOnFirstCreate = false;

		public AbstractApplicationUnderTest() {

			try {
				packageInfo = Robolectric.packageManager.getPackageInfo(
						Robolectric.application.getPackageName(), -1);
			} catch (NameNotFoundException e) {
				// We're in a test, so facilitate exception handling by just
				// crashing
				throw new RuntimeException(e);
			}
		}

		public void setMockedCurrentVersionCode(int currentVersionCode) {
			this.packageInfo.versionCode = currentVersionCode;
		}

		public void setMockedLastVersionCode(int lastVersionCode) {
			Robolectric.application
					.getSharedPreferences(SHARED_PREFERENCES_NAME,
							Context.MODE_PRIVATE).edit()
					.putInt(KEY_LAST_APP_VERSION, lastVersionCode).commit();
		}

		@Override
		protected void onFirstCreate() {
			isOnFirstCreate = true;

		}

		@Override
		protected void onUpgrade(int oldVersion, int newVersion) {
			acutalOldVersion = oldVersion;
			actualNewVersion = newVersion;
		}

		public int getActualNewVersion() {
			return actualNewVersion;
		}

		public int getAcutalOldVersion() {
			return acutalOldVersion;
		}

		@Override
		public Context getApplicationContext() {
			return Robolectric.application;
		}

		protected boolean isOnFirstCreate() {
			return isOnFirstCreate;
		}
	}
}
