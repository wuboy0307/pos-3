package edu.txstate.pos;

import edu.txstate.pos.model.User;
import edu.txstate.pos.service.POSSyncService;
import edu.txstate.pos.storage.Storage;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Parent class for every POS application Activity.  This class provides
 * convenience methods to access the Storage and User objects.
 * 
 * To use:
 * Create the Activity normally using the wizard in Eclipse so all of the files
 * for the Activity are created.  After the Activity is there, change the line
 * 'extends Activity' to 'extends POSActivity'. 
 * 
 * The data is from the application context stored in the custom Application class
 * @see edu.txstate.pos.POSApplication
 * 
 * @author Geoff Marinski
 *
 */
public class POSActivity extends Activity {

	/**
	 * onCreate() for the Activity
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Don't think we need to do anything here at this point.
	}
	
	/**
	 * Uses all.xml to make a common menu base for all actions...
	 * unless this gets overridden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all, menu);
		return true;
	}
	
	/**
	 * Used to update the contents of a menu option.  This is called right 
	 * before the menu is shown, every time it is shown.
	 * 
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		MenuItem toggleItem = menu.findItem(R.id.menu_action_poll);
		if (POSSyncService.isServiceAlarmOn(getBaseContext())) {
			toggleItem.setTitle("Stop Sync");
		} else {
			toggleItem.setTitle("Start Sync");
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
					boolean shouldStart = !POSSyncService.isServiceAlarmOn(getBaseContext());
					POSSyncService.setServiceAlarm(getBaseContext(), shouldStart);
					
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
	
	/**
	 * Returns the User object for the user that is currently logged in.
	 * 
	 * @return The currently logged in user.
	 */
	protected User getUser() {
		return ((POSApplication) getApplication()).getUser();
	}
	
	/**
	 * Returns true if a user is logged in.
	 * 
	 * @return True if there is a user logged into the application.
	 */
	protected boolean isLoggedIn() {
		return ((POSApplication) getApplication()).isLoggedIn();
	}
	
	/**
	 * Returns a ready to use Storage object.
	 * 
	 * @return The Storage object
	 */
	protected Storage getStorage() {
		return ((POSApplication) getApplication()).getStorage();
	}
	
}
