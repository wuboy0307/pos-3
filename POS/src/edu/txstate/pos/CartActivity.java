package edu.txstate.pos;

import edu.txstate.pos.model.CartItem;


public class CartActivity extends POSFragmentActivity {


	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment =  new CartFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment<CartItem> getListFragment() {
		mItemFragment = new CartItemListFragment();
		return mItemFragment;
	}

	public void deleteSelectedItem() {
		mItemFragment.deleteSelectedItem();
	}

	@Override
	int getMainView() {
		return R.id.fragmentContainer;
	}

	@Override
	int getSpinnerView() {
		return R.id.fragment_spinner;
	}

}
