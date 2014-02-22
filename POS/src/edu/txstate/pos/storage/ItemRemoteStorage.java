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
			params.put(ACTION, ACTION_SYNC);
			params.put(DEVICE_ID,androidID);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				Item item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new Item(
							rec.getString(ITEM_ID),
							rec.getString(DESCRIPTION),
							(float) rec.getDouble(PRICE),
							rec.getInt(UPDATE_USER)
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
		params.put(ACTION, ACTION_GET);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_ITEM_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoItemFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				item = new Item(
								ret.getString(ITEM_ID),
								ret.getString(DESCRIPTION),
								(float) ret.getDouble(PRICE),
								ret.getInt(UPDATE_USER)
						);
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
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
			params.put(ACTION, ACTION_GET_ALL);
			JSONObject json = getObject(params);
			if (RC_SUCCESS == json.getInt(RETURN_CODE)) {
				JSONArray array = json.getJSONArray("data");
				
				Item item = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject rec = array.getJSONObject(i);
					item = new Item(
							rec.getString(ITEM_ID),
							rec.getString(DESCRIPTION),
							(float) rec.getDouble(PRICE),
							rec.getInt(UPDATE_USER)
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
