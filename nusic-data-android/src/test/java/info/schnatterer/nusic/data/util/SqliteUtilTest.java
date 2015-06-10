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
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.ContentValues;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
// Test is faster without dependency Injection!
public class SqliteUtilTest {

	@Test
	public void testToBoolean0() {
		assertEquals(Boolean.FALSE, SqliteUtil.toBoolean(0));
	}

	@Test
	public void testToBoolean1() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(1));
	}

	@Test
	public void testToBooleanGt1() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(10000000));
	}

	@Test
	public void testToBooleanLt0() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(-1));
	}

	@Test
	public void testToBooleanNull() {
		assertNull(SqliteUtil.toBoolean(null));
	}

	@Test
	public void testToIntegerFalse() {
		assertEquals(Integer.valueOf(0), SqliteUtil.toInteger(Boolean.FALSE));
	}

	@Test
	public void testToIntegerTrue() {
		assertEquals(Integer.valueOf(1), SqliteUtil.toInteger(Boolean.TRUE));
	}

	@Test
	public void testToIntegerNull() {
		assertNull(SqliteUtil.toInteger(null));
	}

	@Test
	public void testToContentValues() {
		Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("key1", Integer.valueOf(42));
		expected.put("KEY2", "value2");

		ContentValues actual = SqliteUtil.toContentValues(expected);

		assertEquals("ContentValues contans unexpected amount of values",
				expected.size(), actual.keySet().size());

		for (Entry<String, Object> expectedEntry : expected.entrySet()) {
			assertEquals("ContentValue returns different value for key "
					+ expectedEntry.getKey(), expectedEntry.getValue(),
					actual.get(expectedEntry.getKey()));
		}
	}

	@Test
	public void testToContentValuesNull() {
		assertNull(SqliteUtil.toContentValues(null));
	}

	@Test
	public void testToContentValuesEmpty() {
		ContentValues actual = SqliteUtil
				.toContentValues(new HashMap<String, Object>());

		assertEquals("ContentValues contans unexpected amount of values", 0,
				actual.keySet().size());
	}
}
