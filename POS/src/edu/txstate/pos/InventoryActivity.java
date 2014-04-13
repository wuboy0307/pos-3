package edu.txstate.pos;

import edu.txstate.pos.model.Item;

public class InventoryActivity extends POSFragmentActivity {

	@Override
	POSFieldFragment getFieldFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	POSListFragment<Item> getListFragment() {
		// TODO Auto-generated method stub
		return new ItemListFragment();
	}

	@Override
	int getMainView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getSpinnerView() {
		// TODO Auto-generated method stub
		return 0;
	}

}
