package edu.txstate.pos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

public class POSControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poscontrol);
		
		Intent intent = getIntent();
		
		String message = intent.getStringExtra(MainActivity.USER_ID);
		message += ", " + intent.getStringExtra(MainActivity.USER_PIN);
		
		TextView textView = new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);
		
		setContentView(textView);
	}
}
