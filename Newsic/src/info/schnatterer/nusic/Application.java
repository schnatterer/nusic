package info.schnatterer.nusic;

import info.schnatterer.nusic.ui.activities.MainActivity;
import info.schnatterer.nusic.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class Application extends android.app.Application {
	private static final int NOTIFICATION_ID_WARNING = 0;
	private static final int NOTIFICATION_ID_INFO = 1;

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		Application.context = getApplicationContext();
	}

	/**
	 * Returns the application context.
	 * 
	 * @return
	 */
	public static Context getContext() {
		return Application.context;
	}

	/**
	 * Error message for {@link Throwable}s that might not contain a localized
	 * error message.
	 * 
	 * Tells the user that something unexpected has happened in his language and
	 * adds the name of the exception
	 * 
	 * @param t
	 * @return
	 */
	public static String createGenericErrorMessage(Throwable t) {
		return String.format(context.getString(R.string.GenericError), t
				.getClass().getSimpleName());
	}

	public static void toast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toast(int stringId) {
		Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG)
				.show();
	}

	public static void toast(String message, Object... args) {
		toast(String.format(message, args));
	}

	public static void toast(int stringId, Object... args) {
		toast(String.format(context.getString(stringId), args));
	}

	/**
	 * Puts out a notification containing a warning symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param text
	 */
	public static void notifyWarning(String text, Object... args) {
		notify(NOTIFICATION_ID_WARNING, String.format(text, args),
				android.R.drawable.ic_dialog_alert, MainActivity.class);
	}

	public static void notifyWarning(int stringId, Object... args) {
		notifyWarning(getContext().getString(stringId), args);
	}

	/**
	 * Puts out a notification containing an info symbol. Overwrites any
	 * previous instances of this notification.
	 * 
	 * @param text
	 */
	public static void notifyInfo(String text, Object... args) {
		// TODO create and use a nusic icon here
		notify(NOTIFICATION_ID_INFO, String.format(text, args),
				android.R.drawable.ic_dialog_info, MainActivity.class);
	}

	public static void notifyInfo(int stringId, Object... args) {
		notifyInfo(getContext().getString(stringId), args);
	}

	/**
	 * Writes an android notification. Overwrites any previous with the same
	 * <code>id</code>.
	 * 
	 * @param id
	 * @param title
	 * @param text
	 * @param smallIconId
	 * @param context
	 */
	private static void notify(int id, String text, int smallIconId,
			Class<? extends Context> context) {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getContext()).setSmallIcon(smallIconId)
				.setContentTitle(getContext().getString(R.string.app_name))
				.setContentText(text);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(getContext(), context);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(context);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(resultPendingIntent);
		notificationBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) getContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, notificationBuilder.build());
		Log.i(Constants.LOG,"Notifcation: "+ text);
	}
}
