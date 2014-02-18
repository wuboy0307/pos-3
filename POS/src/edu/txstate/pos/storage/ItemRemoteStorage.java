package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.pos.model.Item;

public class ItemRemoteStorage extends RemoteStorage {

	public static final String ITEM_ACTION_GET_ALL = "getAll";
	public static final String ITEM_ACTION_GET = "get";
	public static final String ITEM_ACTION_SYNC = "sync";
	
	public static final String DEVICE_ID = "device_id";
	public static final String ITEM_ID = "item_id";
	
	private static int RC_ITEM_NO_ITEM_FOUND = 1;
	
	public ItemRemoteStorage(String android) {
		super(android);
	}
	
	@Override
	public String getScriptName() {
		return "item";
	}

	public List<Item> sync() throws ConnectionError {
		List<Item> ret = new ArrayList<Item>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(ACTION, ITEM_ACTION_SYNC);
			params.put(DEVICE_ID,androidID);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				Item item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new Item(
							rec.getString("item_id"),
							rec.getString("description"),
							(float) rec.getDouble("price"),
							rec.getInt("user_id")
							);
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
	
	public Item getItem(String id) throws ConnectionError, NoItemFoundException {
		Item item = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put(ITEM_ID, id);
		params.put(ACTION, ITEM_ACTION_GET);
		JSONObject ret = getObject(params);
		try {
			if (RC_ITEM_NO_ITEM_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoItemFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				item = new Item(
								ret.getString("item_id"),
								ret.getString("description"),
								(float) ret.getDouble("price"),
								ret.getInt("user_id")
						);
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return item;
	}
	
	public List<Item> getAll() throws ConnectionError {
		List<Item> ret = new ArrayList<Item>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(ACTION, ITEM_ACTION_GET_ALL);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				Item item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new Item(
							rec.getString("item_id"),
							rec.getString("description"),
							(float) rec.getDouble("price"),
							rec.getInt("user_id")
							);
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

}
