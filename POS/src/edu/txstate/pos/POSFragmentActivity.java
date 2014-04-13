package edu.txstate.pos;

import java.util.HashMap;
import java.util.Map;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.Storage;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public abstract class POSFragmentActivity extends FragmentActivity implements POSTaskParent {

	private static final String LOG_TAG = "POSFragmentActivity";
	
	private View mSpinnerView = null;
	private View mMainView = null;
	POSFieldFragment mFieldFragment;
	POSListFragment mItemFragment;
	private Map<String,POSTask> tasks = null;
	boolean showProgress = true;
	
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
}
