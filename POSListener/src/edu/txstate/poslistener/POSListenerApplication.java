package edu.txstate.poslistener;

import edu.txstate.pos.remote.iRemoteInterface;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class POSListenerApplication extends Application implements ServiceConnection {
	
	private static final String LOG_TAG = "POSListener";
	
	private iRemoteInterface mRemoteInterface = null;
	
	/**
	 * @return the remoteInterface
	 */
	public iRemoteInterface getRemoteInterface() {
		return mRemoteInterface;
	}

	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "Started POSLIstenerApplication");
		//Intent pollService = new Intent(getBaseContext(),PollService.class);
		//getBaseContext().startService(pollService);
		//Intent service = new Intent("edu.txstate.pos.remote.POSRemote.SERVICE");
        //startService(service);
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
