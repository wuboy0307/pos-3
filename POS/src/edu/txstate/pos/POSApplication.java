package edu.txstate.pos;

import java.io.File;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.model.User;
import edu.txstate.pos.service.POSSyncService;
import edu.txstate.pos.storage.Storage;

/**
 * Custom Application class for the POS application.  Used to keep some persistent
 * objects around.
 * 
 * A User object is always set.  The User object will be set to the default "DEVICE"
 * user until it is reset using the setUser method.  This allows an Activity such as
 * the SplashActivity to have access to a User and Storage object before a user is
 * logged into the application.
 * 
 * @author Geoff Marinski
 *
 */
public class POSApplication extends Application {

	private static final String LOG_TAG = "POS_APPLICATION";
	
	private String mDeviceID = null;
	private User mUser = null;
	private Storage mStorage = null;
	private static final int deviceUserID = -1;
	private boolean connected = true;
	private SQLiteDatabase mDb = null;

	private boolean killDBEveryTime = true;
	
	/**
	 * Status of network connectivity
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Sets the state flag for network connectivity
	 * 
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Returns the Storage object.
	 * 
	 * @return The Storage object used to access persistent storage.
	 */
	public Storage getStorage() {
		return mStorage;
	}

	/**
	 * Returns the currently logged in User object.  This is always set even
	 * if it is the default "DEVICE" user.
	 * 
	 * @return Currently logged in User.
	 */
	public User getUser() {
		return mUser;
	}

	/**
	 * Sets the currently logged in User.  This will also reset the currently
	 * instantiated Storage object to the current user is known for logging 
	 * purposes.
	 * 
	 * @param user The User that will be logged in from this point on.
	 */
	public void setUser(User user) {
		mUser = user;
		if (user != null) {
			Log.i(LOG_TAG, "User logged in: " + mUser.getLogin());
			// New user means we need to reset the Storage object with that user
			mStorage.setLoggedInUser(mUser);
		}
	}

	/**
	 * Returns the unique device ID established for this device.
	 * 
	 * @return The unique device ID
	 */
	public String getDeviceID() {
		return mDeviceID;
	}
	
	/**
	 * @return the db
	 */
	public SQLiteDatabase getDb() {
		return mDb;
	}

	/**
	 * Returns true if a user is logged in.  This will return
	 * false if the the default "DEVICE" user is set.
	 * 
	 * @return True if a real user is logged in.
	 */
	public boolean isLoggedIn() {
		if (mUser != null && mUser.getId() >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The onCreate handler.
	 * 
	 * Sets the unique device ID, the default "DEVICE" user as the logged in user, and
	 * creates a Storage object.
	 * 
	 */
	public void onCreate() {
		super.onCreate();
		
		// Generate the unique ID for this device - used for synchronization
		TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    mDeviceID = deviceUuid.toString();
	    Log.i(LOG_TAG, "Device ID: " + mDeviceID);
	    
	    // Set the DEVICE_USER for DB updates and logging
	    mUser = new User();
	    mUser.setId(deviceUserID);
	    mUser.setLogin("DEVICE");
	    Log.i(LOG_TAG,"Set user to DEVICE");

	    File dbFile = new File("/data/data/edu.txstate.pos/databases/POSDB");
	    if (dbFile.exists() && killDBEveryTime) {
	    	boolean deleted = SQLiteDatabase.deleteDatabase(dbFile);
	    	Log.i(LOG_TAG, "Deleted POSDB: " + deleted);
	    } else {
	    	Log.i(LOG_TAG, "POSDB not found");
	    }
	    
	    SQLiteOpenHelper dbHelper = new POS_DBHelper(getApplicationContext());
	    mDb = dbHelper.getWritableDatabase();
	    
	    Log.i(LOG_TAG, "DB set? " + (mDb != null));
	    // Create a Storage object
	    mStorage = new Storage(mDb,mDeviceID,mUser);
	    
	    // Fire up the background sync service
	    //Intent syncService = new Intent(getBaseContext(),POSSyncService.class);
	    //getBaseContext().startService(syncService);
	    //Log.i(LOG_TAG,"Started sync");
	    
	}
}
