package edu.txstate.pos.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "POS";
	
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS USER";
    private static final String SQL_CREATE_ITEM = 
    		"CREATE TABLE ITEM (ITEM_ID INTEGER PRIMARY KEY, NAME TEXT, COST REAL, SYNC INTEGER)";
    private static final String SQL_CREATE_CART =
    		"CREATE TABLE CART (LOCAL_CART_ID INTEGER PRIMARY KEY, CUSTOMER_ID TEXT, ";
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ITEM);
    }
	
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
