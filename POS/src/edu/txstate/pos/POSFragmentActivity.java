package edu.txstate.pos;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public abstract class POSFragmentActivity extends FragmentActivity {

	POSFieldFragment mFieldFragment;
	POSListFragment mItemFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContainer());
		
		mFieldFragment  = getFieldFragment();
		mItemFragment = getListFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(getFieldContainer(), mFieldFragment);
		transaction.add(getListContainer(), mItemFragment);
		transaction.commit();

	}
	
	abstract POSFieldFragment getFieldFragment();
	abstract POSListFragment getListFragment();

	protected int getContainer() {
		return R.layout.activity_fragment_container;
	}
	
	protected int getFieldContainer() {
		return R.id.fragment_field_container;
	}

	protected int getListContainer() {
		return R.id.fragment_item_container;
	}
}
