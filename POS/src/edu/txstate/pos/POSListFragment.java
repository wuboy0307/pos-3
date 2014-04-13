package edu.txstate.pos;

import java.util.List;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.StorageException;
import android.support.v4.app.ListFragment;

public class POSListFragment extends ListFragment {
	
	public Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
	
	public List<User> getUsers() throws ConnectionError {
		return ((POSApplication) getActivity().getApplication()).getUsers();
	}
}
