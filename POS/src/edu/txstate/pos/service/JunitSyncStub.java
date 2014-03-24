package edu.txstate.pos.service;

import java.util.List;

import org.json.JSONException;

import edu.txstate.pos.POSApplication;
import edu.txstate.pos.callback.ServiceCallback;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.RemoteCart;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.CartRemoteStorage;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.util.Log;

public class JunitSyncStub implements ServiceCallback {

	private static final String LOG_TAG = "JUNIT_SYNC";
	
	private Storage storage = null;
	
	public JunitSyncStub(Storage storage) {
		this.storage = storage;
	}
	
	@Override
	public void push() {
		Log.d(LOG_TAG,"Pushing...");
		//sync.pushItems();
		try {
			List<Item> items = storage.getUnsyncdItems();
			for (Item i : items) {
				Log.d(LOG_TAG, "Push item: " + i.getId());
				storage.syncItem(i);
			}
			
			List<RemoteCart> carts = storage.getPushableCarts();
			for (RemoteCart cart : carts) {
				Log.d(LOG_TAG, "Push cart: " + cart.getId());
				storage.pushCart(cart);
				storage.setCartDone(cart.getId());
			}
		} catch (StorageException e) {
			Log.e(LOG_TAG, "Sync Problem: " + e.getMessage());
			e.printStackTrace();
		} catch (NoCartFoundException e) {
			Log.e(LOG_TAG, "NoCartFoundException");
		}
	}

}
