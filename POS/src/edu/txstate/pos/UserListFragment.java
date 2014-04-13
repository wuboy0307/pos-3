package edu.txstate.pos;

import java.util.ArrayList;
import java.util.List;

import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UserListFragment extends POSListFragment {
	private static String LOG_TAG = "UserListFragment";
	
	private ArrayAdapter<User> mAdapter;
	private int selected;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG,"onCreate");
		
		List<User> mUsers = null;
		try {
			mUsers = getUsers();
		} catch (ConnectionError e) {
			mUsers = new ArrayList<User>();
			Toast.makeText(getActivity().getApplicationContext(), 
					"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
		}
		
		mAdapter = new ArrayAdapter<User>(
					getActivity(),
					android.R.layout.simple_list_item_1,
					mUsers
				);
		setListAdapter(mAdapter);
		
	}

	public void deleteSelectedItem() {
		User u = (User) getListAdapter().getItem(selected);

		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//CartItem ci = (CartItem) getListAdapter().getItem(position);
		this.selected = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Delete user?")
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((UserAdminActivity) getActivity()).deleteSelectedItem();
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

}
