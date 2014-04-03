package edu.txstate.pos;

import com.nabancard.sdkadvanced.CustomizeSDKAdvanced;
import com.nabancard.sdkadvanced.SDKAdvancedCallbacks;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.EditText;

public class PayAnywhereTestActivity extends Activity implements SDKAdvancedCallbacks {

	private CustomizeSDKAdvanced instance = null;
	//private String mAppName = "Mobile Point of Sale";
	private static String alertMessage = "";
	private static boolean showAlert = false;
	private static String app_name = "Mobile Point-Of-Sale";
	

	private final static String pa_mid = "<8788290228799-021>",
								pa_login_id = "<206318>",
								pa_username = "<paya3928>",
								pa_password = "<2014Test>",
								pa_MyAppName = "Mobile Point of Sales";

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_anywhere_test);
		
		initializeVariables();
		customizeSDKAdvanced();
	}
	
	private void customizeSDKAdvanced(){
		instance = CustomizeSDKAdvanced.getInstance(PayAnywhereTestActivity.this, (SDKAdvancedCallbacks)this);
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
		//instance.setAmount(pa_chargeAmt);
		instance.showChargeScreen();
	}
	
	private void initializeVariables(){
		Button charge_amt_b = (Button) findViewById(R.id.charge_amt_b);
		//Button new_preauth_b = (Button) findViewById(R.id.new_preauth_b);
        //Button current_preauth_b = (Button) findViewById(R.id.current_preauth_b);
        //Button recent_b = (Button) findViewById(R.id.recent_b);
        //Button void_b = (Button) findViewById(R.id.void_b);
        //Button refund_b = (Button) findViewById(R.id.refund_b);
		
		//TODO FIX below
		charge_amt_b.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view){
						if(view !=null){
							instance.setAmount(((EditText)findViewById(R.id.charge_amt_et)).getText().toString());
							instance.setInvoice(((EditText)findViewById(R.id.invoice_et)).getText().toString());
							instance.showChargeScreen();
						}
					}
				}
		);
		
        //new_preauth_b.setOnClickListener(clickListener);
        //current_preauth_b.setOnClickListener(clickListener);
        //recent_b.setOnClickListener(clickListener);
        //void_b.setOnClickListener(clickListener);
        //refund_b.setOnClickListener(clickListener); 
	}

	/*
	OnClickListener clickListener = new OnClickListener(){
		public void onClick(View v){
			if(v != null){
				switch(v.getId()){
					case R.id.charge_amt_b:
						instance.setAmount(((EditText)findViewById(R.id.charge_amt_et)).getText().toString());
						instance.setInvoice(((EditText)findViewById(R.id.invoice_et)).getText().toString());
						instance.showChargeScreen();
						break;
	//				case R.id.new_preauth_b:
	//					instance.setAmount(((EditText)findViewById(R.id.enter_amount_et)).getText().toString());
	//					instance.setInvoice(((EditText)findViewById(R.id.invoice_et)).getText().toString());
	//					instance.showNewPreauthScreen();
	//					break;
	//				case R.id.current_preauth_b:
	//					instance.showCurrentPreauthScreen();
	//					break;
	//				case R.id.recent_b:
	//					instance.showRecentTransactionsScreen();
	//					break;
	//				case R.id.void_b:
	//					instance.showVoidScreen();
	//					break;
	//				case R.id.refund_b:
	//					instance.showRefundScreen();
	//					break;
					default:
						break;
				}
			}
		}
	};
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay_anywhere_test, menu);
		return true;
	}

	@Override
	public void approvedTransaction(String message, 
			String pnr, 
			String transaction_type,
			String invoice_number, 
			String card_type) {

		if (pnr!=null)
		{
			alertMessage = message+"\nPnref: "+pnr+"\nInvoice: "+invoice_number+"\nCard Type: "+card_type;
			showAlert=true;
		}
		else
		{
			alertMessage = message;
			showAlert=true;
		}
		Log.i("TestApp", "message1 = "+message);
		
	}

	@Override
	public void cancelledTransaction() {
		alertMessage = "Transaction Cancelled";
		showAlert=true;
		Log.i("TestApp", "message4 = Transaction Cancelled");
		
	}

	@Override
	public void declinedTransaction(String message, String reason) {
		alertMessage = message+"\n"+reason;
		showAlert=true;
		Log.i("TestApp", "message2 = "+message);
		
	}

	@Override
	public void mostRecentEmailTransaction(String message) {
		alertMessage = message;
		showAlert=true;
		Log.i("TestApp", "message3 = "+message);
		
	}
	
	@SuppressWarnings("deprecation")
	private void showAlertDialog()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(PayAnywhereTestActivity.this).create();
		alertDialog.setTitle(app_name);
		alertDialog.setMessage(alertMessage);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which){}
		});
		try
		{
			alertDialog.show();
		}
		catch (BadTokenException bte)
		{
			Log.i("TestApp", "exception thrown in testscreen \"showAlertDialog()\""+bte.getMessage());
		}
		catch (Exception e)
		{
			Log.i("TestApp", "catch (Exception e) = "+e.getMessage());
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (showAlert==true)
		{
			showAlertDialog();
			showAlert=false;
		}
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		instance.stopHeadSetService();
	}

}
