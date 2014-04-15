package edu.txstate.pos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.InvalidCartException;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;

/**
 * Activity that allows for resending a receipt to a customer.
 */
public class ResendActivity extends POSActivity {

	private static final String LOG_TAG = "ResendActivity";
	
	// UI references.
	private EditText mEmailView;
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mEmailView = (EditText) findViewById(R.id.email);

		findViewById(R.id.resend_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						resendReceipt();
					}
				});
	}
	
	private void resendReceipt() {
		email = mEmailView.getText().toString();
		if (email != null) {
			executeAsyncTask("GetCartTask", 
					new GetCartTask("GetCartTask",this), 
					true, 
					(POSModel) null);
		}
	}

	@Override
	int getContentView() {
		return R.layout.activity_resend;
	}
	
	@Override
	int getMainView() {
		return R.id.resend_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.spinner;
	}
	
	@Override
	void netStatusUpdate() {
		
	}
	
	public class GetCartTask extends POSTask<Cart> {
		public GetCartTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		Cart backgroundWork(Storage storage, POSModel... args) {
			Cart cart = null;
			try {
				cart = ((POSApplication) getApplication()).getLastCart(email);
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				cart = null;
			} catch (NoCartFoundException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				cart = null;
			}
			return cart;
		}

		@Override
		void postWork(Storage storage, Cart workResult) {
			if (workResult != null) {
				((POSApplication) getApplication()).getHome().sendEmail(workResult);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
		}
		
		
	}
}
