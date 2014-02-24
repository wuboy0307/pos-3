package edu.txstate.pos.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.txstate.db.POSContract;
import edu.txstate.pos.model.Item;

public class SettingsLocalStorage extends LocalStorage {

	public static final String LOG_TAG = "SETTINGS_LOCAL";
	
	public SettingsLocalStorage(SQLiteDatabase db) {
		super(db);
	}
	
	public void add(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.POSSettings.COLUMN_NAME_KEY, key);
		values.put(POSContract.POSSettings.COLUMN_NAME_VALUE, value);
		db.insertOrThrow(POSContract.POSSettings.TABLE_NAME, null, values);
	}
	
	public void update(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.POSSettings.COLUMN_NAME_KEY,key);
		values.put(POSContract.POSSettings.COLUMN_NAME_VALUE,value);
		
		String selection = POSContract.POSSettings.COLUMN_NAME_KEY + " = ?";
		String[] selectionArgs = { String.valueOf(key) };
		
		db.update(POSContract.POSSettings.TABLE_NAME, values, selection, selectionArgs);
	}
	
	public String get(String key) throws SQLException, NoItemFoundException {
		String ret = null;
		
		String[] projection = {
				POSContract.POSSettings.COLUMN_NAME_VALUE
		};
		
		String selection = POSContract.POSSettings.COLUMN_NAME_KEY + " = ?";
		String[] selectionArgs = { String.valueOf(key) };
		Cursor c = db.query(POSContract.POSSettings.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
        	ret = c.getString(0);
        } else {
        	throw new NoItemFoundException("No key found for key " + key);
        }
        return ret;
	}
}
