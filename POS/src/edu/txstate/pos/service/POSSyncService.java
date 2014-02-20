package edu.txstate.pos.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class POSSyncService extends IntentService {

	private static final String LOG_TAG = "SYNC_SERVICE";
	
	// TODO: Make this a configurable interval
	private static int interval = 1000 * 30;  // 30 seconds
	
	public POSSyncService() {
		super("POSSyncService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(LOG_TAG, "Intent: " + intent);
		
		// Check to see if networking is alive - no sense going on if not
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		if (!isNetworkAvailable) return;
		
		Log.d(LOG_TAG,"Calling sync");
		
	}
	
	/**
	 * Returns the current interval.
	 * 
	 * @return The interval in seconds.
	 */
	public static int getInterval() {
		return POSSyncService.interval / 1000;
	}
	
	/**
	 * Sets the interval for the alarm to the given number of seconds.
	 * 
	 * @param seconds
	 */
	public static void setInterval(int seconds) {
		interval = seconds * 1000;
	}
	
	/**
	 * Sets the alarm for this background service to fire the service at the interval.
	 * The flag turnOn is used to tell it to turnOn or off.  The current state can be
	 * figured out using the isServiceAlarmOn() method
	 * 
	 * @see isServiceAlaramOn()
	 * 
	 * @param context
	 * @param turnOn Should the alarm be turned on?
	 */
	public static void setServiceAlarm(Context context, boolean turnOn) {
		Intent i = new Intent(context, POSSyncService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (turnOn) {
			Log.i(LOG_TAG,"ON!!");
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pi);
		} else {
			Log.i(LOG_TAG,"OFF!!");
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}
	
	/**
	 * PendingIntent is a token object - the token will be the same if you ask for
	 * the PendingIntent with the same Intent object.
	 * 
	 * When an alarm is cancelled, the pending intent is also cleaned up so you can
	 * use the FLAG_NO_CREATE option (meaning don't create an intent if you don't 
	 * find one for this intent I'm passing) to tell if the intent is there or not.
	 * If null comes back, then there is no PendingIntent hanging around firing alarms.
	 * 
	 * @param context
	 * @return If the alarm is on
	 */
	public static boolean isServiceAlarmOn(Context context) {
		Intent i = new Intent(context, POSSyncService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		Log.d(LOG_TAG,"isServiceAlarmOn? " + (pi != null));
		return pi != null;
	}

}
