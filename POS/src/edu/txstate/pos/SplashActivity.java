package edu.txstate.pos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import edu.txstate.db.POSContract;
import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.Storage;

public class SplashActivity extends POSActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
        new SplashAsync().execute(getBaseContext());
	}

	// @Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.splash, menu);
	//	return true;
	//}

	private class SplashAsync extends AsyncTask<Context, String, String> {

		@Override
		protected String doInBackground(Context... context) {
			

			
			POS_DBHelper dbHelper = new POS_DBHelper(context[0]);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        //onProgressUpdate("GET DATABASE");
	        db.execSQL(POSContract.Item.SQL_DELETE);
	        //onProgressUpdate("DELETE");
	        db.execSQL(POSContract.Item.SQL_CREATE);
	        //onProgressUpdate("CREATE");
	        
	        Storage storage = getStorage();
	        try {
				storage.syncItems();
			} catch (ConnectionError e) {
				Log.e("SPLASH_ACTIVITY",e.getMessage());
			}

			return null;
		}
		
	    protected void onProgressUpdate(String... progress) {
	    	TextView txt = (TextView) findViewById(R.id.splash_status);
	        txt.setText(progress[0]);
	    }

	    protected void onPostExecute(String result) {
	    	TextView txt = (TextView) findViewById(R.id.splash_status);
	        txt.setText("Executed");
	    }

	}
	
}
