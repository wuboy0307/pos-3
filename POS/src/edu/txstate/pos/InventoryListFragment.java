package edu.txstate.pos;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;

public class InventoryListFragment extends POSListFragment<Item> {
	private static String LOG_TAG = "ItemListFragment";
	
	private ArrayList<Item> mItems = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG,"onCreate");
		
		mItems = new ArrayList<Item>();

		mAdapter = new ArrayAdapter<Item>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				mItems);
		setListAdapter(mAdapter);
		updateItems();
	}

	public void updateItems() {
		parent.executeAsyncTask("GetItemsTask", 
				new GetItemsTask("GetItemsTask", parent), 
				true, 
				(POSModel) null);
	}
	
	@Override
	public void deleteSelectedItem() {
		Item item = (Item) getListAdapter().getItem(selected);
		parent.executeAsyncTask("DeleteItemsTask", 
				new DeleteItemTask("DeleteItemsTask", parent), 
				true, 
				item);
	}
	
	public class DeleteItemTask extends POSTask<Item> {
		public DeleteItemTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		Item backgroundWork(Storage storage, POSModel... args) {
			Item ret = (Item) args[0];
			try {
				parent.getStorage().deleteItem(ret.getId());
				mItems.remove(selected);
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			return ret;
		}

		@Override
		void postWork(Storage storage, Item workResult) {
			if (workResult != null) {
				mAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			
			}
		}
	}
	
	public class GetItemsTask extends POSTask<List<Item>> {
		public GetItemsTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		List<Item> backgroundWork(Storage storage, POSModel... args) {
			try {
				List<Item> newItems = storage.getAllItems();
				mItems.clear();
				mItems.addAll(newItems);
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return null;
			}
			
			return mItems;
		}

		@Override
		void postWork(Storage storage, List<Item> workResult) {
			if (workResult != null) {
				mAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			
			}
			
		}
	}
	
}
