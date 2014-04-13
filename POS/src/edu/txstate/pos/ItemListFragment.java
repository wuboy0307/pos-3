package edu.txstate.pos;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import edu.txstate.pos.model.Item;

public class ItemListFragment extends POSListFragment<Item> {
	private static String LOG_TAG = "ItemListFragment";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG,"onCreate");
		
		ArrayList<Item> al = new ArrayList<Item>();
		for (int i = 0; i<100; i++) {
			Item item = new Item("foo","Item 1","1.00");
			al.add(item);
		}
		
		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				al);
		setListAdapter(adapter);
	}

	@Override
	public void deleteSelectedItem() {
		// TODO Auto-generated method stub
		
	}
	
}
