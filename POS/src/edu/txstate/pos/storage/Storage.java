package edu.txstate.pos.storage;

import java.util.List;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;

public class Storage {

	private RemoteStorage remote = null;
	
	public Storage() {
		remote = new RemoteStorage();
	}
	
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		return user = remote.login(user);
	}
	
	public User addUser(User user) throws ConnectionError, UserExistsException {
		return remote.addUser(user);
	}
	
	public void deleteUser(String login) throws ConnectionError, NoUserFoundException {
		remote.deleteUser(login);
	}
	
	public void updateUser(User user) throws ConnectionError, NoUserFoundException {
		remote.updateUser(user);
	}
	
	public List<User> getUsers() throws ConnectionError {
		return remote.getUsers();
	}
	
	public void sellCart(Cart cart) throws InvalidCartException {
		if (!cart.isValid()) throw new InvalidCartException();
	}
	
}
