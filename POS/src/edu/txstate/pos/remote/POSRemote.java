package edu.txstate.pos.remote;

import edu.txstate.pos.POSApplication;
import edu.txstate.pos.storage.NoItemFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
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
	/**
	 * When the client binds, it needs the remote interface that applies to be
	 * returned.
	 * 
	 * @param Intent - The intent of the calling application
	 */
	public IBinder onBind(Intent arg0) {
		// Looks like you can have more than one
		// bound interface -- intent as that info - pg 25
		return mNewItemInterfaceBinder;
	}
	
	/**
	 * Implementation of the service called.  Adds the new item passed in to the
	 * local storage.
	 */
	private final iRemoteInterface.Stub mNewItemInterfaceBinder =
			new iRemoteInterface.Stub() {
				public void newItem(RemoteItem i) {
					Log.i(LOG_TAG,"item received: " + i.getItem().getId());
					Storage storage = ((POSApplication) getApplication()).getStorage();
					try {
						try {
							storage.getItem(i.getItem().getId());
							Log.i(LOG_TAG,"Item already exists locally: " + i.getItem().getId());
							
							if (i.getDeviceID() != null && i.getDeviceID().equals(storage.getDeviceID())) {
								Log.i(LOG_TAG, "My item came back: " + i.getItem().getId());
							} else {
								Log.i(LOG_TAG, "Updating item: " + i.getItem().getId());
								storage.updateAsSyncdItem(i.getItem());
							}
						} catch (NoItemFoundException e) {
							Log.i(LOG_TAG,"Item not found locally: " + i.getItem().getId());
							storage.addSyncdItem(i.getItem());
						}
					} catch (StorageException e) {
						Log.e(LOG_TAG,e.getMessage());
					}
				}
			};

}
