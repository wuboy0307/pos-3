package edu.txstate.pos.storage;

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

import android.util.Log;
import edu.txstate.pos.model.User;

public class RemoteStorage {
	
	private static final String ACTION = "action";

	private static final String USER_ACTION_ADD = "add";
	private static final String USER_ACTION_DELETE = "delete";
	private static final String USER_ACTION_UPDATE = "update";
	private static final String USER_ACTION_GET_ALL = "getAll";
	
	private static final String LOGIN = "login";
	private static final String PIN = "pin";
	private static final String IS_ACTIVE = "is_active";
	private static final String IS_ADMIN = "is_admin";
	private static final String USER_ID = "user_id";
	
	private static String RETURN_CODE = "returnCode";
	private static String RETURN_MESSAGE = "returnMessage";
	
	private static int RC_LOGIN_NO_USER_FOUND = 1;
	private static int RC_LOGIN_BAD_PASSWORD = 2;
	
	private static int RC_USER_NO_ACTION = -1;
	private static int RC_USER_EXISTS = 1;
	private static int RC_USER_NO_USER_FOUND = 2;
	
	private static int RC_SUCCESS = 0;
	
	public RemoteStorage() {
		
	}
	
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, user.getLogin());
		params.put(PIN, user.getPIN());
		JSONObject ret = getObject("login",params);
		try {
			if (RC_LOGIN_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_LOGIN_BAD_PASSWORD == ret.getInt(RETURN_CODE)) {
				throw new BadPasswordException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				user.setActive(ret.getString(IS_ACTIVE));
				user.setAdmin(ret.getString(IS_ADMIN));
				user.setId(ret.getInt(USER_ID));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return user;
	}
	
	public User addUser(User user) throws ConnectionError, UserExistsException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, user.getLogin());
		params.put(PIN, user.getPIN());
		params.put(IS_ADMIN, user.isAdmin() ? "Y" : "N");
		params.put(ACTION, USER_ACTION_ADD);
		params.put(IS_ACTIVE,"Y");
		JSONObject ret = getObject("user",params);
		try {
			if (RC_USER_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_EXISTS == ret.getInt(RETURN_CODE)) {
				throw new UserExistsException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				user.setActive(ret.getString(IS_ACTIVE));
				user.setId(ret.getInt(USER_ID));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return user;
	}
	
	public void deleteUser(String login) throws ConnectionError, NoUserFoundException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, login);
		params.put(ACTION, USER_ACTION_DELETE);
		JSONObject ret = getObject("user",params);
		try {
			if (RC_USER_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				Log.d("HTTP", "User deleted: " + login);
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public void updateUser(User user) throws ConnectionError, NoUserFoundException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, user.getLogin());
		params.put(PIN, user.getPIN());
		params.put(IS_ACTIVE, user.isActive() ? "Y" : "N");
		params.put(IS_ADMIN, user.isAdmin() ? "Y" : "N");
		String sID = String.valueOf(user.getId());
		params.put(USER_ID, sID);
		params.put(ACTION, USER_ACTION_UPDATE);
		JSONObject ret = getObject("user",params);
		try {
			if (RC_USER_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				Log.d("HTTP", "User updated: " + user.getId());
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public List<User> getUsers() throws ConnectionError {
		List<User> ret = new ArrayList<User>();
		
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(ACTION, USER_ACTION_GET_ALL);
			JSONObject json = getArray("user",params);
			JSONArray array = json.getJSONArray("data");
			
			User user = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject rec = array.getJSONObject(i);
				user = new User(rec.getString(LOGIN),rec.getString(PIN));
				user.setActive(rec.getString(IS_ACTIVE));
				user.setAdmin(rec.getString(IS_ADMIN));
				ret.add(user);
			}
		} catch (ConnectionError e) {
			e.printStackTrace();
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return ret;
	}
	
	public JSONObject getObject(String function, Map<String, String> params) throws ConnectionError {
		JSONObject ret = null;
		try {
			String json = call(function,params);
			ret = new JSONObject(json);
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return ret;
	}
	
	public JSONObject getArray(String function, Map<String, String> params) throws ConnectionError {
		JSONObject ret = null;
		try {
			String json = call(function,params);
			ret = new JSONObject(json);
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return ret;
	}
	
	public String call(String function, Map<String, String> params) throws ConnectionError {
		StringBuilder buffer = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        String url = "http://172.16.89.203/~g_m108/cgi-bin/" + function + ".pl";
        //String url = "http://cs.txstate.edu/~g_m108/cgi-bin/" + function + ".pl";

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
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return buffer.toString();
      }
}
