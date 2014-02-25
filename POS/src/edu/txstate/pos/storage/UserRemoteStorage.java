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

/**
 * Management of remote User objects.  Includes login.
 * 
 * See user.pl for server side.
 * 
 */
public class UserRemoteStorage extends RemoteStorage {

	/**
	 * Constructor.
	 * 
	 * @param androidID The device ID
	 */
	public UserRemoteStorage(String androidID) {
		super(androidID);
	}

	/**
	 * Use the user.pl service
	 */
	public String getScriptName() {
		return "user";
	}
	
	/**
	 * Uses the login and password in the given User object and attempts to login.  If the login
	 * is successful, then the User object is returned populated with the current user's settings.
	 * Otherwise, an exception is thrown.
	 * 
	 * @param user
	 * @return A populated User object
	 * @throws ConnectionError
	 * @throws NoUserFoundException
	 * @throws BadPasswordException
	 */
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
	
	/**
	 * Adds a User to the system if the login doesn't already exist.
	 * 
	 * @param user The User to add
	 * @return The same User object with the User ID populated
	 * @throws ConnectionError
	 * @throws UserExistsException
	 */
	public User add(User user) throws ConnectionError, UserExistsException {
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
	
	/**
	 * Deletes the user associated with the given login.
	 * 
	 * @param login The login to delete.
	 * @throws ConnectionError
	 * @throws NoUserFoundException
	 */
	public void delete(String login) throws ConnectionError, NoUserFoundException {
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
	
	/**
	 * Update the given User.  The ID of the User is used to find the user to
	 * update, if it exists.  The the ID isn't given, then the login is used.
	 * 
	 * @param user The User to update.
	 * @throws ConnectionError
	 * @throws NoUserFoundException
	 */
	public void update(User user) throws ConnectionError, NoUserFoundException {
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
	
	/**
	 * Gets all of the users - active or inactive.
	 * 
	 * @return A List of all the users.
	 * @throws ConnectionError
	 */
	public List<User> getAll() throws ConnectionError {
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
