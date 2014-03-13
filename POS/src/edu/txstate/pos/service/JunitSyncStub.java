package edu.txstate.pos.service;

import edu.txstate.pos.callback.ServiceCallback;
import android.util.Log;

public class JunitSyncStub implements ServiceCallback {

	private static final String LOG_TAG = "JUNIT_SYNC";
	
	@Override
	public void push() {
		Log.d(LOG_TAG,"CALLED SYNC");

	}

}
