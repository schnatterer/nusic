package info.schnatterer.newsic.db.loader;

import info.schnatterer.newsic.Constants;
import info.schnatterer.newsic.db.dao.impl.AbstractSqliteDao;
import info.schnatterer.newsic.db.model.Entity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public abstract class AbstractAsyncSqliteLoader<RESULT, ENTITY extends Entity, DAO extends AbstractSqliteDao<ENTITY>>
		extends AsyncTaskLoader<AsyncResult<RESULT>> {
	private AsyncResult<RESULT> data;
	private DAO dao;

	public AbstractAsyncSqliteLoader(Context context) {
		super(context);
		dao = createDao(context);
	}

	protected abstract DAO createDao(Context context);

	public abstract RESULT doLoadInBackground() throws Exception;

	protected DAO getDao() {
		return dao;
	}

	@Override
	public void deliverResult(AsyncResult<RESULT> data) {
		if (isReset()) {
			// a query came in while the loader is stopped
			return;
		}

		this.data = data;

		super.deliverResult(data);
	}

	@Override
	public AsyncResult<RESULT> loadInBackground() {
		AsyncResult<RESULT> result = new AsyncResult<RESULT>();

		RESULT dataList = null;

		try {
			dataList = doLoadInBackground();

		} catch (Exception e) {
			Log.e(Constants.LOG, e.getMessage(), e);
			result.setException(e);
		}

		result.setData(dataList);

		return result;
	}

	@Override
	protected void onStartLoading() {
		if (data != null) {
			deliverResult(data);
		}

		if (takeContentChanged() || data == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();

		onStopLoading();

		data = null;
	}

	@Override
	public void onCanceled(AsyncResult<RESULT> data) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(data);
		releaseResources(data);
		// Allow garbage collection
		data = null;
	}

	/**
	 * Cleanup is taken care of by the {@link AsyncTaskLoader}'s lifecycle.
	 * 
	 * @param oldData
	 */
	protected void releaseResources(AsyncResult<RESULT> oldData) {
		// Make sure not to leak a cursor
		dao.closeCursor();
	}

	//
	// @Override
	// public void deliverResult(RESULT newData) {
	// if (isReset()) {
	// releaseResources();
	// return;
	// }
	//
	// // Hold a reference to the old data so it doesn't get garbage collected.
	// // We must protect it until the new data has been delivered.
	// RESULT oldData = result;
	// result = newData;
	//
	// if (isStarted()) {
	// super.deliverResult(result);
	// }
	//
	// // Invalidate the old data as we don't need it any more.
	// if (oldData != null && oldData != result) {
	// releaseResources(oldData);
	// }
	// }
	//
	// @Override
	// protected void onStartLoading() {
	// if (mData != null) {
	// // Deliver any previously loaded data immediately.
	// deliverResult(mData);
	// }
	//
	// // Begin monitoring the underlying data source.
	// if (mObserver == null) {
	// mObserver = new SampleObserver();
	// // TODO: register the observer
	// }
	//
	// if (takeContentChanged() || mData == null) {
	// // When the observer detects a change, it should call
	// // onContentChanged()
	// // on the Loader, which will cause the next call to
	// // takeContentChanged()
	// // to return true. If this is ever the case (or if the current data
	// // is
	// // null), we force a new load.
	// forceLoad();
	// }
	// }
	//
	// @Override
	// protected void onStopLoading() {
	// cancelLoad();
	// }
	//
	// @Override
	// protected void onReset() {
	// // Ensure the loader has been stopped.
	// onStopLoading();
	// }

}
