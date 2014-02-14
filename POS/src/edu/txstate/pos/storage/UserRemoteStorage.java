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

	private static final String USER_ACTION_ADD = "add";
	private static final String USER_ACTION_DELETE = "delete";
	private static final String USER_ACTION_UPDATE = "update";
	private static final String USER_ACTION_GET_ALL = "getAll";
	private static final String USER_ACTION_LOGIN = "login";
	
	private static final String LOGIN = "login";
	private static final String PIN = "pin";
	private static final String IS_ACTIVE = "is_active";
	private static final String IS_ADMIN = "is_admin";
	private static final String USER_ID = "user_id";

	private static int RC_LOGIN_NO_USER_FOUND = 1;
	private static int RC_LOGIN_BAD_PASSWORD = 2;
	
	private static int RC_USER_NO_ACTION = -1;
	private static int RC_USER_EXISTS = 1;
	private static int RC_USER_NO_USER_FOUND = 2;
	
	public String getScriptName() {
		return "user";
	}
	
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOGIN, user.getLogin());
		params.put(PIN, user.getPIN());
		params.put(ACTION, USER_ACTION_LOGIN);
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
