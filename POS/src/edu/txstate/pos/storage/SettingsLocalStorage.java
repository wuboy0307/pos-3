package edu.txstate.pos.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.txstate.db.POSContract;

/**
 * Manages the local settings storage.
 * 
 */
public class SettingsLocalStorage extends LocalStorage {

	public static final String LOG_TAG = "SETTINGS_LOCAL";
	
	/**
	 * Constructor.
	 * 
	 * @param db  Current SQLiteDatabase
	 */
	public SettingsLocalStorage(SQLiteDatabase db) {
		super(db);
	}
	
	/**
	 * Add a value for the given key for local settings.
	 * 
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void add(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.POSSettings.COLUMN_NAME_KEY, key);
		values.put(POSContract.POSSettings.COLUMN_NAME_VALUE, value);
		db.insertOrThrow(POSContract.POSSettings.TABLE_NAME, null, values);
	}
	
	/**
	 * Update the value for the given local setting.
	 * 
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void update(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		values.put(POSContract.POSSettings.COLUMN_NAME_KEY,key);
		values.put(POSContract.POSSettings.COLUMN_NAME_VALUE,value);
		
		String selection = POSContract.POSSettings.COLUMN_NAME_KEY + " = ?";
		String[] selectionArgs = { String.valueOf(key) };
		
		db.update(POSContract.POSSettings.TABLE_NAME, values, selection, selectionArgs);
	}
	
	/**
	 * Get the local settings value for the given key.
	 * 
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws NoItemFoundException
	 */
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
