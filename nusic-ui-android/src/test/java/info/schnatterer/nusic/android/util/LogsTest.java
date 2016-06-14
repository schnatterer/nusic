/**
 * Copyright (C) 2013 Johannes Schnatterer
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
package info.schnatterer.nusic.android.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests for {@link Logs}.
 * 
 * @author schnatter
 *
 */
public class LogsTest {

	@Test
	public void testSetRootLogLevel() {
		// TODO test with mockito
	}

	@Test
	public void testSetLogCatLevel() {
		// TODO test with mockito (and robolectric?)
	}

	/**
	 * Tests {@link Logs#findNewestLogFile(File[])}.
	 */
	@Test
	public void testFindNewestLogFile() {
		String expectedPath = "test2015-12-01.log";
		File[] input = new File[] { new File("test2015-01-30.log"),
				new File("test2015-06-20.log"), new File(expectedPath) };
		File actualFile = Logs.findNewestLogFile(input);
		assertEquals("find newest returned unexpected result", expectedPath,
				actualFile.getPath());
	}

	@Test
	public void testGetLogFiles() {
		// TODO test with robolectric
	}

}
