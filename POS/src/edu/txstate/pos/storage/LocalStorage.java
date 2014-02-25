package edu.txstate.pos.storage;

import android.database.sqlite.SQLiteDatabase;

/**
 * The parent class for all local storage objects.
 *
 */
public class LocalStorage {

	SQLiteDatabase db = null;
	
	/**
	 * Constructor.
	 * 
	 * @param db The SQLiteDatabase
	 */
	public LocalStorage(SQLiteDatabase db) {
		this.db = db;
	}
}
