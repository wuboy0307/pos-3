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
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;

public class LoginActivity extends POSActivity {

	private static final String LOG_TAG = "Login";
	
	public final static String USER_ID = "edu.txstate.pos.USER_ID";
	public final static String USER_PIN = "edu.txstate.pos.USER_PIN";
	// TODO: need to hide offline admin information more securely
	private final static User offlineAdmin = new User("123456", "1234");
	public static int numRetries = 0;
	private String mStatusMessage = "Bad user name or password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}

	@Override
	int getContentView() {
		return R.layout.activity_login;
	}
	
	@Override
	int getMainView() {
		return R.id.login_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.login_spinner;
	}
	
	@SuppressWarnings("deprecation")
	public void login(View view) {
		//get userID and userPIN from EditText from MainActivity
		EditText editTextUserId = (EditText) findViewById(R.id.editTextUserID);
		EditText editTextUserPin = (EditText) findViewById(R.id.editTextUserPIN);
		
		//convert editText to a string
		String userID = editTextUserId.getText().toString();
		String pin = editTextUserPin.getText().toString();
		
		Log.i("login_test", "101010");
		
		User user = new User(userID,pin);	
		
		executeAsyncTask("LoginTask",new LoginActivityTask("LoginTask",this), true, user);
		
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
	 * Represents an asynchronous task.  In this case, one that makes a network
	 * call to resend a customer a receipt.
	 * 
	 */
	public class LoginActivityTask extends POSTask<Integer> {
		
		public LoginActivityTask(String name, POSTaskParent parent) {
			super(name,parent);
		}
		
		@Override
		protected Integer backgroundWork(Storage storage, POSModel... args) {
			// Access to POS storage object to do work of resending receipt
			try {
				User newUser = storage.login((User) args[0]);
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
		protected void postWork(Storage storage, final Integer success) {
			Log.e(LOG_TAG,"POST EXECUTE");
			if (success == Activity.RESULT_OK) {
				finish();
				return;
			} else {
				AlertDialog alertDialogBadLogin = new AlertDialog.Builder(LoginActivity.this).create();
				// Setting Dialog Title
				alertDialogBadLogin.setTitle("Alert");
				// Setting Dialog Message
				alertDialogBadLogin.setMessage("User ID or PIN is invalid: " + mStatusMessage);
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

	}
}
