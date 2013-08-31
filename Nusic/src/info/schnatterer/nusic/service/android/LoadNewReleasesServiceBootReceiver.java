package info.schnatterer.nusic.service.android;

import info.schnatterer.nusic.service.PreferencesService;
import info.schnatterer.nusic.service.impl.PreferencesServiceSharedPreferences;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that schedules execution of {@link LoadNewReleasesService}
 * after device has been restarted.
 * 
 * @author schnatterer
 * 
 */
public class LoadNewReleasesServiceBootReceiver extends BroadcastReceiver {
	private static PreferencesService preferencesService = PreferencesServiceSharedPreferences
			.getInstance();

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Date nextReleaseRefresh = preferencesService.getNextReleaseRefresh();

		// if (nextReleaseRefresh == null) {
		// Application.notifyInfo("Starting release refresh");
		// } else {
		// Application.notifyInfo("Scheduling release refresh to "
		// + nextReleaseRefresh);
		// }

		if (nextReleaseRefresh == null || isHistorical(nextReleaseRefresh)) {
			// Start service right away
			context.startService(LoadNewReleasesService
					.createIntentRefreshReleases(context));
		} else {
			// Schedule service
			LoadNewReleasesService.schedule(context,
					preferencesService.getRefreshPeriod(), nextReleaseRefresh);
		}
	}

	/**
	 * @param d
	 * @return <code>true</code> if <code>d</code> is in the past. Otherwise
	 *         <code>false</code>.
	 */
	private boolean isHistorical(Date d) {
		return d.before(new Date());
	}
}
