package edu.txstate.pos;

import com.nabancard.sdkadvanced.CustomizeSDKAdvanced;
import com.nabancard.sdkadvanced.SDKAdvancedCallbacks;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static edu.txstate.pos.CartActivity.*;

public class CartFieldFragment extends POSFieldFragment {

	private final String LOG_TAG = "CART_FRAG";
	
	private EditText mCustomerEmail;
	private TextView mSubtotal;
	private TextView mTax;
	private TextView mTotal;
	private TextView mPayMessage;
	
	private Button mAddItemButton;
	private Button mManualButton;
	private Button mSwipeButton;
	private Button mCancelButton;
	
	private Cart cart;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			cart = getCart();
		} catch (StorageException e) {
			Toast.makeText(getActivity().getApplicationContext(), 
					"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateFields();
	}

	public void updateFields() {
		Log.d(LOG_TAG,"Cart ID: " + cart.getId());
		mCustomerEmail.setText(cart.getCustomer());
		mSubtotal.setText(cart.getSubTotal());
		mTax.setText(cart.getTaxAmount());
		mTotal.setText(cart.getTotal());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cart_fields, parent, false);
		
		mCustomerEmail = (EditText) v.findViewById(R.id.customer_email);
		mSubtotal = (TextView) v.findViewById(R.id.subtotal);
		mTax = (TextView) v.findViewById(R.id.tax);
		mTotal = (TextView) v.findViewById(R.id.total);
		mPayMessage = (TextView) v.findViewById(R.id.payment);
		
		mAddItemButton = (Button) v.findViewById(R.id.cart_add_item_button);
		mManualButton = (Button) v.findViewById(R.id.cart_manual_button);
		mSwipeButton = (Button) v.findViewById(R.id.cart_swipe_button);
		mCancelButton = (Button) v.findViewById(R.id.cart_cancel_button);
		
		updateFields();
		
		mCustomerEmail.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence c, int start, int before, int count) {
						try {
							getCart().setCustomer(c.toString());
						} catch (StorageException e) {
							Log.e(LOG_TAG, e.getMessage());
						}
					}
					
					public void beforeTextChanged(CharSequence c, int start, int count, int after) {
						// do nothing
					}
					
					public void afterTextChanged(Editable c) {
						// do nothing
					}
				});
		
		
		mAddItemButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity().getBaseContext(), AddCartItemActivity.class);
						getActivity().startActivity(intent);
					}
				});

		mManualButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity().getBaseContext(), ManualPayActivity.class);
						getActivity().startActivityForResult(intent, MANUAL_PAYMENT);
					}
				});

		
		mSwipeButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						swipe();
					}
				});
		
		mCancelButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						cancelCart();
					}
				});
		
		return v;
	}
	
	private void cancelCart() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Delete cart?")
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancel();
				dialog.dismiss();
			}
		})
		.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void cancel() {
		parent.executeAsyncTask("CancelCart", 
				new CancelCartTask("CancelCart", parent), true, (POSModel) null);
	}
	
	private void swipe() {
		String total = mTotal.getText().toString();	
		((CartActivity) parent).swipe(total, String.valueOf(cart.getId()));
	}
	
	public void setPaymentMessage(String message) {
		mPayMessage.setText("Payment: " + message);
	}
	
	public class CancelCartTask extends POSTask<Cart> {
		public CancelCartTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		Cart backgroundWork(Storage storage, POSModel... args) {
			// This one works a little backwards.  Return
			// a cart object if it didn't work, null if it did.
			Cart ret = null;
			try {
				ret = getPOSApplication().getCart();
				ret.delete();
				ret = null;
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
			} catch (SQLException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
			} catch (NoCartFoundException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			return ret;
		}

		@Override
		void postWork(Storage storage, Cart workResult) {
			if (workResult == null) {
				finish();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
}
