package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;

/**
 * Management of local Item objects.
 * 
 */
public class ItemLocalStorage extends LocalStorage {

	private static final String LOG_TAG = "LOCAL_STORAGE_ITEM";
	
	/**
	 * Constructor.
	 * 
	 * @param db
	 */
	public ItemLocalStorage(SQLiteDatabase db) {
		super(db);
		Log.d(LOG_TAG, ("Local null: " + (db == null)));
		Log.d(LOG_TAG, ("parent null: " + (super.db == null)));
	}
	
	/**
	 * Add an Item to the inventory.  Sets the sync flag to push.
	 * 
	 * @param item
	 * @param updUser
	 * @throws SQLException
	 */
	public void addItem(Item item, int sync, User updUser) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.Item.COLUMN_NAME_ITEM_ID, item.getId());
		values.put(POSContract.Item.COLUMN_NAME_DESCRIPTION, item.getDescription());
		values.put(POSContract.Item.COLUMN_NAME_PRICE, item.getPrice());
		values.put(POSContract.Item.COLUMN_NAME_USER_ID, updUser.getId());
		values.put(POSContract.Item.COLUMN_NAME_SYNC, sync);
		
		db.insertOrThrow(POSContract.Item.TABLE_NAME, null, values);
		Log.d(LOG_TAG, "addItem " + sync);
		//values.put(POSContract.Foo.COLUMN_NAME_CODE, "code1");
		//db.insertOrThrow(POSContract.Foo.TABLE_NAME, null, values);
	}
	
	/**
	 * Delete the item from the local inventory.
	 * 
	 * @param itemID  The item to delete.
	 * @throws SQLException
	 */
	public void delete(String itemID) throws SQLException {
		String selection = POSContract.Item.COLUMN_NAME_ITEM_ID + " = ?";
		String[] selectionArgs = { String.valueOf(itemID) };
		db.delete(POSContract.Item.TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Update the item in the local inventory.  Uses the Item ID
	 * to update the data.
	 * 
	 * @param item    The item to update
	 * @param updUser The user making the update.
	 * @throws SQLException
	 */
	public void update(Item item, int sync, User updUser) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.Item.COLUMN_NAME_ITEM_ID, item.getId());
		values.put(POSContract.Item.COLUMN_NAME_DESCRIPTION, item.getDescription());
		values.put(POSContract.Item.COLUMN_NAME_PRICE, item.getPrice());
		values.put(POSContract.Item.COLUMN_NAME_USER_ID, updUser.getId());
		values.put(POSContract.Item.COLUMN_NAME_SYNC, sync );
		
		String selection = POSContract.Item.COLUMN_NAME_ITEM_ID + " = ?";
		String[] selectionArgs = { String.valueOf(item.getId()) };
		
		db.update(POSContract.Item.TABLE_NAME, values, selection, selectionArgs);
	}
	
	/**
	 * Get an Item in the local inventory by item ID.
	 * 
	 * @param itemID  ID of item to get
	 * @return
	 * @throws SQLException
	 * @throws NoItemFoundException
	 */
	public Item getItem(String itemID) throws SQLException, NoItemFoundException {
		Item ret = null;
		
		String[] projection = {
                POSContract.Item.COLUMN_NAME_ITEM_ID,
                POSContract.Item.COLUMN_NAME_DESCRIPTION,
                POSContract.Item.COLUMN_NAME_PRICE,
                POSContract.Item.COLUMN_NAME_SYNC,
                POSContract.Item.COLUMN_NAME_USER_ID
        };
        String sortOrder = POSContract.Item.COLUMN_NAME_ITEM_ID;
        String selection = POSContract.Item.COLUMN_NAME_ITEM_ID + " = ?";
		String[] selectionArgs = { String.valueOf(itemID) };
        Cursor c = db.query(POSContract.Item.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
	        	Item item = new Item(c.getString(0),
	        						 c.getString(1),
	        						 c.getString(2),
	        						 c.getInt(4));
	        	item.setSyncStatus(c.getInt(3));
	        	ret = item;
        } else {
        	throw new NoItemFoundException("No item found for ID " + itemID);
        }
				
		return ret;
	}
	
	/**
	 * Get all of the items in the local inventory.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Item> getItems() throws SQLException {
        List<Item> ret = new ArrayList<Item>();
        
		String[] projection = {
                POSContract.Item.COLUMN_NAME_ITEM_ID,
                POSContract.Item.COLUMN_NAME_DESCRIPTION,
                POSContract.Item.COLUMN_NAME_PRICE,
                POSContract.Item.COLUMN_NAME_SYNC,
                POSContract.Item.COLUMN_NAME_USER_ID
        };
        String sortOrder = POSContract.Item.COLUMN_NAME_ITEM_ID;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(POSContract.Item.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
	        do {
	        	Item item = new Item(c.getString(0),
	        						 c.getString(1),
	        						 c.getString(2),
	        						 c.getInt(4));
	        	item.setSyncStatus(c.getInt(3));
	        	ret.add(item);
	        } while (c.moveToNext());
        }
		
        return ret;
	}
	
	/**
	 * Get all of the unsync'd items in the local inventory.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Item> getUnsyncdItems() throws SQLException {
        List<Item> ret = new ArrayList<Item>();
        
		String[] projection = {
                POSContract.Item.COLUMN_NAME_ITEM_ID,
                POSContract.Item.COLUMN_NAME_DESCRIPTION,
                POSContract.Item.COLUMN_NAME_PRICE,
                POSContract.Item.COLUMN_NAME_SYNC,
                POSContract.Item.COLUMN_NAME_USER_ID
        };
        String sortOrder = POSContract.Item.COLUMN_NAME_ITEM_ID;
        String selection = POSContract.Item.COLUMN_NAME_SYNC + " = ?";
        String[] selectionArgs = { String.valueOf(SyncStatus.PUSH) };
        Cursor c = db.query(POSContract.Item.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
	        do {
	        	Item item = new Item(c.getString(0),
	        						 c.getString(1),
	        						 c.getString(2),
	        						 c.getInt(4));
	        	item.setSyncStatus(c.getInt(3));
	        	ret.add(item);
	        } while (c.moveToNext());
        }
		
        return ret;
	}
	
}
