package edu.txstate.poslistener;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class PollService extends IntentService implements ServiceConnection {

	private static final String LOG_TAG = "PollService";
	private static int interval = 10000;
	
	private iRemoteInterface mRemoteInterface = null;
	
	public PollService() {
		super("PollService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(LOG_TAG, "POLL!!!!");

		if (mRemoteInterface != null) {
			Log.i(LOG_TAG, "Interface connected");
			RemoteItem i = new RemoteItem();
			i.setId("ID1");
			i.setDeviceID("deviceFoo");
			i.setPrice("10.99");
			i.setDescription("Remote Item 1");
			try {
				mRemoteInterface.newItem(i);
			} catch (RemoteException e) {
				Log.e(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		} else {
			Log.i(LOG_TAG, "Disconnected");
			//Intent service = new Intent("edu.txstate.pos.remote.POSRemote.SERVICE");
	        //startService(service);
	        bindService(new Intent("edu.txstate.pos.remote.POSRemote.SERVICE"), this, Context.BIND_AUTO_CREATE);
		}
		//stopSelf();
	}
	/**
	 * Returns the current interval.
	 * 
	 * @return The interval in seconds.
	 */
	public static int getInterval() {
		return interval / 1000;
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
		Intent i = new Intent(context, PollService.class);
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
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		Log.d(LOG_TAG,"isServiceAlarmOn? " + (pi != null));
		return pi != null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, "On Service Connected");
		mRemoteInterface = iRemoteInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, "On Service Disconnected");
		mRemoteInterface = null;
	}

}
