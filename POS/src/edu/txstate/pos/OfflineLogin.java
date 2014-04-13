package edu.txstate.pos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

public class OfflineLogin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_login);
		
		Intent intent = getIntent();
		
		String userId = intent.getStringExtra(LoginActivity.USER_ID);
		String userPin = intent.getStringExtra(LoginActivity.USER_PIN);
		
//***TEST
TextView userIdView = new TextView(this);
userIdView.setTextSize(20);
userIdView.setText(userId);

TextView userPinView = new TextView(this);
userPinView.setTextSize(20);
userPinView.setText(userPin);

setContentView(userIdView);
setContentView(userPinView);
//***TEST
	}
}
