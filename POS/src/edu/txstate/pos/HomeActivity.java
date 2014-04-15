package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends POSActivity {

	private static final String LOG_TAG = "HomeActivity";
	private static final int LOGIN_RESPONSE = 0;
	
	private TextView mMessage = null;
	private Button mLoginButton = null;
	private Button mCheckoutButton = null;
	private Button mPriceCheckButton = null;
	private Button mResendReceiptButton = null;
	private Button mInventoryButton = null;
	private Button mUserAdminButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mMessage = (TextView) findViewById(R.id.login_message);
		mLoginButton = (Button) findViewById(R.id.login_button);
		mCheckoutButton = (Button) findViewById(R.id.checkout_button);
		mPriceCheckButton = (Button) findViewById(R.id.pricecheck_button);
		mInventoryButton = (Button) findViewById(R.id.inventory_button);
		mResendReceiptButton = (Button) findViewById(R.id.resendrec_button);
		mUserAdminButton = (Button) findViewById(R.id.admin_button);
		
		mLoginButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (isLoggedIn()) {
							((POSApplication) getApplication()).logUserOut();
							setButtonsAndText();
						} else {
							Intent intent = new Intent(getBaseContext(), LoginActivity.class);
							startActivityForResult(intent, LOGIN_RESPONSE);
						}
					}
				});
		
		mCheckoutButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), CartActivity.class);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
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
		
		mInventoryButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), InventoryActivity.class);
						startActivity(intent);
					}
				});
		
		mResendReceiptButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), ResendActivity.class);
						startActivity(intent);
					}
				});
		
		mUserAdminButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getBaseContext(), UserAdminActivity.class);
						startActivity(intent);
					}
				});

		setButtonsAndText();
		setPing();
		((POSApplication) getApplication()).setHome(this);
	}
	
	public void sendEmail(Cart cart) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, "geoff@marinski.com");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Mobile POS Receipt");
		intent.putExtra(Intent.EXTRA_TEXT, "Total");
		Intent mailer = Intent.createChooser(intent, null);
		startActivity(mailer);
	}
	
	@Override
	int getContentView() {
		return R.layout.activity_home;
	}
	
	@Override
	int getMainView() {
		return R.id.home_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.home_spinner;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setButtonsAndText();
		setPing();
	}
	
	@Override
	void netStatusUpdate() {
		if(!mNetworkAvailable) mUserAdminButton.setEnabled(false);
		else if (isLoggedIn() && getUser().isAdmin()) mUserAdminButton.setEnabled(true);
	}
		
	private void setButtonsAndText() {
		if (isLoggedIn()) {
			mMessage.setText("Welcome, " + getUser().getLogin());
			mLoginButton.setEnabled(true);
			mLoginButton.setText("Logout");
			mCheckoutButton.setEnabled(true);
			mPriceCheckButton.setEnabled(true);
			mInventoryButton.setEnabled(true);
			mResendReceiptButton.setEnabled(true);
			if (getUser().isAdmin() && mNetworkAvailable) {
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
			mInventoryButton.setEnabled(false);
			mResendReceiptButton.setEnabled(false);
			mUserAdminButton.setEnabled(false);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"----------------------- ON RESULT!!!!!");
		if (resultCode == RESULT_OK) {
        	setButtonsAndText();
        }
	}

}
