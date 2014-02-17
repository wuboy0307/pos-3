package edu.txstate.pos.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.Item;

public class ItemLocalStorage extends LocalStorage {

	public ItemLocalStorage(Context context) {
		super(context);
	}

	public List<Item> getItems() {
        List<Item> ret = new ArrayList<Item>();
        
		String[] projection = {
                POSContract.Item.COLUMN_NAME_ITEM_ID,
                POSContract.Item.COLUMN_NAME_DESCRIPTION,
                POSContract.Item.COLUMN_NAME_PRICE,
                POSContract.Item.COLUMN_NAME_SYNC
        };
        String sortOrder = POSContract.Item.COLUMN_NAME_ITEM_ID;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(POSContract.Item.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
	        while (c.moveToNext()) {
	        	Item item = new Item(c.getString(1),
	        						 c.getString(2),
	        						 c.getFloat(3),
	        						 c.getInt(4));
	        	ret.add(item);
	        }
        }
		
        return ret;
	}
	
}
