package edu.txstate.pos.storage;

import android.database.sqlite.SQLiteDatabase;

public class LocalStorage {

	SQLiteDatabase db = null;
	
	public LocalStorage(SQLiteDatabase db) {
		this.db = db;
	}
}
