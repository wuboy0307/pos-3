package edu.txstate.pos.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Parent class for all remote storage classes.  It:
 * Provides the constants for the field types.
 * Provides the remote service call.
 * 
 * The abstract method will simply return the script name
 * for the service on the POS server side.  There is a 
 * one-to-one mapping between a remote class implementation
 * and the server side script.  For example:
 * ItemRemoteStorage will hit item.pl
 * 
 * The string returned leaves off the '.pl'
 * 
 * The JSON objects are returned in one of two formats.  All
 * return calls contain a RETURN_CODE and RETURN_MESSAGE.  The
 * RETURN_CODE should be checked for RC_SUCCESS and throw an
 * appropriate exception otherwise.
 * 
 * The first format is where one object is returned (or if just
 * a return code is sent back):
 * {
 *  "returnMessage":"Success: 0",
 *  "returnCode":0,
 *  "user_id":"1",
 *  "price":"9.99",
 *  "updateTimestamp":"2014-02-18 18:45:10",
 *  "description":"Item 1","item_id":"001"
 *  }
 *  
 *  The second format is where more than one object can be
 *  returned (such as getAll()). The element 'data' will contain
 *  an array of object.:
 *  
 *  {
 *   "returnMessage":"Success: 5",
 *   "returnCode":0,
 *   "data":
 *   	  [
 *   		{"pin":"1234","is_active":"Y","is_admin":"Y","user_id":"4","updateTimestamp":"2014-02-18 18:45:10","login":"admin"},
 *   		{"pin":"1234","is_active":"Y","is_admin":"Y","user_id":"2","updateTimestamp":"2014-02-18 18:45:10","login":"binh"},
 *    		{"pin":"1234","is_active":"Y","is_admin":"N","user_id":"-1","updateTimestamp":"2014-02-18 18:45:10","login":"DEVICE"},
 *    		{"pin":"5555","is_active":"Y","is_admin":"N","user_id":"1","updateTimestamp":"2014-02-18 18:45:10","login":"geoff"},
 *    		{"pin":"4321","is_active":"Y","is_admin":"Y","user_id":"3","updateTimestamp":"2014-02-18 18:45:10","login":"pham"}
 *        ]
 *  }
 *
 */
public abstract class RemoteStorage {
	
	public static final String LOG_TAG = "REMOTE_STORAGE_HTTP";
	
	// ACTIONS
	static final String ACTION_GET_ALL = "getAll";
	static final String ACTION_GET = "get";
	static final String ACTION_SYNC = "sync";
	static final String ACTION_PING = "ping";

	// RETURN CODES
	static final int RC_NO_ITEM_FOUND = 1;
	static final int RC_ITEM_EXISTS = -1;
	static final int RC_SUCCESS = 0;
	static final int RC_NO_ACTION = -99;
	static final int RC_MISSING_REQUIRED_FIELDS = -98;
	static final int RC_SIMULATE_DOWN = -97;
	static final int RC_SIMULATE_BROKEN = -96;
	static final int RC_NO_USER_FOUND = 1;
	static final int RC_BAD_PASSWORD = 2;
	static final int RC_USER_EXISTS = 1;
	static final int RC_USER_NO_USER_FOUND = 2;
	
	// ACTIONS
	static final String ACTION_ADD = "add";
	static final String ACTION_DELETE = "delete";
	static final String ACTION_UPDATE = "update";
	static final String ACTION_LOGIN = "login";

	// FIELDS
	static final String RETURN_CODE = "returnCode";
	static final String RETURN_MESSAGE = "returnMessage";

	static final String ACTION = "action";
	
	static final String DEVICE_ID = "device_id";
	static final String LOGIN = "login";
	static final String PIN = "pin";
	static final String IS_ACTIVE = "is_active";
	static final String IS_ADMIN = "is_admin";
	static final String USER_ID = "user_id";
	static final String ITEM_ID = "item_id";
	static final String DESCRIPTION = "description";
	static final String UPDATE_USER = "update_user_id";
	static final String PRICE = "price";
	
	// The device ID passed to most methods
	String androidID = null;
	
	/**
	 * Constructor
	 * 
	 * @param androidID  The device ID
	 */
	public RemoteStorage(String androidID) {
		this.androidID = androidID;
	}
	
	/**
	 * Provide the name of the POS server side script, minus the 
	 * ".pl"
	 * 
	 * @return The script name to call for this class
	 */
	public abstract String getScriptName();
	

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
        String url = "http://172.16.89.203/~g_m108/cgi-bin/" + getScriptName() + ".pl";
        //String url = "http://cs.txstate.edu/~g_m108/cgi-bin/" + getScriptName() + ".pl";

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
