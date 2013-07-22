package info.schnatterer.newsic.test.db;

import info.schnatterer.newsic.db.NewsicDatabase;
import junit.framework.TestCase;

public class NewsicDatabaseTest extends TestCase {

	private class TestNewsicDatabase extends NewsicDatabase {
		public TestNewsicDatabase() {
			super(null);
		}

		@Override
		public String createTable(String tableName,
				String... columnsAndTypeTuples) {
			return super.createTable(tableName, columnsAndTypeTuples);
		}
	};

	private TestNewsicDatabase db = new TestNewsicDatabase();

	public void testCreateTable() {
		String expected = "CREATE TABLE testdb(id INTEGER PRIMARY KEY AUTOINCREMENT, someText TEXT NOT NULL);";
		String actual = db.createTable("testdb", "id",
				"INTEGER PRIMARY KEY AUTOINCREMENT", "someText",
				"TEXT NOT NULL");
		assertEquals("Unexpected sql query returned", expected, actual);
	}

}
