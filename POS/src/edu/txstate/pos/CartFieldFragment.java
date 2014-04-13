package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.storage.StorageException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CartFieldFragment extends POSFieldFragment {

	private final String LOG_TAG = "CART_FRAG";
	
	private EditText mCustomerEmail;
	private TextView mSubtotal;
	private TextView mTax;
	private TextView mTotal;
	private Button mAddItemButton;
	private Button mManualButton;
	private Button mPayButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cart_fields, parent, false);
		
		mCustomerEmail = (EditText) v.findViewById(R.id.customer_email);
		mSubtotal = (TextView) v.findViewById(R.id.subtotal);
		mTax = (TextView) v.findViewById(R.id.tax);
		mTotal = (TextView) v.findViewById(R.id.total);
		mAddItemButton = (Button) v.findViewById(R.id.cart_add_item_button);
		mManualButton = (Button) v.findViewById(R.id.cart_manual_button);
		mPayButton = (Button) v.findViewById(R.id.cart_sell_button);
		
		try {
			Cart cart = getCart();
			Log.d(LOG_TAG,"Cart ID: " + cart.getId());
			mCustomerEmail.setText(cart.getCustomer());
			mSubtotal.setText(cart.getSubTotal());
			mTax.setText(cart.getTaxAmount());
			mTotal.setText(cart.getTotal());
		} catch (StorageException e) {
			Toast.makeText(getActivity().getApplicationContext(), 
					"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
		}

		
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

					}
				});

		
		mPayButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
		
		return v;
	}
}
