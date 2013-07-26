package info.schnatterer.newsic.db.loader;

import info.schnatterer.newsic.db.dao.impl.ReleaseDaoSqlite;
import info.schnatterer.newsic.db.model.Release;

import java.util.List;

import android.content.Context;

public class ReleaseLoader extends
		AbstractAsyncSqliteLoader<List<Release>, Release, ReleaseDaoSqlite> {

	public ReleaseLoader(Context context) {
		super(context);
	}

	@Override
	protected ReleaseDaoSqlite createDao(Context context) {
		return new ReleaseDaoSqlite(context);
	}

	@Override
	public List<Release> doLoadInBackground() throws Exception {
		// TODO use all newest
		return getDao().findAll();
	}

}
