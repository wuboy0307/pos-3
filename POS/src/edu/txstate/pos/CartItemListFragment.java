package edu.txstate.pos;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.txstate.pos.model.CartItem;
import edu.txstate.pos.storage.StorageException;

public class CartItemListFragment extends POSListFragment {
	private static String LOG_TAG = "CartItemListFragment";
	
	private ArrayAdapter<CartItem> mAdapter;
	private int selected;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG,"onCreate");
		
		List<CartItem> mItems;
		try {
			mItems = getCart().getItems();
		} catch (StorageException e) {
			mItems = new ArrayList<CartItem>();
			Toast.makeText(getActivity().getApplicationContext(), 
					"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
			
			e.printStackTrace();
		}
		
		mAdapter = new ArrayAdapter<CartItem>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				mItems);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	public void deleteSelectedItem() {
		//Log.d(LOG_TAG,"Delete Selected Item " + selected);
		CartItem ci = (CartItem) getListAdapter().getItem(selected);
		try {
			getCart().updateQuantity(ci.getItem(), 0);
		} catch (StorageException e) {
			Log.e(LOG_TAG,e.getMessage());
			e.printStackTrace();
		}
		mAdapter.notifyDataSetChanged();
	}
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//CartItem ci = (CartItem) getListAdapter().getItem(position);
		this.selected = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Delete item from cart?")
		.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((CartActivity) getActivity()).deleteSelectedItem();
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
