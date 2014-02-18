package edu.txstate.pos;

import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;
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

public class FakeloginActivity extends POSActivity {

	private FakeLoginActivityTask mFakeTask = null;
	
	private View mFakeStatusView = null;
	private View mFakeView = null;
	
	private Button mOneButton = null;
	private Button mTwoButton = null;
	private Button mThreeButton = null;
	
	private TextView mLoginStatus = null;
	private String mStatusMessage = "Select a user";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fakelogin);
	
		mFakeView = findViewById(R.id.flogin_form);
		mFakeStatusView = findViewById(R.id.flogin_spinner);
		
		mOneButton = (Button) findViewById(R.id.flogin_one);
		mTwoButton = (Button) findViewById(R.id.flogin_two);
		mThreeButton = (Button) findViewById(R.id.flogin_three);
		
		mLoginStatus = (TextView) findViewById(R.id.flogin_status);
		
		mOneButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						User user = new User("geoff","5555");
						fakeLogin(user);
					}
				});

		mTwoButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						User user = new User("binh","1234");
						fakeLogin(user);
					}
				});
		
		mThreeButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						User user = new User("pham","4321");
						fakeLogin(user);
					}
				});
	}

	public void fakeLogin(User user) {
		if (mFakeTask != null) return;
		
		showProgress(true);
		mFakeTask = new FakeLoginActivityTask();
		mFakeTask.execute(user);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fakelogin, menu);
		return true;
	}

	/**
	 * Shows the progress UI and hides the form.
	 */
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

	/**
	 * Represents an asynchronous task.  In this case, one that makes a network
	 * call to resend a customer a receipt.
	 * 
	 */
	private class FakeLoginActivityTask extends AsyncTask<User, String, Integer> {
		@Override
		protected Integer doInBackground(User... user) {
			// Access to POS storage object to do work of resending receipt
			Storage storage = getStorage();
			try {
				User newUser = storage.login(user[0]);
				((POSApplication) getApplication()).setUser(newUser);
				return Activity.RESULT_OK;
			} catch (ConnectionError e) {
				mStatusMessage = e.getMessage();
				Log.e("FAKELOGIN_ACTIVITY",e.getMessage());
				return Activity.RESULT_CANCELED;
			} catch (NoUserFoundException e) {
				mStatusMessage = e.getMessage();
				Log.e("FAKELOGIN_ACTIVITY",e.getMessage());
				return Activity.RESULT_CANCELED;
			} catch (BadPasswordException e) {
				mStatusMessage = e.getMessage();
				Log.e("FAKELOGIN_ACTIVITY",e.getMessage());
				return Activity.RESULT_CANCELED;
			}
			
		}

		@Override
		protected void onPostExecute(final Integer success) {
			Log.e("FLOGIN_ACTIVITY","POST EXECUTE");
			mFakeTask = null;
			//showProgress(false);
			setResult(success.intValue());
			if (success == Activity.RESULT_OK) {
				finish();
				return;
			} else {
				mLoginStatus.setText(mStatusMessage);
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
	
}
