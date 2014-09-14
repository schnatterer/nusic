/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.application;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import info.schnatterer.testUtil.TestUtil;
import junit.framework.TestCase;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

public class AbstractApplicationTest extends TestCase {

	static {
		Thread.currentThread().setContextClassLoader(
				AbstractApplicationTest.class.getClassLoader());
	}

	private AbstractApplicationUnderTest abstractApplication = new AbstractApplicationUnderTest();

	public void testHandleAppVersion() {
		final int expectedLastVersionCode = -1;
		final int expectedCurrentVersionCode = 1;

		abstractApplication.setMockedLastVersionCode(expectedLastVersionCode);
		abstractApplication
				.setMockedCurrentVersionCode(expectedCurrentVersionCode);

		assertFalse("wasUpgraded() didn't return false",
				AbstractApplicationUnderTest.wasUpgraded());

		abstractApplication.handleAppVersion();

		assertEquals("Unexpected last version code returned",
				expectedLastVersionCode,
				abstractApplication.getAcutalOldVersion());
		assertEquals("Unexpected current version code returned",
				expectedCurrentVersionCode,
				abstractApplication.getActualNewVersion());

		assertTrue("wasUpgraded() didn't return true",
				AbstractApplicationUnderTest.wasUpgraded());

		verify(abstractApplication.sharedPreferencesEditor).putInt(
				AbstractApplicationUnderTest.KEY_LAST_APP_VERSION,
				expectedCurrentVersionCode);
		verify(abstractApplication.sharedPreferencesEditor).commit();
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
		private PackageInfo packageInfo = new PackageInfo();

		private SharedPreferences sharedPreferences = mock(SharedPreferences.class);

		private Editor sharedPreferencesEditor = mock(SharedPreferences.Editor.class);

		private int acutalOldVersion;

		private int actualNewVersion;

		public AbstractApplicationUnderTest() {
			TestUtil.setPrivateField(this, "sharedPreferences",
					sharedPreferences, this.getClass().getSuperclass());

			when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
			when(sharedPreferencesEditor.putInt(anyString(), anyInt()))
					.thenReturn(sharedPreferencesEditor);
		}

		public void setMockedCurrentVersionCode(int currentVersionCode) {
			this.packageInfo.versionCode = currentVersionCode;
		}

		public void setMockedLastVersionCode(int lastVersionCode) {
			when(sharedPreferences.getInt(eq(KEY_LAST_APP_VERSION), anyInt()))
					.thenReturn(lastVersionCode);
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
			return new MockContext() {
				@Override
				public PackageManager getPackageManager() {
					return new MockPackageManager() {
						@Override
						public PackageInfo getPackageInfo(String packageName,
								int flags) throws NameNotFoundException {
							return packageInfo;
						}
					};
				}

				@Override
				public String getPackageName() {
					// Avoid UnsupportedOperationException
					return null;
				}
			};
		}
	}
}
