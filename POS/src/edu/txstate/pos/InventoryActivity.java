package edu.txstate.pos;

public class InventoryActivity extends POSFragmentActivity {

	@Override
	POSFieldFragment getFieldFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	POSListFragment getListFragment() {
		// TODO Auto-generated method stub
		return new ItemListFragment();
	}

}
