package edu.txstate.pos;

import edu.txstate.pos.model.Item;

public class InventoryActivity extends POSFragmentActivity {

	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment = new InventoryFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment<Item> getListFragment() {
		mListFragment = new InventoryListFragment();
		return mListFragment;
	}

	@Override
	int getMainView() {
		return R.id.fragmentContainer;
	}

	@Override
	int getSpinnerView() {
		return R.id.fragment_spinner;
	}
	
	public void refreshList() {
		((InventoryListFragment) mListFragment).updateItems();
	}

}
