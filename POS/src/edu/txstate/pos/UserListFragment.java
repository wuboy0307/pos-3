package edu.txstate.pos;

import java.util.List;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.Storage;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserListFragment extends POSListFragment<User> {
	private static String LOG_TAG = "UserListFragment";
	
	public static String EXTRA_FIELD_ID = "id";
	public static String EXTRA_FIELD_LOGIN = "login";
	public static String EXTRA_FIELD_PIN = "pin";
	public static String EXTRA_FIELD_ADMIN = "isAdmin";
	public static String EXTRA_FIELD_ACTIVE = "isActive";
	
	public static String EXTRA_FIELD_MODE = "mode";
	
	public final static int EXTRA_MODE_ADD = 1;
	public final static int EXTRA_MODE_EDIT = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG,"onCreate");
		
		List<User> mUsers = ((POSApplication) getActivity().getApplication()).getUsers();
		/*
		mAdapter = new ArrayAdapter<User>(
					getActivity(),
					android.R.layout.simple_list_item_1,
					mUsers
				);
		*/
		mAdapter =  new UserAdapter(mUsers);
		setListAdapter(mAdapter);
		parent.executeAsyncTask("GetUsers",
				new GetUsersTask("GetUsers",parent), 
				true, 
				(POSModel) null);
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		this.selected = position;
		User u = ((UserAdapter) getListAdapter()).getItem(position);
		Intent intent = new Intent(getActivity().getBaseContext(), AddUserActivity.class);
		Bundle extras = new Bundle();
		extras.putInt(EXTRA_FIELD_ID, u.getId());
		extras.putString(EXTRA_FIELD_LOGIN, u.getLogin());
		extras.putString(EXTRA_FIELD_PIN, u.getPIN());
		extras.putBoolean(EXTRA_FIELD_ADMIN, u.isAdmin());
		extras.putBoolean(EXTRA_FIELD_ACTIVE, u.isActive());
		extras.putInt(EXTRA_FIELD_MODE, EXTRA_MODE_EDIT);
		intent.putExtras(extras);
		getActivity().startActivity(intent);
	}
	
	private class UserAdapter extends ArrayAdapter<User> {
		public UserAdapter(List<User> users) {
			super(getActivity(),0,users);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().
						inflate(R.layout.list_item_user, null);
			}
			
			User u = getItem(position);
			TextView loginView = (TextView) convertView.findViewById(R.id.user_list_item_userID);
			CheckBox adminCheck = (CheckBox) convertView.findViewById(R.id.user_list_item_checkBox);
			
			loginView.setText(u.getLogin());
			adminCheck.setChecked(u.isAdmin());
			
			return convertView;
		}
	}
	
	public class GetUsersTask extends POSTask<List<User>> {
		public GetUsersTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		List<User> backgroundWork(Storage storage, POSModel... args) {
			List<User> ret = null;
			try {
				ret = storage.getUsers();
			} catch (ConnectionError e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			return ret;
		}

		@Override
		void postWork(Storage storage, List<User> workResult) {
			if (workResult != null) {
				getPOSApplication().setUsers(workResult);
				mAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void deleteSelectedItem() {
		// TODO Auto-generated method stub
		
	}

}
