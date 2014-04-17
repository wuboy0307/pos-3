package edu.txstate.pos;

import com.nabancard.sdkadvanced.CustomizeSDKAdvanced;
import com.nabancard.sdkadvanced.SDKAdvancedCallbacks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.CartItem;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.model.Payment;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;


public class CartActivity extends POSFragmentActivity implements SDKAdvancedCallbacks {

	private static final String LOG_TAG = "CartActivity";
	
	public static final int MANUAL_PAYMENT = 1;
	public static final int CARDSWIPE_PAYMENT = 2;
	
	public static final String EXTRA_FIELD_CARD = "card";
	public static final String EXTRA_FIELD_PIN = "pin";
	
	CustomizeSDKAdvanced instance = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			instance = CustomizeSDKAdvanced.getInstance(
					CartActivity.this, 
					(SDKAdvancedCallbacks) this
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
			
			instance.setEmailReceiptEnabled(false);
			instance.setSignatureScreenEnabled(true);
	}
	
	private final static String pa_mid = "8788290228799021",
			pa_login_id = "206318",
			pa_username = "paya3928",
			pa_password = "2014Test",
			pa_MyAppName = "Mobile Point of Sales";
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG,"onActivityResult");
		if (resultCode == RESULT_OK) {
			switch(requestCode) {
				case MANUAL_PAYMENT:
							Log.d(LOG_TAG,"MANUAL");
							String card = "card";
							Payment payment = new Payment(card);
							sell(payment);
							break;
			}
		}
	}
	
	public void swipe(String total, String cartID) {
		instance.setAmount(total);
		String deviceID = ((POSApplication) getApplication()).getDeviceID();
		instance.setInvoice(deviceID + "-" + cartID);
		instance.showChargeScreen();
	}

	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment =  new CartFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment<CartItem> getListFragment() {
		mListFragment = new CartItemListFragment();
		return mListFragment;
	}

	public void deleteSelectedItem() {
		mListFragment.deleteSelectedItem();
	}

	@Override
	int getMainView() {
		return R.id.fragmentContainer;
	}

	@Override
	int getSpinnerView() {
		return R.id.fragment_spinner;
	}
	
	private void sell(Payment payment) {
		executeAsyncTask("SellCartTask", 
				new SellCartTask("SellCartTask",this), true, payment);
		
		//((POSApplication) getApplication()).getHome().sendEmail();
		
		try {
			Cart cart = ((POSApplication) getApplication()).getCart();
			((POSApplication) getApplication()).getHome().sendEmail(cart);
		} catch (StorageException e) {
			Toast.makeText(this, 
					"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	
	}
	
	

	/************************************************/
	/*	Pay Anywhere Integration                   */
	/***********************************************/
	
	/**
	 * 
	 */
	@Override
	public void approvedTransaction(String message, 
									String pnr, 
									String transaction_type,
									String invoice_number, 
									String card_type) {
		
		
		Payment payment = new Payment(pnr,card_type);
		sell(payment);
	}

	@Override
	public void cancelledTransaction() {
		//Log.d(LOG_TAG,"CANCELLED");
		((CartFieldFragment) mFieldFragment).setPaymentMessage("Cancelled");
	}

	@Override
	public void declinedTransaction(String message, String reason) {
		//Log.d(LOG_TAG,"DECLINED");
		((CartFieldFragment) mFieldFragment).setPaymentMessage("Declined");
	}

	@Override
	public void mostRecentEmailTransaction(String message) {

	}
	
	public class SellCartTask extends POSTask<Payment> {
		public SellCartTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		Payment backgroundWork(Storage storage, POSModel... args) {
			Payment ret = null;
			try {
				Cart cart = ((POSApplication) getApplication()).getCart();
				cart.addPayment((Payment) args[0]);
				cart.sell();
				ret = (Payment) args[0];
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			return ret;
		}

		@Override
		void postWork(Storage storage, Payment workResult) {
			if (workResult != null) {
				((POSApplication) getApplication()).sellCart(); 
				finish();
			} else {
				Toast.makeText(CartActivity.this, 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
		}
		
	}

}
