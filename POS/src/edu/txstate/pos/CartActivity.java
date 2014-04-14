package edu.txstate.pos;

import android.content.Intent;
import android.util.Log;
import edu.txstate.pos.model.CartItem;


public class CartActivity extends POSFragmentActivity {

	private static final String LOG_TAG = "CartActivity";
	
	public static final int MANUAL_PAYMENT = 1;
	public static final int CARDSWIPE_PAYMENT = 2;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG,"onActivityResult");
		if (resultCode == RESULT_OK) {
			switch(requestCode) {
				case MANUAL_PAYMENT:
							Log.d(LOG_TAG,"MANUAL");
							break;
				case CARDSWIPE_PAYMENT:
					
							break;
			}
		}
	}

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
