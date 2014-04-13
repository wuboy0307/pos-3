package edu.txstate.pos;


public class CartActivity extends POSFragmentActivity {

	@Override
	POSFieldFragment getFieldFragment() {
		return new CartFieldFragment();
	}

	@Override
	POSListFragment getListFragment() {
		return new CartItemListFragment();
	}



}
