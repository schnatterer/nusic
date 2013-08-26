package info.schnatterer.nusic.db.loader;

import info.schnatterer.nusic.db.dao.impl.ReleaseDaoSqlite;
import info.schnatterer.nusic.db.model.Release;

import java.util.Date;
import java.util.List;

import android.content.Context;

public class ReleaseLoader extends
		AbstractAsyncSqliteLoader<List<Release>, Release, ReleaseDaoSqlite> {

	private Date dateCreatedGt;

	/**
	 * @param context
	 * @param dateCreatedGt
	 *            the releases should all have a date created that is greater
	 *            than this
	 */
	public ReleaseLoader(Context context, Date dateCreatedGt) {
		super(context);
		this.dateCreatedGt = dateCreatedGt;
	}

	@Override
	protected ReleaseDaoSqlite createDao(Context context) {
		return new ReleaseDaoSqlite(context);
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		if (dateCreatedGt == null) {
			return getDao().findAll();
		} else {
			return getDao().findJustCreated(dateCreatedGt);
		}
	}
}
