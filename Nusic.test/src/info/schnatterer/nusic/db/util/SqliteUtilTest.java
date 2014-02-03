package info.schnatterer.nusic.db.util;

import info.schnatterer.nusic.db.util.SqliteUtil;
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
