package edu.txstate.pos.storage;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;

public class CartLocalStorage extends LocalStorage {
	
	public CartLocalStorage(SQLiteDatabase db) {
		super(db);
	}
	
	public Cart createCart(User updUser) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.Cart.COLUMN_NAME_USER_ID, updUser.getId());
		values.put(POSContract.Cart.COLUMN_NAME_SYNC, SyncStatus.DRAFT);
		
		long id = db.insertOrThrow(POSContract.Cart.TABLE_NAME, null, values);
		
		Cart cart = new Cart(updUser,id);
		return cart;
	}
}
