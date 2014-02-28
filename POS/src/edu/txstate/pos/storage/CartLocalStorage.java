package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.CartItem;
import edu.txstate.pos.model.Item;
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
	
	public void deleteCart(User updUser, int syncStatus) throws SQLException {
		String selection = POSContract.Cart.COLUMN_NAME_USER_ID + " = ? AND " + POSContract.Cart.COLUMN_NAME_SYNC + " = ?";
		String[] selectionArgs = { String.valueOf(updUser.getId()), String.valueOf(syncStatus) };
		db.delete(POSContract.Cart.TABLE_NAME, selection, selectionArgs);
	}
	
	public Map<String,String> getCart(User updUser) throws SQLException, NoCartFoundException {
		Map<String,String> ret = new HashMap<String,String>();
		
		String[] projection = {
				POSContract.Cart._ID,
				POSContract.Cart.COLUMN_NAME_USER_ID,
				POSContract.Cart.COLUMN_NAME_CUSTOMER_ID,
				POSContract.Cart.COLUMN_NAME_SUBTOTAL,
				POSContract.Cart.COLUMN_NAME_TAX_RATE,
				POSContract.Cart.COLUMN_NAME_TAX_AMOUNT,
				POSContract.Cart.COLUMN_NAME_TOTAL,
				POSContract.Cart.COLUMN_NAME_PAYMENT_CARD,
				POSContract.Cart.COLUMN_NAME_PAYMENT_PIN,
				POSContract.Cart.COLUMN_NAME_SIGNATURE_FILE,
				POSContract.Cart.COLUMN_NAME_SYNC
		};
		String selection = POSContract.Cart.COLUMN_NAME_USER_ID + " = ? AND " + POSContract.Cart.COLUMN_NAME_SYNC + " = ?";
		String[] selectionArgs = { String.valueOf(updUser.getId()), String.valueOf(SyncStatus.DRAFT) };
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
	
	public void updateCart(long cartID, ContentValues cart) throws SQLException {
		String selection = POSContract.Cart._ID + " = ?";
		String[] selectionArgs = { String.valueOf(cartID) };
		
		db.update(POSContract.Cart.TABLE_NAME, cart, selection, selectionArgs);
	}
	
	public void addItem(long cartID, String itemID, int quantity) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.CartItem.COLUMN_NAME_CART_ID, cartID);
		values.put(POSContract.CartItem.COLUMN_NAME_ITEM_ID, itemID);
		values.put(POSContract.CartItem.COLUMN_NAME_QUANTITY, quantity);
		
		db.insertOrThrow(POSContract.CartItem.TABLE_NAME, null, values);
	}
	
	public void updateItem(long cartID, String itemID, int quantity) throws SQLException {
		ContentValues values = new ContentValues();
		//values.put(POSContract.CartItem.COLUMN_NAME_CART_ID, cartID);
		//values.put(POSContract.CartItem.COLUMN_NAME_ITEM_ID, itemID);
		values.put(POSContract.CartItem.COLUMN_NAME_QUANTITY, quantity);
		
		String selection = POSContract.CartItem.COLUMN_NAME_CART_ID + " = ? and " + POSContract.CartItem.COLUMN_NAME_ITEM_ID + " = ?";
		String[] selectionArgs = { String.valueOf(cartID), String.valueOf(itemID) };
		
		db.update(POSContract.CartItem.TABLE_NAME, values, selection, selectionArgs);
	}
	
	public void deleteItem(long cartID, String itemID) throws SQLException {
		String selection = POSContract.CartItem.COLUMN_NAME_CART_ID + " = ? AND " + POSContract.CartItem.COLUMN_NAME_ITEM_ID + " = ?";
		String[] selectionArgs = { String.valueOf(cartID), String.valueOf(itemID) };
		db.delete(POSContract.CartItem.TABLE_NAME, selection, selectionArgs);
	}
	
	public List<CartItem> getItems(long cartID) throws SQLException {
		List<CartItem> ret = new ArrayList<CartItem>();
		String sql = "select i.*, c.quantity from cart_item c, item i where c.item_id = i.item_id and c.cart_id = ?";
		String[] selectionArgs = { String.valueOf(cartID) };
		Cursor c = db.rawQuery(sql, selectionArgs);
		CartItem ci = null;
		if (c.moveToFirst()) {
			ci = new CartItem(new Item(c.getString(0),c.getString(1),c.getString(2)),c.getInt(5));
			ret.add(ci);
			while (c.moveToNext()) {
				ci = new CartItem(new Item(c.getString(0),c.getString(1),c.getString(2)),c.getInt(5));
				ret.add(ci);
			}
		}
		return ret;
	}
}
