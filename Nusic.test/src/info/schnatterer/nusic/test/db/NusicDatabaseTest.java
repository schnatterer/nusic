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
package info.schnatterer.nusic.test.db;

import info.schnatterer.nusic.db.NusicDatabase;
import junit.framework.TestCase;

public class NusicDatabaseTest extends TestCase {

	private class TestNusicDatabase extends NusicDatabase {
		public TestNusicDatabase() {
			super(null);
		}

		@Override
		public String createTable(String tableName,
				String... columnsAndTypeTuples) {
			return super.createTable(tableName, columnsAndTypeTuples);
		}
	};

	private TestNusicDatabase db = new TestNusicDatabase();

	public void testCreateTable() {
		String expected = "CREATE TABLE testdb(id INTEGER PRIMARY KEY AUTOINCREMENT, someText TEXT NOT NULL);";
		String actual = db.createTable("testdb", "id",
				"INTEGER PRIMARY KEY AUTOINCREMENT", "someText",
				"TEXT NOT NULL");
		assertEquals("Unexpected sql query returned", expected, actual);
	}

}
