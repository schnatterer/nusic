package info.schnatterer.nusic.db.model;

import info.schnatterer.nusic.db.dao.impl.AbstractSqliteDao;

public interface Entity {
	Long getId();

	void setId(Long id);

	/**
	 * Called by the {@link AbstractSqliteDao} before persisting.
	 */
	void prePersist();
}
