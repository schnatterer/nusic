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
