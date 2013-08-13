//package info.schnatterer.newsic.service.android;
//
//import info.schnatterer.newsic.Constants;
//
//import java.util.Calendar;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//public class LoadNewReleasesScheduler extends BroadcastReceiver {
//	private static final int BROADCAST_REQUEST_ID = 0;
//
//	private LoadNewReleasesServiceConnection loadNewReleasesServiceConnection = LoadNewReleasesServiceConnection;
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		Log.d(Constants.LOG, "Starting LoadNewReleasesService");
//		LoadNewReleasesServiceConnection.startAndBind(this, null, false);
//	}
//
//	public void schedule(Context context, int daysFromNow) {
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, daysFromNow);
//		AlarmManager alarms = (AlarmManager) context
//				.getSystemService(Context.ALARM_SERVICE);
//		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//				cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
//				getPendingIntent(context));
//	}
//
//	private PendingIntent getPendingIntent(Context context) {
//		Intent intent = new Intent(context, this.getClass());
//		return PendingIntent.getBroadcast(context, BROADCAST_REQUEST_ID,
//				intent, PendingIntent.FLAG_CANCEL_CURRENT);
//	}
//}
