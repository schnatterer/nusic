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
package info.schnatterer.nusic.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

//@RunWith(RobolectricTestRunner.class)
//@Config(manifest = Config.NONE)
public class NusicDatabaseSqliteTest {
	//
	// @Before
	// public void setUp() {
	// Provider<Context> contextProviderMock = new Provider<Context>() {
	// @Override
	// public Context get() {
	// return Robolectric.application;
	// }
	// };
	// NusicDatabaseSqlite.contextProvider = contextProviderMock;
	// }

	@Test
	public void testCreateTable() {
		String expected = "CREATE TABLE testdb(id INTEGER PRIMARY KEY AUTOINCREMENT, someText TEXT NOT NULL);";
		String actual = NusicDatabaseSqlite.createTable("testdb", "id",
				"INTEGER PRIMARY KEY AUTOINCREMENT", "someText",
				"TEXT NOT NULL");
		assertEquals("Unexpected sql query returned", expected, actual);
	}

}
