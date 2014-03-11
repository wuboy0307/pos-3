package edu.txstate.poslistener;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class POSListenerApplication extends Application {
	
	private static final String LOG_TAG = "POSListener";
	
	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "Started POSLIstenerApplication");
		//Intent pollService = new Intent(getBaseContext(),PollService.class);
		//getBaseContext().startService(pollService);
	}
}
