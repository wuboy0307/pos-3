package edu.txstate.pos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import static edu.txstate.pos.UserListFragment.*;

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
						Bundle extras = new Bundle();
						extras.putInt(EXTRA_FIELD_MODE, EXTRA_MODE_ADD);
						intent.putExtras(extras);
						getActivity().startActivity(intent);
					}
				});
		
		return v;
	}

	@Override
	void netStatusUpdate() {
		// TODO Auto-generated method stub
		
	}
	
}
