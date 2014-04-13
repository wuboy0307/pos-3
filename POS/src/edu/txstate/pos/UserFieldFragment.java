package edu.txstate.pos;

import android.content.Intent;
import android.os.Bundle;
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
		Log.d(LOG_TAG,"onCreateView");
		View v = inflater.inflate(R.layout.fragment_user_fields, parent, false);
		
		mAdd = (Button) v.findViewById(R.id.user_add_button);
		
		mAdd.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity().getBaseContext(), AddUserActivity.class);
						getActivity().startActivity(intent);
					}
				});
		
		return v;
	}
	
}
