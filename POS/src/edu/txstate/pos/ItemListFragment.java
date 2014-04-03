package edu.txstate.pos;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import edu.txstate.pos.model.Item;

public class ItemListFragment extends ListFragment {
	private ArrayList<Item> mItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle("Item Inventory");
	}
}
