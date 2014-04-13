package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.support.v4.app.Fragment;

public class POSFieldFragment extends Fragment {

	protected Storage getStorage() {
		return ((POSApplication) getActivity().getApplication()).getStorage();
	}
	
	protected Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
}
