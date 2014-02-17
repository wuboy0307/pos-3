package edu.txstate.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;

public class MainActivity extends POSActivity {

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
	
	public void login(View view) {
		String androidID = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID); 
		Storage storage = getStorage();
		
		//get userID from EditText
		EditText inputUserID = (EditText) findViewById(R.id.editText1);
		String userID = inputUserID.getText().toString();
		
		//get user pin from EditText
		EditText inputUserPIN = (EditText) findViewById(R.id.editText2);
		String pin = inputUserPIN.getText().toString();
		
		User user = new User(userID,pin);
		
		int numRetries = 0;
		try {
			storage.login(user);
			// if you get here, it worked
//TODO: launch new activity with user name o

			
		} catch (NoUserFoundException e) {
			// if bad username, then do something here
			
			if(numRetries > 3)
			{
//TODO: exceeded number of retries, freeze login for 30 sec?
			}
			else
			{
//TODO: display error msg "User ID or PIN is invalid"
			}
			
			++numRetries; //inc # retries
			
		} catch (BadPasswordException e) {
			// if bad password, then do something here
			
			if(numRetries > 3)
			{
//TODO: exceeded number of retries, freeze login for 30 sec?
			}
			else
			{
//TODO: display error msg "User ID or PIN is invalid"
			}
			
			++numRetries; //inc # of retries
		} catch (ConnectionError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clear(View view) {
		// do whatever when clear button is hit
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent = null;
	    switch (item.getItemId()) {
	        case R.id.action_search:
	            intent = new Intent(this, SplashActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.action_resend:
	            intent = new Intent(this, ResendActivity.class);
	            startActivity(intent);
	            return true;	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
