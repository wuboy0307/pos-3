package edu.txstate.pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;

public class MainActivity extends POSActivity {

	public final static String USER_ID = "edu.txstate.pos.USER_ID";
	public final static String USER_PIN = "edu.txstate.pos.USER_PIN";
//TODO need to hide offline admin information more securely
	private final static User offlineAdmin = new User("123456", "1234");
	public static int numRetries = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void login(View view) {
		String androidID = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID); 
		Storage storage = getStorage();
		
		//get userID and userPIN from EditText from MainActivity
		EditText editTextUserId = (EditText) findViewById(R.id.editTextUserID);
		EditText editTextUserPin = (EditText) findViewById(R.id.editTextUserPIN);
		
		//convert editText to a string
		String userID = editTextUserId.getText().toString();
		String pin = editTextUserPin.getText().toString();
		
		User user = new User(userID,pin);
		
		
/*TEST SECTION***********
		Intent intent = new Intent (this, POSControl.class);
		intent.putExtra(USER_ID, userID);
		intent.putExtra(USER_PIN, pin);
		startActivity(intent);
*************************/
		
		AlertDialog alertDialogBadLogin = new AlertDialog.Builder(MainActivity.this).create();

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
		});

		// Showing Alert Message
		alertDialogBadLogin.show();
		
		try {
			User validUser = storage.login(user);
			// if you get here, it worked
//TODO: launch new activity with user name displayed on ActionBar
			

			Intent intentPOSControl = new Intent (this, POSControl.class);
			intentPOSControl.putExtra(USER_ID, validUser.getId());
			startActivity(intentPOSControl);
			
		} catch (NoUserFoundException e) {
			// if bad username, then do something here
			
			if(numRetries > 3)
			{
//TODO: exceeded number of retries, freeze login for 30 sec?
			}
			else
				alertDialogBadLogin.show();
			
			++numRetries; //inc # retries
			
		} catch (BadPasswordException e) {
			// if bad password, then do something here
			
			if(numRetries > 3)
			{
//TODO: exceeded number of retries, freeze login for 30 sec?
			}
			else
				alertDialogBadLogin.show();
			
			++numRetries; //inc # of retries
			
		} catch (ConnectionError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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


	public void openScanActivity(View view) {
		// Opens the activity_scan Activity to do some scanning stuff
		Intent intent = new Intent(this, ScanActivity.class);
		startActivity(intent);



	}
	
}
