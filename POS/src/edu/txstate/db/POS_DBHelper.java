package edu.txstate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class POS_DBHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = "POS_DBHelper";
	
	public POS_DBHelper(Context context) {
		super(context, POSContract.DATABASE_NAME, null, POSContract.DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(LOG_TAG, "CREATING DATABASE!!!");
		db.execSQL(POSContract.Item.SQL_DELETE);
		db.execSQL(POSContract.Item.SQL_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
		Log.v(LOG_TAG, "UPGRADING DATABASE!!!");
        db.execSQL(POSContract.Item.SQL_DELETE);
        onCreate(db);

	}

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
