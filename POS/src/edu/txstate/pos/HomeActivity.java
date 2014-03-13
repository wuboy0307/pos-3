package edu.txstate.pos;

import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends POSActivity {

	private static final int FAKE_LOGIN_RESPONSE = 0;
	
	private HomeAsync mHomeAsync = null;
	
	private View mHomeStatusView = null;
	private View mHomeView = null;
	
	private TextView mMessage = null;
	private Button mLoginButton = null;
	private Button mCheckoutButton = null;
	private Button mPriceCheckButton = null;
	private Button mResendReceiptButton = null;
	private Button mUserAdminButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mHomeView = findViewById(R.id.home_form);
		mHomeStatusView = findViewById(R.id.home_spinner);
		
		if (mHomeAsync == null) {
			showProgress(true);
			mHomeAsync = new HomeAsync();
			mHomeAsync.execute(getBaseContext());
		}
	
		mMessage = (TextView) findViewById(R.id.login_message);
		mLoginButton = (Button) findViewById(R.id.login_button);
		mCheckoutButton = (Button) findViewById(R.id.checkout_button);
		mPriceCheckButton = (Button) findViewById(R.id.pricecheck_button);
		mResendReceiptButton = (Button) findViewById(R.id.resendrec_button);
		mUserAdminButton = (Button) findViewById(R.id.admin_button);
		
		mLoginButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), FakeloginActivity.class);
						startActivityForResult(intent, FAKE_LOGIN_RESPONSE);
					}
				});
		
		mCheckoutButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
		
		mPriceCheckButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), ScanActivity.class);
						startActivity(intent);
					}
				});
		
		mResendReceiptButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), FakeAddItem.class);
						startActivity(intent);
					}
				});
		
		mUserAdminButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});

		setButtonsAndText();
		
	}
	
	private void setButtonsAndText() {
		if (isLoggedIn()) {
			mMessage.setText("Welcome, " + getUser().getLogin());
			mLoginButton.setEnabled(true);
			mLoginButton.setText("Logout");
			mCheckoutButton.setEnabled(true);
			mPriceCheckButton.setEnabled(true);
			mResendReceiptButton.setEnabled(true);
			if (getUser().isAdmin()) {
				mUserAdminButton.setEnabled(true);
			} else {
				mUserAdminButton.setEnabled(false);
			}
		} else {
			mMessage.setText("Please Log in");
			mLoginButton.setEnabled(true);
			mLoginButton.setText("Login");
			mCheckoutButton.setEnabled(false);
			mPriceCheckButton.setEnabled(false);
			mResendReceiptButton.setEnabled(false);
			mUserAdminButton.setEnabled(false);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == FAKE_LOGIN_RESPONSE) {
	        if (resultCode == RESULT_OK) {
	        	setButtonsAndText();
	        }
	    }
	}
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mHomeStatusView.setVisibility(View.VISIBLE);
			mHomeStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mHomeStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mHomeView.setVisibility(View.VISIBLE);
			mHomeView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mHomeView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mHomeStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mHomeView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private class HomeAsync extends AsyncTask<Context, String, Integer> {

		@Override
		protected Integer doInBackground(Context... context) {
			/*
			try {
				// Simulate long network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return 0;
			}*/
			
		    Storage storage = getStorage();

	        try {
	        	Item item = new Item("001AA",
	    				"Junit Test Item 001AAA",
	    				"11.99");
	        	storage.addItem(item);
	        	Log.e("HOME_ACTIVITY", "ADDED ITEM");
				//storage.syncItems();
			} catch (ConnectionError e) {
				Log.e("HOME_ACTIVITY",e.getMessage());
			} catch (StorageException e) {
				Log.e("HOME_ACTIVITY",e.getMessage());
			}
			Log.e("HOME_ACTIVITY","DO IN BACKGROUD DONE");
	        return 1;
		}
		
		@Override
	    protected void onProgressUpdate(String... progress) {
	    	Log.d("HOME_ACTIVITY","PROGRESS UPDATE");
	    }

	    @Override
		protected void onPostExecute(final Integer sucess) {
			mHomeAsync = null;
	    	showProgress(false);
	    	Log.d("HOME_ACTIVITY","POST EXECUTE");
	    }
	    
		@Override
		protected void onCancelled() {
			mHomeAsync = null;
			showProgress(false);
			Log.d("HOME_ACTIVITY","CANCELLED");
		}

	}

}
