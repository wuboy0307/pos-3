package edu.txstate.pos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;

public class MainActivity extends POSActivity {

	private static final String LOG_TAG = "Login";
	private LoginActivityTask mLoginTask = null;
	
	public final static String USER_ID = "edu.txstate.pos.USER_ID";
	public final static String USER_PIN = "edu.txstate.pos.USER_PIN";
//TODO need to hide offline admin information more securely
	private final static User offlineAdmin = new User("123456", "1234");
	public static int numRetries = 0;
	
	private View mFakeStatusView = null;
	private View mFakeView = null;
	
	private TextView mLoginStatus = null;
	private String mStatusMessage = "Bad user name or password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mFakeView = findViewById(R.id.login_form);
		mFakeStatusView = findViewById(R.id.login_spinner);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void login(View view) {
		if (mLoginTask != null) return;
		
		// Show the spinner
		showProgress(true);
		
		//get userID and userPIN from EditText from MainActivity
		EditText editTextUserId = (EditText) findViewById(R.id.editTextUserID);
		EditText editTextUserPin = (EditText) findViewById(R.id.editTextUserPIN);
		
		//convert editText to a string
		String userID = editTextUserId.getText().toString();
		String pin = editTextUserPin.getText().toString();
Log.i("login_test", "101010");
		User user = new User(userID,pin);	
		
		mLoginTask = new LoginActivityTask();
		mLoginTask.execute(user);
		
/*TEST SECTION***********
		Intent intent = new Intent (this, POSControl.class);
		intent.putExtra(USER_ID, userID);
		intent.putExtra(USER_PIN, pin);
		startActivity(intent);
*************************/
		
//****CREATE alert dialog for bad login
/*		AlertDialog alertDialogBadLogin = new AlertDialog.Builder(MainActivity.this).create();
		// Setting Dialog Title
		alertDialogBadLogin.setTitle("Alert");
		// Setting Dialog Message
		alertDialogBadLogin.setMessage("User ID or PIN is invalid.");
		// Setting Icon to Dialog
		alertDialogBadLogin.setIcon(R.drawable.ic_action_bad);
		// Setting OK Button
		alertDialogBadLogin.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				Toast.makeText(getApplicationContext(), "Please verify User ID and PIN.", Toast.LENGTH_SHORT).show();
			}
		});  */
//****End of alert dialog
		/*
		try {
			User validUser = storage.login(user);			
Log.e("MainAct_login", "inside login");

			Intent intentPOSControl = new Intent (this, POSControl.class);
			intentPOSControl.putExtra(USER_ID, validUser.getId());
			startActivity(intentPOSControl);
			
		} catch (NoUserFoundException e) {
			alertDialogBadLogin.show();
		} catch (BadPasswordException e) {
			alertDialogBadLogin.show();
		} catch (ConnectionError e) {
			e.printStackTrace();
		}
	*/
	}

	
/**
 * @author Binh
 * @param view
 */
	public void offline_login(View view) {		
		//convert user id and pin to editable text
		EditText editTextUserId = (EditText) findViewById(R.id.editTextUserID);
		EditText editTextUserPin = (EditText) findViewById(R.id.editTextUserPIN);
		
		//convert editText to string
		String UserID = editTextUserId.getText().toString();
		String UserPIN = editTextUserPin.getText().toString();
		
		//pass user info to user object
		User user = new User(UserID, UserPIN);
			
		if(user.getLogin() == offlineAdmin.getLogin() && user.getPIN() == offlineAdmin.getPIN()) {

			Intent intent = new Intent (this, OfflineLogin.class);
			
			intent.putExtra(USER_ID, user.getLogin());
			intent.putExtra(USER_PIN, user.getId());
			
			startActivity(intent);
		}
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
	private class LoginActivityTask extends AsyncTask<User, String, Integer> {
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
			}
			
		}

		@Override
		protected void onPostExecute(final Integer success) {
			Log.e(LOG_TAG,"POST EXECUTE");
			mLoginTask = null;
			//showProgress(false);
			setResult(success.intValue());
			if (success == Activity.RESULT_OK) {
				finish();
				return;
			} else {
				showProgress(false);
				//Toast.makeText(getApplicationContext(), "Please verify User ID and PIN.", Toast.LENGTH_SHORT).show();
				
				AlertDialog alertDialogBadLogin = new AlertDialog.Builder(MainActivity.this).create();
				// Setting Dialog Title
				alertDialogBadLogin.setTitle("Alert");
				// Setting Dialog Message
				alertDialogBadLogin.setMessage("User ID or PIN is invalid.");
				// Setting Icon to Dialog
				alertDialogBadLogin.setIcon(R.drawable.ic_action_bad);
				// Setting OK Button
				alertDialogBadLogin.setButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
					}
				  });
				alertDialogBadLogin.show();
				
			}
		}
		
		@Override
		protected void onProgressUpdate(String... progress) {

	    }

		@Override
		protected void onCancelled() {
			mLoginTask = null;
			showProgress(false);
		}
	}
}
