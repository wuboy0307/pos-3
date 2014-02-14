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
	public static final String ITEM_ACTION_SYNC = "sync";
	
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
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				Item item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new Item(
							rec.getString("item_id"),
							rec.getString("description"),
							rec.getDouble("price"),
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
							rec.getDouble("price"),
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
