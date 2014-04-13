package edu.txstate.pos;

import edu.txstate.pos.model.User;

public class UserAdminActivity extends POSFragmentActivity {

	private UserFieldFragment mFieldFragment;
	private UserListFragment mListFragment;
	
	@Override
	POSFieldFragment getFieldFragment() {
		mFieldFragment = new UserFieldFragment();
		return mFieldFragment;
	}

	@Override
	POSListFragment<User> getListFragment() {
		mListFragment = new UserListFragment();
		return mListFragment;
	}
	
	public void deleteSelectedItem() {
		mListFragment.deleteSelectedItem();
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
