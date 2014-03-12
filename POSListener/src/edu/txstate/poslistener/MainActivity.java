package edu.txstate.poslistener;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final String LOG_TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(LOG_TAG, "Remote client");
		Intent service = new Intent("edu.txstate.pos.remote.POSRemote.SERVICE");
        startService(service);
        
		Log.d(LOG_TAG, "start poll service");
		//Intent pollService = new Intent(getBaseContext(),PollService.class);
		//getBaseContext().startService(pollService);
		Log.d(LOG_TAG, "after start");
		
		Log.d(LOG_TAG, "Bind service");
		//bindService(new Intent("edu.txstate.pos.remote.POSRemote.SERVICE"), (ServiceConnection) pollService, Context.BIND_AUTO_CREATE);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all, menu);
		return true;
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		// If the "all" menu is being used, set the right toggle
		// If the MenuItem isn't found, then that activity has a custom
		// menu
		MenuItem toggleItem = menu.findItem(R.id.menu_action_poll);
		if (toggleItem != null) {
			if (PollService.isServiceAlarmOn(getBaseContext())) {
				toggleItem.setTitle("Stop Sync");
			} else {
				toggleItem.setTitle("Start Sync"); 
			}
		}
		return true; // true or else menu will not be shown
	}
	
	/**
	 * Handler for the menu clicks.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_action_poll:
					// Toggles the alarm
					boolean shouldStart = !PollService.isServiceAlarmOn(getBaseContext());
					PollService.setServiceAlarm(getBaseContext(), shouldStart);
					
					// This is required in later versions to tell the action bar
					// to update itself.
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						this.invalidateOptionsMenu();
					break;
			case R.id.menu_action_settings:
					break;
			default:
					return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			this.invalidateOptionsMenu();
	}
	

}
