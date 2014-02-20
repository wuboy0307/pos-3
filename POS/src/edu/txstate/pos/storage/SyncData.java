package edu.txstate.pos.storage;

import android.util.Log;

public class SyncData {
	
	public static final String LOG_TAG = "SYNC";
	
	private Storage storage = null;
	
	public SyncData(Storage storage) {
		this.storage = storage;
	}
	
	public void pushItems() {
		Log.d(LOG_TAG,"pushItems()");
	}
	
	public boolean ping() {
		return storage.ping();
	}
}
