package edu.txstate.pos.service;

import java.util.List;

import edu.txstate.pos.POSApplication;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.RemoteCart;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import edu.txstate.pos.storage.SyncData;
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
	private static int interval = 1000 * 5;  // 30 seconds
	
	/**
	 * Constructor.
	 */
	public POSSyncService() {
		super("POSSyncService");
	}
	
	/**
	 * 
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(LOG_TAG, "Intent: " + intent);
		
		// Check to see if networking is alive
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		
		// The network is there, so try to ping the web service
		SyncData sync = null;
		if (isNetworkAvailable) {
			sync = new SyncData(((POSApplication) getApplication()).getStorage());
			isNetworkAvailable = isNetworkAvailable && sync.ping();
		}
		
		// Set the connection status flag so activities can display the warning
		// icon in the action bar
		((POSApplication) getApplication()).setConnected(isNetworkAvailable);
		
		// No sense in going on if the network or web sercvice isn't working
		if (!isNetworkAvailable) {
			Log.i(LOG_TAG, "Network connectivity is down.");
			boolean isOn = isServiceAlarmOn(getApplicationContext());
			Log.e(LOG_TAG, "On?: " + isOn);
			if (!isOn) {
				Log.i(LOG_TAG, "Turn on!");
				setServiceAlarm(getApplicationContext(), true);
			}
			return;
		}
		
		// PUSH all : push all data from our local device to the remote DB
		Log.d(LOG_TAG,"Pushing...");
		//sync.pushItems();
		Storage storage = ((POSApplication) getApplication()).getStorage();
		try {
			List<Item> items = storage.getUnsyncdItems();
			for (Item i : items) {
				Log.d(LOG_TAG, "Push item: " + i.getId());
				storage.syncItem(i);
			}
			
			List<RemoteCart> carts = storage.getPushableCarts();
			for (RemoteCart cart : carts) {
				Log.d(LOG_TAG, "Push cart: " + cart.getId());
				storage.pushCart(cart);
			}
			stopSelf();
			// If everything works, then shut myself off
			Log.i(LOG_TAG, "Stopping Sync");
		} catch (NoCartFoundException e) {
			stopSelf();
			// If everything works, then shut myself off
			Log.i(LOG_TAG, "Stopping Sync");
		} catch (StorageException e) {
			Log.e(LOG_TAG, "Sync Problem: " + e.getMessage());
			boolean isOn = isServiceAlarmOn(getApplicationContext());
			Log.e(LOG_TAG, "On?: " + isOn);
			if (!isOn) {
				Log.i(LOG_TAG, "Turn on!");
				setServiceAlarm(getApplicationContext(), true);
			}
		}
		
		
		
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
