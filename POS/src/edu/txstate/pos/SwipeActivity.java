package edu.txstate.pos;

import com.nabancard.sdkadvanced.CustomizeSDKAdvanced;
import com.nabancard.sdkadvanced.SDKAdvancedCallbacks;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SwipeActivity extends Activity {
	private final static String pa_mid = "8788290228799021",
			pa_login_id = "206318",
			pa_username = "paya3928",
			pa_password = "2014Test",
			pa_MyAppName = "Mobile Point of Sales";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);
		/*
		CustomizeSDKAdvanced instance = CustomizeSDKAdvanced.getInstance(
				this, 
				this
			);
		instance.setMerchantId(pa_mid);
		instance.setLoginId(pa_login_id);
		instance.setUsername(pa_username);
		instance.setPassword(pa_password);
		instance.setApplicationName(pa_MyAppName);
		instance.setPortraitBackgroundDrawable(
		getResources().getDrawable(R.drawable.back)
		);
		
		instance.setMerchantLogoPortraitDrawable(
		getResources().getDrawable(R.drawable.ic_pos_app)
		);
		
		instance.setBackButtonDrawable(
		getResources().getDrawable(R.drawable.back), 
		getResources().getDrawable(R.drawable.back_hover)
		);
		
		instance.setEmailReceiptEnabled(true);
		instance.setSignatureScreenEnabled(true);
		instance.setAmount(mTotal.getText().toString());
		String deviceID = getPOSApplication().getDeviceID();
		instance.setInvoice(deviceID + "-" + String.valueOf(cart.getId()));
		instance.showChargeScreen();
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.swipe, menu);
		return true;
	}

}
