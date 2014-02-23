package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.txstate.pos.model.User;

public class UserRemoteStorage extends RemoteStorage {


	
	public String getScriptName() {
		return "user";
	}
	
	public UserRemoteStorage(String androidID) {
		super(androidID);
	}
	
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, user.getLogin());
		params.put(PIN, user.getPIN());
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_LOGIN);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_BAD_PASSWORD == ret.getInt(RETURN_CODE)) {
				throw new BadPasswordException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				user.setActive(ret.getString(IS_ACTIVE));
				user.setAdmin(ret.getString(IS_ADMIN));
				user.setId(ret.getInt(USER_ID));
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
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
		params.put(IS_ACTIVE,"Y");
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_ADD);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_EXISTS == ret.getInt(RETURN_CODE)) {
				throw new UserExistsException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				user.setActive(ret.getString(IS_ACTIVE));
				user.setId(ret.getInt(USER_ID));
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return user;
	}
	
	public void deleteUser(String login) throws ConnectionError, NoUserFoundException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, login);
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_DELETE);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				Log.d("HTTP", "User deleted: " + login);
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
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
		params.put(USER_ID, String.valueOf(user.getId()));
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_UPDATE);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_ACTION == ret.getInt(RETURN_CODE)) {
				throw new ConnectionError(ret.getString(RETURN_MESSAGE));
			} else if (RC_USER_NO_USER_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoUserFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				Log.d("HTTP", "User updated: " + user.getId());
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public List<User> getUsers() throws ConnectionError {
		List<User> ret = new ArrayList<User>();
		
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(DEVICE_ID, androidID);
			params.put(ACTION, ACTION_GET_ALL);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				User user = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					user = new User(rec.getString(LOGIN),rec.getString(PIN));
					user.setActive(rec.getString(IS_ACTIVE));
					user.setAdmin(rec.getString(IS_ADMIN));
					ret.add(user);
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
}
