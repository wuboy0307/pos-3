package edu.txstate.pos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CartActivity extends POSFragmentActivity {

	CartFieldFragment mFieldFragment;
	ItemListFragment mItemFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		
		mFieldFragment  = new CartFieldFragment();
		mItemFragment = new ItemListFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.cart_field_container, mFieldFragment);
		transaction.add(R.id.cart_item_container, mItemFragment);
		transaction.commit();
		/*
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if (fragment == null) {
			fragment = new CartFieldFragment();
			fm.beginTransaction().add(R.id.cart_field_fragment, fragment).commit();
			fragment = new ItemListFragment();
			fm.beginTransaction().add(R.id.cart_item_fragment, fragment).commit();
		}
		*/
	}

}
