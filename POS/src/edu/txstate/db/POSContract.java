package edu.txstate.db;

import android.provider.BaseColumns;

public final class POSContract {
    
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "POSDB";
    
	public POSContract() {}
	
	public static abstract class Item implements BaseColumns {
		public static final String TABLE_NAME = "item";
		public static final String COLUMN_NAME_ITEM_ID = "item_id";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_PRICE = "price";
		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String COLUMN_NAME_SYNC = "sync";
		
		public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
				" (" + COLUMN_NAME_ITEM_ID + " TEXT PRIMARY KEY," +
				" " + COLUMN_NAME_DESCRIPTION + " TEXT," +
				" " + COLUMN_NAME_PRICE + " TEXT," +
				" " + COLUMN_NAME_USER_ID + " INTEGER," +
				" " + COLUMN_NAME_SYNC + " INTEGER);";
	}
	/*
	public static abstract class Cart implements BaseColumns {
		public static final String TABLE_NAME = "cart";
		public static final String _ID = "cart_id";
		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
		public static final String COLUMN_NAME_CUSTOMER_NAME = "customer_name";
		public static final String COLUMN_NAME_SUBTOTAL = "subtotal";
		public static final String COLUMN_NAME_TAX_RATE = "tax_rate";
		public static final String COLUMN_NAME_TAX_AMOUNT = "tax_amount";
		public static final String COLUMN_NAME_TOTAL = "total";
		public static final String COLUMN_NAME_PAYMENT_CARD = "card_no";
		public static final String COLUMN_NAME_PAYMENT_PIN = "pin";
		public static final String COLUMN_NAME_SIGNATURE_FILE = "signature_file";
		public static final String COLUMN_NAME_SYNC = "sync";
		
		public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
				" (" + _ID + " INTEGER NOT NULL, " + 
				COLUMN_NAME_USER_ID + " INTEGER NOT NULL UNIQUE, " +
				COLUMN_NAME_CUSTOMER_ID + " TEXT, " + 
				COLUMN_NAME_CUSTOMER_NAME + " TEXT, " + 
				COLUMN_NAME_SUBTOTAL + " TEXT, " + 
				COLUMN_NAME_TAX_RATE + " TEXT, " + 
				COLUMN_NAME_TAX_AMOUNT + " TEXT, " + 
				COLUMN_NAME_TOTAL + " TEXT, " + 
				COLUMN_NAME_PAYMENT_CARD + " TEXT, " +
				COLUMN_NAME_PAYMENT_PIN + " TEXT, " +
				COLUMN_NAME_SIGNATURE_FILE + " TEXT, " + 
				COLUMN_NAME_SYNC + " INTEGER, " + 
				"PRIMARY KEY(" + _ID + "))";
				
	}

	public static abstract class CartItem implements BaseColumns {
		public static final String TABLE_NAME = "cart_item";
		public static final String COLUMN_NAME_CART_ID = "cart_id";
		public static final String COLUMN_NAME_ITEM_ID = "item_id";
		public static final String COLUMN_NAME_QUANTITY = "quantity";
			
		public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
				" (" + COLUMN_NAME_CART_ID + " INTEGER NOT NULL, " + 
				COLUMN_NAME_ITEM_ID + " TEXT NOT NULL, " + 
				COLUMN_NAME_QUANTITY + " INTEGER, " + 
				"PRIMARY KEY(" + COLUMN_NAME_CART_ID + "," + COLUMN_NAME_ITEM_ID + "))";		
		}*/
	
}
