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
package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.Constants;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * {@link Service} that locks the CPU before running the service.
 * 
 * Similar to <a href="https://github.com/commonsguy/cwac-wakeful">common
 * ware'</a> Wakeful {@link IntentService}.
 * 
 * To use it, just derive your service from this class and override
 * {@link #onStartCommandWakeful(Intent, int, int)} instead of
 * {@link #onStartCommand(Intent, int, int)}.
 * 
 * @author schnatterer
 * 
 */
/**
 * @author schnatterer
 * 
 */
public abstract class WakefulService extends Service {
	private static final String LOCK_NAME = "info.schnatterer.nusic.service.android.WakefulService";

	private static volatile PowerManager.WakeLock lockStatic = null;

	/**
	 * Keeps the lock after {@link #onStartCommand(Intent, int, int)} has been
	 * finished. <b> Note: </b> The derived class that sets this to
	 * <code>true</code> is responsible for calling {@link #releaseLock()}
	 * itself!
	 */
	protected boolean keepLock = false;

	abstract protected int onStartCommandWakeful(Intent intent, int flags,
			int startId);

	synchronized protected static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					LOCK_NAME);
			lockStatic.setReferenceCounted(true);
		}

		return lockStatic;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		acquireLock(this.getApplicationContext());

		try {
			Log.d(Constants.LOG, "Calling service method");
			return onStartCommandWakeful(intent, flags, startId);
		} finally {
			if (!keepLock) {
				releaseLock(this.getApplicationContext());
			}
		}
	}

	/**
	 * Tries to acquire the lock, if not held.
	 */
	protected static void acquireLock(Context context) {
		PowerManager.WakeLock lock = getLock(context);

		if (!lock.isHeld()) {
			lock.acquire();
			Log.d(Constants.LOG, "Lock acquired");
		}
	}

	/**
	 * Releases the lock, if held.
	 */
	protected static void releaseLock(Context context) {
		PowerManager.WakeLock lock = getLock(context);
		if (lock.isHeld()) {
			lock.release();
			Log.d(Constants.LOG, "Lock released");
		}
	}

}
