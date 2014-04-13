package edu.txstate.pos;


public class CartActivity extends POSFragmentActivity {

	CartFieldFragment mFieldFragment;
	CartItemListFragment mItemFragment;
	
	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment =  new CartFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment getListFragment() {
		mItemFragment = new CartItemListFragment();
		return mItemFragment;
	}

	public void deleteSelectedItem() {
		mItemFragment.deleteSelectedItem();
	}

}
