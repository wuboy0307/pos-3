package edu.txstate.pos;

import java.util.List;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.StorageException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class POSListFragment<X> extends ListFragment {
	
	private static final String LOG_TAG = "POSListFragment";
	
	int selected;
	ArrayAdapter<X> mAdapter;
	POSTaskParent parent = null;
	String mStatusMessage = null;
	
	public Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
	
	public List<User> getUsers() throws ConnectionError {
		return ((POSApplication) getActivity().getApplication()).getUsers();
	}
	
	public POSApplication getPOSApplication() {
		return (POSApplication)getActivity().getApplication();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(LOG_TAG,"onListItemClick");
		//CartItem ci = (CartItem) getListAdapter().getItem(position);
		this.selected = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Delete item?")
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((POSFragmentActivity) getActivity()).deleteSelectedItem();
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
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume()");
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = (POSTaskParent) activity;
	}
	
	abstract public void deleteSelectedItem();
	
}
