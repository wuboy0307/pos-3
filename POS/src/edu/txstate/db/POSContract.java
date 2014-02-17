package edu.txstate.db;

import android.provider.BaseColumns;

public final class POSContract {
    
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "POS";
    
	public POSContract() {}
	
	public static abstract class Item implements BaseColumns {
		public static final String TABLE_NAME = "item";
		public static final String COLUMN_NAME_ITEM_ID = "item_id";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_PRICE = "price";
		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String COLUMN_NAME_SYNC = "sync";
		
		public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
				" (" + COLUMN_NAME_ITEM_ID + " INTEGER PRIMARY KEY," +
				" " + COLUMN_NAME_DESCRIPTION + " TEXT," +
				" " + COLUMN_NAME_PRICE + " REAL," +
				" " + COLUMN_NAME_USER_ID + " INTEGER," +
				" " + COLUMN_NAME_SYNC + " INTEGER);";
	}
	
	public static abstract class Cart implements BaseColumns {
		public static final String TABLE_NAME = "cart";
		public static final String COLUMN_NAME_CART_ID = "cart_id";
		public static final String COLUMN_NAME_SYNC = "sync";
	}

}
