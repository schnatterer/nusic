package info.schnatterer.newsic.service.android;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

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
public abstract class WakefulService extends Service {
	static final String LOCK_NAME = "info.schnatterer.newsic.service.android.WakefulService";

	private static volatile PowerManager.WakeLock lockStatic = null;

	abstract protected int onStartCommandWakeful(Intent intent, int flags,
			int startId);

	synchronized private static PowerManager.WakeLock getLock(Context context) {
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
		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
			lock.acquire();
		}

		try {
			return onStartCommandWakeful(intent, flags, startId);
		} finally {
			if (lock.isHeld()) {
				lock.release();
			}
		}
	}

}
