package edu.txstate.pos.storage;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.User;

public class CartLocalStorage extends LocalStorage {
	
	private static final String LOG_TAG = "LOCAL_STORAGE_CART";
	
	public CartLocalStorage(SQLiteDatabase db) {
		super(db);
	}
	
	public long createCart(User updUser, String taxRate) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.Cart.COLUMN_NAME_USER_ID, updUser.getId());
		values.put(POSContract.Cart.COLUMN_NAME_TAX_RATE, taxRate);
		values.put(POSContract.Cart.COLUMN_NAME_SYNC, SyncStatus.DRAFT);
		
		long id = db.insertOrThrow(POSContract.Cart.TABLE_NAME, null, values);
		
		return id;
	}
	
	public void deleteCart(User updUser) throws SQLException {
		String selection = POSContract.Cart.COLUMN_NAME_USER_ID + " = ?";
		String[] selectionArgs = { String.valueOf(updUser.getId()) };
		db.delete(POSContract.Cart.TABLE_NAME, selection, selectionArgs);
	}
	
	public Map<String,String> getCart(User updUser) throws SQLException, NoCartFoundException {
		Map<String,String> ret = new HashMap<String,String>();
		
		String[] projection = {
				POSContract.Cart._ID,
				POSContract.Cart.COLUMN_NAME_USER_ID,
				POSContract.Cart.COLUMN_NAME_CUSTOMER_ID,
				POSContract.Cart.COLUMN_NAME_CUSTOMER_NAME,
				POSContract.Cart.COLUMN_NAME_SUBTOTAL,
				POSContract.Cart.COLUMN_NAME_TAX_RATE,
				POSContract.Cart.COLUMN_NAME_TAX_AMOUNT,
				POSContract.Cart.COLUMN_NAME_TOTAL,
				POSContract.Cart.COLUMN_NAME_PAYMENT_CARD,
				POSContract.Cart.COLUMN_NAME_PAYMENT_PIN,
				POSContract.Cart.COLUMN_NAME_SIGNATURE_FILE,
				POSContract.Cart.COLUMN_NAME_SYNC
		};
		String selection = POSContract.Cart.COLUMN_NAME_USER_ID + " = ?";
		String[] selectionArgs = { String.valueOf(updUser.getId()) };
		Cursor c = db.query(POSContract.Cart.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < projection.length; i++) {
				ret.put(projection[i], c.getString(i));
			}
		} else {
			throw new NoCartFoundException("No cart found for user: " + updUser.getId());
		}
		return ret;
	}
}
