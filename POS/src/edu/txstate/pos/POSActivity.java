package edu.txstate.pos;

import java.util.HashMap;
import java.util.Map;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.model.User;
import edu.txstate.pos.service.POSSyncService;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;

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
public abstract class POSActivity extends Activity implements POSTaskParent {

	private static final String LOG_TAG = "POSActivity-Parent";
	
	private View mSpinnerView = null;
	private View mMainView = null;
	private Map<String,POSTask> tasks = null;
	boolean showProgress = true;
	String mStatusMessage = null;
	
	/**
	 * onCreate() for the Activity
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());
		mSpinnerView = (View) findViewById(getSpinnerView());
		mMainView = (View) findViewById(getMainView());
		tasks = new HashMap<String,POSTask>();
		Log.d(LOG_TAG,"Null? " + (mSpinnerView == null));
	}
	
	// The child's content view
	abstract int getContentView();
	// The child's main view
	abstract int getMainView();
	// The child's spinner view
	abstract int getSpinnerView();
	
	/**
	 * Uses all.xml to make a common menu base for all actions...
	 * unless this gets overridden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all, menu);
		
		MenuItem fakeItem = menu.findItem(R.id.menu_action_fakeitem);
		Intent intent = new Intent(this, FakeAddItem.class);
		fakeItem.setIntent(intent);
		
		MenuItem fakeCart = menu.findItem(R.id.menu_action_fakecart);
		Intent cartIntent = new Intent(this, CartActivity.class);
		fakeCart.setIntent(cartIntent);
		
		return true;
	}
	
	/**
	 * Used to update the contents of a menu option.  This is called right 
	 * before the menu is shown, every time it is shown.
	 * 
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			ActionBar actionBar = this.getActionBar();
			if (((POSApplication) getApplication()).isConnected()) {
				actionBar.setIcon(R.drawable.ic_pos_app);
			} else {
				actionBar.setIcon(R.drawable.ic_action_bad);	
			}
		}
		
		// If the "all" menu is being used, set the right toggle
		// If the MenuItem isn't found, then that activity has a custom
		// menu
		MenuItem toggleItem = menu.findItem(R.id.menu_action_poll);
		if (toggleItem != null) {
			if (POSSyncService.isServiceAlarmOn(getBaseContext())) {
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
	//public boolean onMenuItemSelected(int featureID, MenuItem item) {
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
			//case R.id.menu_action_fakeitem:
			//		Log.d(LOG_TAG, "Fake Item");
			//		Intent intent = new Intent(this, FakeAddItem.class);
			//		startService(intent);
			//		break;
			default:
					return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	/**
	 * Handler for activity being resumed.
	 */
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			this.invalidateOptionsMenu();
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
	public Storage getStorage() {
		return ((POSApplication) getApplication()).getStorage();
	}
	
	/**
	 * Return the current cart.
	 * 
	 * @return
	 * @throws StorageException
	 */
	protected Cart getCart() throws StorageException {
		return ((POSApplication) getApplication()).getCart();
	}
	
	/**
	 * Shows the spinner.
	 * 
	 * @param Show spinner or not
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSpinnerView.setVisibility(View.VISIBLE);
			mSpinnerView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSpinnerView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mMainView.setVisibility(View.VISIBLE);
			mMainView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mMainView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mSpinnerView.setVisibility(show ? View.VISIBLE : View.GONE);
			mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Sets the return value for the activity
	 * 
	 * @param result The return value
	 */
	public void setTaskResult(int result) {
		setResult(result);
	}
	
	/**
	 * When an async taks is complete, remove it from
	 * the task list and turn off the spinner
	 * 
	 * @param Name of the task
	 */
	public void finishCallback(String taskName) {
		POSTask task = tasks.get(taskName);
		if (task != null) tasks.remove(taskName);
		if (showProgress) showProgress(false);
		
	}
	
	/**
	 * Execute the given async task if it isn't already
	 * being executed.
	 * 
	 * @param task An instance of the task to be executed
	 * @param args The arguments given to the task
	 */
	public void executeAsyncTask(String name, POSTask task, boolean showProgress, POSModel... args) {
		Log.d(LOG_TAG,"executeAsync");
		POSTask current = tasks.get(name);
		if (current == null) {
			Log.d(LOG_TAG,"Not found");
			this.showProgress = showProgress;
			showProgress(true && showProgress);
			tasks.put(task.getClass().getName(), task);
			task.execute(args);
		} else {
			Log.d(LOG_TAG,"Found");
		}
	}
	
}
