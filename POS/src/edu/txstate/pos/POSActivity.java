package edu.txstate.pos;

import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.Storage;
import android.app.Activity;
import android.os.Bundle;

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
