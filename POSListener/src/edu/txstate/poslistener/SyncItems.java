package edu.txstate.poslistener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.pos.remote.RemoteItem;

import android.util.Log;

/**
 * API for the web services.
 * 
 * @author gmarinsk
 *
 */
public class SyncItems {

	private final String LOG_TAG = "Listener Sync RemoteItems";
	
	// RETURN CODES
	static final int RC_SUCCESS = 0;
	// ACTION
	static final String ACTION_SYNC = "sync";
	// FIELDS
	static final String RETURN_CODE = "returnCode";
	static final String RETURN_MESSAGE = "returnMessage";

	static final String ACTION = "action";
	
	static final String DEVICE_ID = "device_id";

	static final String ITEM_ID = "item_id";
	static final String DESCRIPTION = "description";
	static final String UPDATE_USER = "update_user_id";
	static final String PRICE = "price";
	
	private String androidID = null;
	
	public SyncItems(String androidID) {
		this.androidID = androidID;
	}
	
	/**
	 * Returns the list of RemoteItems that are new since the last time it was
	 * asked for.
	 * 
	 * @return
	 * @throws ConnectionError
	 */
	public List<RemoteItem> sync() throws ConnectionError {
		List<RemoteItem> ret = new ArrayList<RemoteItem>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(ACTION, ACTION_SYNC);
			params.put(DEVICE_ID,androidID);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				RemoteItem item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new RemoteItem();
					item.setId(rec.getString(ITEM_ID));
					item.setDescription(rec.getString(DESCRIPTION));
					item.setPrice(rec.getString(PRICE));
					ret.add(item);
				} 
			} else {
				throw new ConnectionError("Return code: " + json.getInt(RETURN_CODE));
			}
		} catch (ConnectionError e) {
			throw new ConnectionError(e.getMessage());
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return ret;
	}

	/**
	 * Gets a JSON object as returned by the POS server.
	 * 
	 * @param params The HTTP parameters values
	 * @return	JSON object that was returned.
	 * @throws ConnectionError
	 */
	public JSONObject getObject(Map<String, String> params) throws ConnectionError {
		JSONObject ret = null;
		try {
			String json = call(params);
			ret = new JSONObject(json);
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return ret;
	}
	
	/**
	 * Makes the network call and gets the return value back as a string.
	 * 
	 * @param params The HTTP parameters/values
	 * @return String returned from the HTTP call
	 * @throws ConnectionError
	 */
	public String call(Map<String, String> params) throws ConnectionError {
		StringBuilder buffer = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        //String url = "http://172.16.89.203/~g_m108/cgi-bin/item.pl";
        String url = "http://cs.txstate.edu/~g_m108/cgi-bin/item.pl";

        HttpPost httpPost = new HttpPost(url);
        Log.d(LOG_TAG, url);
        try {
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	if (params != null && !params.isEmpty()) {
        		for (String key : params.keySet()) {
        			Log.d(LOG_TAG,"+ " + key + ":" + params.get(key));
        			nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        		}
        	}

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
        	HttpResponse response = client.execute(httpPost);
        	StatusLine statusLine = response.getStatusLine();
        	int statusCode = statusLine.getStatusCode();
        	Log.d(LOG_TAG,"Status Code: " + statusCode);
        	if (statusCode == 200) {
	            HttpEntity entity = response.getEntity();
	            InputStream content = entity.getContent();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	            String line;
	            while ((line = reader.readLine()) != null) {
	              buffer.append(line);
	              Log.d(LOG_TAG,"--> " + line);
	            }
		    } else {
		    	throw new ConnectionError("BAD HTTP STATUS: " + statusCode);
		    }
        } catch (ClientProtocolException e) {
        	throw new ConnectionError("ClientProtocolException: " + e.getMessage());
        } catch (IOException e) {
        	throw new ConnectionError("IOException: " + e.getMessage());
        }
        return buffer.toString();
      }
	
}
