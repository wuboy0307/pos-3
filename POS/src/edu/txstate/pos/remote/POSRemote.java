package edu.txstate.pos.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class POSRemote extends Service {
	
	private static final String LOG_TAG = "POSRemote";
	
	public static final String POS_REMOTE =
			"edu.txstate.pos.remote.POSRemote.SERVICE";

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// Looks like you can have more than one
		// bound interface -- intent as that info - pg 25
		return mRemoteInterfaceBinder;
	}
	
	// The remote interface
	private final iRemoteInterface.Stub mRemoteInterfaceBinder =
			new iRemoteInterface.Stub() {
				public void newItem(RemoteItem i) {
					Log.i(LOG_TAG,"item received");
				}
			};

}
