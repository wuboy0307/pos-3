package edu.txstate.pos.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class LocalStorage {

	SQLiteDatabase db = null;
	
	public LocalStorage(Context context) {
		DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
	}
}
