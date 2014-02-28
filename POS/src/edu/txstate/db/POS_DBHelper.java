package edu.txstate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Implementation of SQLiteOpenHelper for POS.  Manages the 
 * creation and update of the POS database schema.
 * 
 * @see POSContract
 * 
 */
public class POS_DBHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = "POS_DBHelper";
	
	/**
	 * Constructor.
	 * 
	 * @param context The context 
	 */
	public POS_DBHelper(Context context) {
		super(context, POSContract.DATABASE_NAME, null, POSContract.DATABASE_VERSION);
	}

	/**
	 * onOpen
	 */
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	/**
	 * Create the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(LOG_TAG, "CREATING DATABASE!!!");
		
		Log.v(LOG_TAG, "CREATE TABLE : ITEM");
		Log.d(LOG_TAG, POSContract.Item.SQL_CREATE);
		db.execSQL(POSContract.Item.SQL_CREATE);
		
		Log.v(LOG_TAG, "CREATE TABLE : CART");
		db.execSQL(POSContract.Cart.SQL_CREATE);
		
		Log.v(LOG_TAG, "CREATE TABLE : CART_ITEM");
		db.execSQL(POSContract.CartItem.SQL_CREATE); 
		
		Log.v(LOG_TAG, "CREATE TABLE : POS_SETTINGS");
		db.execSQL(POSContract.POSSettings.SQL_CREATE); 
		
		//Log.v(LOG_TAG,POSContract.Foo.SQL_CREATE);
		//db.execSQL(POSContract.Foo.SQL_CREATE);
		
	}

	/**
	 * On upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
		Log.v(LOG_TAG, "UPGRADING DATABASE!!!");
		
		Log.v(LOG_TAG, "DELETE TABLE : ITEM");
		db.execSQL(POSContract.Item.SQL_DELETE);
		
		Log.v(LOG_TAG, "DELETE TABLE : CART");
		db.execSQL(POSContract.Cart.SQL_DELETE);
		
		Log.v(LOG_TAG, "DELETE TABLE : CART_ITEM");
		db.execSQL(POSContract.CartItem.SQL_DELETE);
		
		Log.v(LOG_TAG, "DELETE TABLE : POS_SETTINGS");
		db.execSQL(POSContract.POSSettings.SQL_DELETE);
		
		//Log.v(LOG_TAG, "DELETE TABLE : FOO");
		//db.execSQL(POSContract.Foo.SQL_DELETE);
		
        onCreate(db);
	}

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
