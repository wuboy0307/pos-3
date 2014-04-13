package edu.txstate.pos;

import edu.txstate.pos.storage.StorageException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class UserFieldFragment extends POSFieldFragment {

	private final String LOG_TAG = "UserFieldFragment";
	
	private Button mAdd;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cart_fields, parent, false);
		
		mAdd = (Button) v.findViewById(R.id.user_add_button);
		
		mAdd.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				
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
