package edu.txstate.pos.storage;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.CartItem;

public class CartRemoteStorage extends RemoteStorage {

	public CartRemoteStorage(String deviceID) {
		super(deviceID);
	}
	
	public String getScriptName() {
		return "cart";
	}
	
	public void add(Cart cart) throws JSONException, ConnectionError {
		JSONObject obj = new JSONObject();
		
		obj.put(CART_ID, cart.getId());
		obj.put(USER_ID, cart.getUser().getId());
		obj.put(CUSTOMER_EMAIL, cart.getCustomer());
		obj.put(SUBTOTAL, cart.getSubTotal());
		obj.put(TAX_RATE, cart.getTaxRate());
		obj.put(TAX_AMOUNT, cart.getTaxAmount());
		obj.put(TOTAL, cart.getTotal());
		obj.put(PAYMENT_CARD, cart.getPayments().getCardNumber());
		if (cart.getPayments().getPin() != null)
			obj.put(PAYMENT_PIN, cart.getPayments().getPin());
		
		JSONArray itemList = new JSONArray();
		for (CartItem ci : cart.getItems()) {
			JSONObject jsonCI = new JSONObject();
			jsonCI.put(ITEM_ID, ci.getItem().getId());
			jsonCI.put(QUANTITY, ci.getQuantity());
			jsonCI.put(PRICE, ci.getItem().getPrice());
		
			itemList.put(jsonCI);
		}
		obj.put(ITEMS, itemList);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(CART, obj.toString());
		params.put(DEVICE_ID, androidID);
		params.put(ACTION, ACTION_ADD);
		JSONObject ret = getObject(params);
		try {
			if (RC_SUCCESS == ret.getInt(RETURN_CODE)) {
				// Good!
			} else {
				throw new ConnectionError("Bad return code for addCart - " + ret.getInt(RETURN_CODE));
			}
		} catch (JSONException e) {
			throw new ConnectionError("JSON parser error: " + e.getMessage());
		}
	}
}
