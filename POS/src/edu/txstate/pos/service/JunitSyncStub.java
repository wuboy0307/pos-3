package edu.txstate.pos.service;

import android.util.Log;

public class JunitSyncStub implements SyncService {

	private static final String LOG_TAG = "JUNIT_SYNC";
	
	@Override
	public void startSync() {
		Log.d(LOG_TAG,"CALLED SYNC");

	}

}
