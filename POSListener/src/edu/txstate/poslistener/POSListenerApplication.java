package edu.txstate.poslistener;

import java.util.UUID;

import edu.txstate.pos.remote.iRemoteInterface;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * The IPC service connection implementation.
 * 
 * @author gmarinsk
 *
 */
public class POSListenerApplication extends Application implements ServiceConnection {
	
	private static final String LOG_TAG = "POSListener";
	
	private iRemoteInterface mRemoteInterface = null;
	private String mDeviceID = null;
	
	/**
	 * @return the remoteInterface
	 */
	public iRemoteInterface getRemoteInterface() {
		return mRemoteInterface;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return mDeviceID;
	}

	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "Started POSLIstenerApplication");
		
		// Generate the unique ID for this device - used for synchronization
		TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		
		String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		
	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    mDeviceID = deviceUuid.toString();
	    Log.i(LOG_TAG, "Device ID: " + mDeviceID);
		
		//Intent pollService = new Intent(getBaseContext(),PollService.class);
		//getBaseContext().startService(pollService);
		//Intent service = new Intent("edu.txstate.pos.remote.POSRemote.SERVICE");
        //startService(service);

        bind();
        
	}
	
	public void bind() {
        bindService(new Intent("edu.txstate.pos.remote.POSRemote.SERVICE"), 
				this, 
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i(LOG_TAG, "onServiceConnected");
		mRemoteInterface = iRemoteInterface.Stub.asInterface(service);
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i(LOG_TAG, "onServiceDisconnected");
		mRemoteInterface = null;
	}
}
