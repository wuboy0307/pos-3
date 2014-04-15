package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.app.Activity;
import android.support.v4.app.Fragment;

public class POSFieldFragment extends Fragment {

	protected String mStatusMessage;
	POSTaskParent parent = null;
	
	protected Storage getStorage() {
		return ((POSApplication) getActivity().getApplication()).getStorage();
	}
	
	protected Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
	
	protected POSApplication getPOSApplication() {
		return ((POSApplication) getActivity().getApplication());
	}
	
	protected void finish() {
		getActivity().finish();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = (POSTaskParent) activity;
	}
}
