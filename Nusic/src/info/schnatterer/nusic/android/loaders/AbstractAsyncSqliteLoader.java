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
package info.schnatterer.nusic.android.loaders;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.data.model.Entity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public abstract class AbstractAsyncSqliteLoader<RESULT, ENTITY extends Entity>
		extends AsyncTaskLoader<AsyncResult<RESULT>> {
	private AsyncResult<RESULT> data;

	public AbstractAsyncSqliteLoader(Context context) {
		super(context);
	}

	public abstract RESULT doLoadInBackground() throws Exception;

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
		// Allow garbage collection
		data = null;
	}
}
