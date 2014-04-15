package edu.txstate.pos;

import java.util.HashMap;
import java.util.Map;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.service.POSSyncService;
import edu.txstate.pos.storage.Storage;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public abstract class POSFragmentActivity extends FragmentActivity implements POSTaskParent {

	private static final String LOG_TAG = "POSFragmentActivity";
	
	private View mSpinnerView = null;
	private View mMainView = null;
	POSFieldFragment mFieldFragment;
	POSListFragment mItemFragment;
	private Map<String,POSTask> tasks = null;
	boolean showProgress = true;
	String mStatusMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContainer());
		
		mSpinnerView = (View) findViewById(getSpinnerView());
		mMainView = (View) findViewById(getMainView());
		
		mFieldFragment  = getFieldFragment();
		mItemFragment = getListFragment();
		
		tasks = new HashMap<String,POSTask>();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(getFieldContainer(), mFieldFragment);
		transaction.add(getListContainer(), mItemFragment);
		transaction.commit();

	}
	
	abstract POSFieldFragment getFieldFragment();
	abstract POSListFragment getListFragment();
	abstract int getMainView();
	abstract int getSpinnerView();

	protected int getContainer() {
		return R.layout.activity_fragment_container;
	}
	
	protected int getFieldContainer() {
		return R.id.fragment_field_container;
	}

	protected int getListContainer() {
		return R.id.fragment_item_container;
	}
	
	public void deleteSelectedItem() {
		mItemFragment.deleteSelectedItem();
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

	@Override
	public void finishCallback(String taskName) {
		POSTask task = tasks.get(taskName);
		if (task != null) tasks.remove(taskName);
		if (showProgress) showProgress(false);
	}

	@Override
	public void setTaskResult(int result) {
		setResult(result);
	}

	@Override
	public Storage getStorage() {
		return ((POSApplication) getApplication()).getStorage();
	}
	
	protected boolean ping() {
		return ((POSApplication) getApplication()).ping();
	}

	@Override
	public void executeAsyncTask(String name, POSTask task,	boolean showProgress, POSModel... args) {
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
	
	/**
	 * Uses all.xml to make a common menu base for all actions...
	 * unless this gets overridden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all, menu);
		
		/*
		MenuItem fakeItem = menu.findItem(R.id.menu_action_fakeitem);
		Intent intent = new Intent(this, FakeAddItem.class);
		fakeItem.setIntent(intent);
		
		MenuItem fakeCart = menu.findItem(R.id.menu_action_fakecart);
		Intent cartIntent = new Intent(this, CartActivity.class);
		fakeCart.setIntent(cartIntent);
		*/
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
			//case R.id.menu_action_settings:
			//		break;
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
}
