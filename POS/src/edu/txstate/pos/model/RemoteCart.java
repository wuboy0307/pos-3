package edu.txstate.pos.model;

import java.util.ArrayList;
import java.util.Map;

import edu.txstate.db.POSContract;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.StorageException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RemoteCart extends Cart implements POSModel {

	public RemoteCart(SQLiteDatabase db, long cartID) throws StorageException {
		super();
		storage = new CartLocalStorage(db);
		this.id = cartID;
		populate();
	}

	private void populate() throws StorageException {
		
		try {
			Map<String,String> cart = storage.getCart(id);
			Log.d(LOG_TAG, "Pulled existing cart");
			
			String val = cart.get(POSContract.Cart.COLUMN_NAME_USER_ID);
			user = new User();
			user.setId(Integer.valueOf(val));
			
			//id = Long.valueOf(cart.get(POSContract.Cart._ID));
			String pin = cart.get(POSContract.Cart.COLUMN_NAME_PAYMENT_PIN);
			val = cart.get(POSContract.Cart.COLUMN_NAME_PAYMENT_CARD);
			
			if (pin == null || pin.isEmpty()) {
				if (val != null) 
					payment = new Payment(val);
			} else {
				if (val != null) 
					payment = new Payment(val,pin);
			}

			// Get the items
			items = storage.getItems(id);
			
			val = cart.get(POSContract.Cart.COLUMN_NAME_CUSTOMER_ID);
			if (val != null) 
				customerEmail = val;

			val = cart.get(POSContract.Cart.COLUMN_NAME_TAX_AMOUNT);
			if (val != null)
				taxAmount = val;
			
			val = cart.get(POSContract.Cart.COLUMN_NAME_TAX_RATE);
			if (val != null)
				taxRate = val;
				
			val = cart.get(POSContract.Cart.COLUMN_NAME_SUBTOTAL);
			if (val != null)
				subTotal = val;
				
			val = cart.get(POSContract.Cart.COLUMN_NAME_TOTAL);
			if (val != null)
				total = val;
				
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new StorageException(e.getMessage());
		} catch (NoCartFoundException e) {
			Log.d(LOG_TAG, "Created new cart");
			id = storage.createCart(user, taxRate);
			items = new ArrayList<CartItem>();
		}

	}
	
	public boolean sell() throws StorageException {
		if (!isValid()) throw new StorageException("RemoteCart can't be sold, only pushed.");
		return false;
	}
}
