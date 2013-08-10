package info.schnatterer.newsic.db.model;

import info.schnatterer.newsic.db.dao.impl.AbstractSqliteDao;

public interface Entity {
	Long getId();

	void setId(Long id);

	/**
	 * Called by the {@link AbstractSqliteDao} before persisting.
	 */
	void prePersist();
}
