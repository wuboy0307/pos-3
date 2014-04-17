package edu.txstate.pos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.callback.ServiceCallback;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;
import edu.txstate.pos.service.POSSyncService;
import edu.txstate.pos.service.SyncService;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import edu.txstate.pos.storage.SyncData;

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
public class POSApplication extends Application implements SyncService, ServiceCallback {

	private static final String LOG_TAG = "POS_APPLICATION";
	
	private String mDeviceID = null;
	private User mUser = null;
	private Storage mStorage = null;
	private Cart mCart = null;
	private static final int deviceUserID = -1;
	private boolean connected = true;
	private SQLiteDatabase mDb = null;
	private List<User> mUsers = null;

	// This setting will delete the database when the application starts
	private boolean killDBEveryTime = false;
	
	private HomeActivity mHome = null;
	
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
	 * @return the home
	 */
	public HomeActivity getHome() {
		return mHome;
	}

	/**
	 * @param home the home to set
	 */
	public void setHome(HomeActivity home) {
		mHome = home;
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
	 * Returns the Cart object.
	 * 
	 * @return The Cart for the logged in user.
	 */
	public Cart getCart() throws StorageException {
		if (isLoggedIn()) {
			if (mCart == null) {
				mCart = new Cart(mDb,getCurrentTaxRate(),mUser,this);
			} else {
				if (mCart.getUser().getId() != mUser.getId()) {
					mCart = new Cart(mDb,getCurrentTaxRate(),mUser,this);
				}
			}
		}
		return mCart;
	}
	
	public void sellCart() {
		mCart = null;
	}
	
	public void deleteCart() throws SQLException, NoCartFoundException {
		if (mCart != null) {
			mCart.delete();
			mCart = null;
		}
	}
	
	public Cart getLastCart(String email) throws StorageException, NoCartFoundException {
		Cart ret = null;
		ret = Cart.getLastCart(mDb, email);
		return ret;
	}
	
	// TODO: Get this from the settings
	public String getCurrentTaxRate() {
		return "0.1";
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
	 * Get the user list for the UserAdminActivity ArrayAdapter.
	 * 
	 * @return the users
	 * @throws ConnectionError 
	 */
	public List<User> getUsers() {
		if (mUsers == null) mUsers = new ArrayList<User>();
		return mUsers;
	}

	/**
	 * Set the user list used by the UserAdminActivity.  This
	 * list is backing an ArrayAdapter so it can't be a simple
	 * set since the object that is created by getUsers() is
	 * being used - not the reference itself.  So the obejct
	 * must be cleared out and reset with the new data passed
	 * in.
	 * 
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		if (mUsers != null) {
			mUsers.clear();
			mUsers.addAll(users);
		}
	}

	/**
	 * Log the current user out.
	 * 
	 */
	public void logUserOut() {
		mUser = null;
		// Don't think setting cart to null is required
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
	    mStorage = new Storage(mDb,mDeviceID,mUser,this);
	    
	    // Fire up the background sync service
	    //Intent syncService = new Intent(getBaseContext(),POSSyncService.class);
	    //getBaseContext().startService(syncService);
	    //Log.i(LOG_TAG,"Started sync");
	    
	}
	
	public void startSync() {
		Intent syncService = new Intent(getBaseContext(),POSSyncService.class);
	    getBaseContext().startService(syncService);
	    Log.i(LOG_TAG,"Started sync");
	}

	@Override
	public void push() {
		Log.i(LOG_TAG,"Push");
		boolean b = POSSyncService.isServiceAlarmOn(getApplicationContext());
		Log.i(LOG_TAG, "B: " + b);
		Intent syncService = new Intent(getApplicationContext(),POSSyncService.class);
	    startService(syncService);
	    Log.i(LOG_TAG,"Started push sync");
	}
	
	public boolean ping() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		
		// The network is there, so try to ping the web service
		SyncData sync = null;
		if (isNetworkAvailable) {
			sync = new SyncData(mStorage);
			isNetworkAvailable = isNetworkAvailable && sync.ping();
		}
		Log.d(LOG_TAG,"PING: " + isNetworkAvailable);
		return isNetworkAvailable;
	}
}
