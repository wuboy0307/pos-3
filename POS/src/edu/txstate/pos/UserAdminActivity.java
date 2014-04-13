package edu.txstate.pos;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class UserAdminActivity extends POSFragmentActivity {

	private UserFieldFragment mFieldFragment;
	private UserListFragment mListFragment;
	
	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment = new UserFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment getListFragment() {
		mListFragment = new UserListFragment();
		return mListFragment;
	}
	
	public void deleteSelectedItem() {
		mListFragment.deleteSelectedItem();
	}

}
