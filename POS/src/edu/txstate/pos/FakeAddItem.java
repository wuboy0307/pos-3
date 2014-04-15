package edu.txstate.pos;

import edu.txstate.pos.model.Item;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FakeAddItem extends POSActivity {

	private static final String LOG_TAG = "FakeAddItem";
	
	private FakeAddItemActivityTask mFakeTask = null;
	
	private View mFakeStatusView = null;
	private View mFakeView = null;
	
	private Button mOneButton = null;
	private Button mTwoButton = null;
	private Button mThreeButton = null;
	
	private TextView mItemStatus = null;
	private String mStatusMessage = "Select an item to add";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fake_add_item);
	
		mFakeView = findViewById(R.id.fadd_form);
		mFakeStatusView = findViewById(R.id.fadd_spinner);
		
		mOneButton = (Button) findViewById(R.id.fadd_one);
		mTwoButton = (Button) findViewById(R.id.fadd_two);
		mThreeButton = (Button) findViewById(R.id.fadd_three);
		
		mItemStatus = (TextView) findViewById(R.id.fadd_status);
		
		mOneButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Item item = new Item("add1","fake add 1","1.99");
						addItem(item);
					}
				});

		mTwoButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Item item = new Item("add2","fake add 2","7.99");
						addItem(item);
					}
				});
		
		mThreeButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Item item = new Item("add3","fake add 3","100");
						addItem(item);
					}
				});
	}
	
	@Override
	int getContentView() {
		return 0;
	}

	@Override
	int getMainView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getSpinnerView() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void addItem(Item item) {
		if (mFakeTask != null) return;
		
		showProgress(true);
		mFakeTask = new FakeAddItemActivityTask();
		mFakeTask.execute(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fakelogin, menu);
		return true;
	}

	/*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mFakeStatusView.setVisibility(View.VISIBLE);
			mFakeStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mFakeStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mFakeView.setVisibility(View.VISIBLE);
			mFakeView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mFakeView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mFakeStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mFakeView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
*/
	/**
	 * Represents an asynchronous task.  In this case, one that makes a network
	 * call to resend a customer a receipt.
	 * 
	 */
	private class FakeAddItemActivityTask extends AsyncTask<Item, String, Integer> {
		@Override
		protected Integer doInBackground(Item... item) {
			// Access to POS storage object to do work of resending receipt
			Storage storage = getStorage();
			try {
				storage.addItem(item[0]);
				return Activity.RESULT_OK;
			} catch (ConnectionError e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return Activity.RESULT_CANCELED;
			} catch (NoUserFoundException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return Activity.RESULT_CANCELED;
			} catch (BadPasswordException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return Activity.RESULT_CANCELED;
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return Activity.RESULT_CANCELED;
			}
			
		}

		@Override
		protected void onPostExecute(final Integer success) {
			Log.e(LOG_TAG,"POST EXECUTE");
			mFakeTask = null;
			//showProgress(false);
			setResult(success.intValue());
			if (success == Activity.RESULT_OK) {
				finish();
				return;
			} else {
				showProgress(false);
				mItemStatus.setText(mStatusMessage);
			}
		}
		
		@Override
		protected void onProgressUpdate(String... progress) {

	    }

		@Override
		protected void onCancelled() {
			mFakeTask = null;
			showProgress(false);
		}
	}

	@Override
	void netStatusUpdate() {
		// TODO Auto-generated method stub
		
	}


}
