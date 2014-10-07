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
package info.schnatterer.nusic.data.util;

import info.schnatterer.nusic.data.util.SqliteUtil;
import junit.framework.TestCase;

public class SqliteUtilTest extends TestCase {

	public void testToBoolean0() {
		assertEquals(Boolean.FALSE, SqliteUtil.toBoolean(0));
	}

	public void testToBoolean1() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(1));
	}

	public void testToBooleanGt1() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(10000000));
	}

	public void testToBooleanLt0() {
		assertEquals(Boolean.TRUE, SqliteUtil.toBoolean(-1));
	}

	public void testToBooleanNull() {
		assertNull(SqliteUtil.toBoolean(null));
	}

	public void testToIntegerFalse() {
		assertEquals(Integer.valueOf(0), SqliteUtil.toInteger(Boolean.FALSE));
	}

	public void testToIntegerTrue() {
		assertEquals(Integer.valueOf(1), SqliteUtil.toInteger(Boolean.TRUE));
	}

	public void testToIntegerNull() {
		assertNull(SqliteUtil.toInteger(null));
	}
}
