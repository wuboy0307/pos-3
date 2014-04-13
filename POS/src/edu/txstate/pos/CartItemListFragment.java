package edu.txstate.pos;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import edu.txstate.pos.model.CartItem;
import edu.txstate.pos.storage.StorageException;

public class CartItemListFragment extends POSListFragment {
	private static String LOG_TAG = "CartItemListFragment";
	
	private ArrayAdapter<CartItem> mAdapter;
	
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
	
}
