package edu.txstate.pos.storage;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Pings the remote POS server for basic function.
 * 
 */
public class Ping extends RemoteStorage {

	public static final String LOG_TAG = "PING";

	/**
	 * Constructor.
	 * 
	 * @param androidID The device ID
	 */
	public Ping(String androidID) {
		super(androidID);
	}
	
	/**
	 * Uses to ping.pl
	 */
	@Override
	public String getScriptName() {
		return "ping";
	}
	
	/**
	 * Pings the POS server.
	 * 
	 * @return True if the POS server is working
	 */
	public boolean ping() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ACTION, ACTION_PING);
		params.put(DEVICE_ID,androidID);
		boolean ret = false;
		try {
			JSONObject obj = getObject(params);
			if (RC_SUCCESS == obj.getInt(RETURN_CODE)) {
				ret = true;
			} else if (RC_SIMULATE_BROKEN == obj.getInt(RETURN_CODE)) {
				Log.i(LOG_TAG, "Simulating Broken");
				ret = false;
			} else if (RC_SIMULATE_DOWN == obj.getInt(RETURN_CODE)) {
				Log.i(LOG_TAG, "Simulating Down");
				ret = false;
			}
		} catch (JSONException e) {
			ret = false;
		} catch (ConnectionError e) {
			ret = false;
		}
		return ret;
	}

}
