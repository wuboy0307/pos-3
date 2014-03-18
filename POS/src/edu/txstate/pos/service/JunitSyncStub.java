package edu.txstate.pos.service;

import org.json.JSONException;

import edu.txstate.pos.callback.ServiceCallback;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.CartRemoteStorage;
import edu.txstate.pos.storage.ConnectionError;
import android.util.Log;

public class JunitSyncStub implements ServiceCallback {

	private static final String LOG_TAG = "JUNIT_SYNC";
	
	private CartRemoteStorage remote = null;
	private CartLocalStorage local = null;
	
	@Override
	public void push() {
		Log.d(LOG_TAG,"CALLED SYNC");
		Cart cart = null;
		try {
			remote.add(cart);
		} catch (ConnectionError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
