package info.schnatterer.nusic.android.listeners;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.android.service.ReleasedTodayService;
import info.schnatterer.nusic.android.service.ReleasedTodayService.ReleasedTodayServiceScheduler;
import info.schnatterer.nusic.core.PreferencesService;

import javax.inject.Inject;

import android.app.TimePickerDialog;
import android.content.Context;
import android.preference.Preference;
import android.util.Log;
import android.widget.TimePicker;

/**
 * Creates a preference listener that opens a {@link TimePicker}. If a new time
 * is persisted {@link ReleasedTodayService#schedule(android.content.Context)}
 * is called.<br/>
 * <br/>
 * <b>Make sure to call {@link #setContext(Context)}.</b>
 */
public class PreferenceReleasedTodayTimePickerListener implements
		Preference.OnPreferenceClickListener {
	@Inject
	private PreferencesService preferencesService;

	@Inject
	private Context context;
	@Inject
	private ReleasedTodayServiceScheduler releasedTodayService;

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (context == null) {
			Log.w(Constants.LOG, "No context set in "
					+ this.getClass().getName());
			return false;
		}
		new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
			// Workaround for onTimeSet() being called twice
			boolean isTimeSet = false;

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if (!isTimeSet) {
					isTimeSet = true;
					if (preferencesService.setReleasedTodaySchedule(hourOfDay,
							minute)) {
						releasedTodayService.schedule();
					}
				}
			}
		}, preferencesService.getReleasedTodayScheduleHourOfDay(),
				preferencesService.getReleasedTodayScheduleMinute(), true)
				.show();
		return true;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}