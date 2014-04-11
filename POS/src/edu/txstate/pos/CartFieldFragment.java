package edu.txstate.pos;

import edu.txstate.pos.storage.StorageException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class CartFieldFragment extends POSFragment {

	private final String LOG_TAG = "CART_FRAG";
	
	private EditText mCustomerEmail;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cart_fields, parent, false);
		

		mCustomerEmail = (EditText) v.findViewById(R.id.customer_email);
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
		
		return v;
	}
}
