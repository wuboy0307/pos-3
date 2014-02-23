package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;

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
							rec.getString(PRICE));
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
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_GET);
		JSONObject ret = getObject(params);
		try {
			if (RC_NO_ITEM_FOUND == ret.getInt(RETURN_CODE)) {
				throw new NoItemFoundException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				item = new Item(
								ret.getString(ITEM_ID),
								ret.getString(DESCRIPTION),
								ret.getString(PRICE));
			} else {
				throw new ConnectionError("Bad return code for getItems - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
		return item;
	}
	
	public void addItem(Item item, User updUser) throws ConnectionError, ItemExistsException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ITEM_ID, item.getId());
		params.put(DESCRIPTION, item.getDescription());
		params.put(PRICE, item.getPrice());
		params.put(DEVICE_ID, androidID);
		params.put(UPDATE_USER, String.valueOf(updUser.getId()));
		params.put(ACTION, ACTION_ADD);
		JSONObject ret = getObject(params);
		try {
			if (RC_ITEM_EXISTS == ret.getInt(RETURN_CODE)) {
				throw new ItemExistsException(ret.getString(RETURN_MESSAGE));
			} else if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				return;
			} else {
				throw new ConnectionError("Bad return code for addItem - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public void updateItem(Item item, User updUser) throws ConnectionError, ItemExistsException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ITEM_ID, item.getId());
		params.put(DESCRIPTION, item.getDescription());
		params.put(PRICE, item.getPrice());
		params.put(DEVICE_ID, androidID);
		params.put(UPDATE_USER, String.valueOf(updUser.getId()));
		params.put(ACTION, ACTION_UPDATE);
		JSONObject ret = getObject(params);
		try {
			if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				return;
			} else {
				throw new ConnectionError("Bad return code for updateItem - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public void deleteItem(String itemID, User updUser) throws ConnectionError {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ITEM_ID, itemID);
		params.put(UPDATE_USER, String.valueOf(updUser.getId()));
		params.put(ACTION, ACTION_DELETE);
		JSONObject ret = getObject(params);
		try {
			if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				return;
			} else {
				throw new ConnectionError("Bad return code for deleteItem - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
	
	public List<Item> getAll() throws ConnectionError {
		List<Item> ret = new ArrayList<Item>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(DEVICE_ID, androidID);
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
							rec.getString(PRICE));
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
