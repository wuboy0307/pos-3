package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.storage.StorageException;
import android.support.v4.app.ListFragment;

public class POSListFragment extends ListFragment {
	
	public Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
}
