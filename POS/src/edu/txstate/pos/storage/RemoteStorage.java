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

public abstract class RemoteStorage {
	
	static final String ACTION = "action";
	
	static final String ACTION_GET_ALL = "getAll";
	static final String ACTION_GET = "get";
	static final String ACTION_SYNC = "sync";

	static final String RETURN_CODE = "returnCode";
	static final String RETURN_MESSAGE = "returnMessage";

	static final int RC_NO_ITEM_FOUND = 1;
	static final int RC_SUCCESS = 0;
	static final int RC_NO_ACTION = -99;
	static final int RC_MISSING_REQUIRED_FIELDS = -98;
	static final int RC_SIMULATE_DOWN = -97;
	static final int RC_SIMULATE_BROKEN = -96;
	static final int RC_NO_USER_FOUND = 1;
	static final int RC_BAD_PASSWORD = 2;
	static final int RC_USER_EXISTS = 1;
	static final int RC_USER_NO_USER_FOUND = 2;
	
	static final String ACTION_ADD = "add";
	static final String ACTION_DELETE = "delete";
	static final String ACTION_UPDATE = "update";
	static final String ACTION_LOGIN = "login";

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
	
	String androidID = null;
	
	public RemoteStorage(String androidID) {
		this.androidID = androidID;
	}
	
	public abstract String getScriptName();
	

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
	
	public String call(Map<String, String> params) throws ConnectionError {
		StringBuilder buffer = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        String url = "http://172.16.89.203/~g_m108/cgi-bin/" + getScriptName() + ".pl";
        //String url = "http://cs.txstate.edu/~g_m108/cgi-bin/" + getScriptName() + ".pl";

        HttpPost httpPost = new HttpPost(url);
        Log.d("HTTP", url);
        try {
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	if (params != null && !params.isEmpty()) {
        		for (String key : params.keySet()) {
        			Log.d("HTTP","+ " + key + ":" + params.get(key));
        			nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        		}
        	}

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
        	HttpResponse response = client.execute(httpPost);
        	StatusLine statusLine = response.getStatusLine();
        	int statusCode = statusLine.getStatusCode();
        	Log.d("HTTP","Status Code: " + statusCode);
        	if (statusCode == 200) {
	            HttpEntity entity = response.getEntity();
	            InputStream content = entity.getContent();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	            String line;
	            while ((line = reader.readLine()) != null) {
	              buffer.append(line);
	              Log.d("HTTP","--> " + line);
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
