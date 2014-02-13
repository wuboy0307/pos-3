package edu.txstate.pos.storage;

import java.util.List;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;

public class Storage {

	private UserRemoteStorage userRemote = null;
	
	public Storage() {
		userRemote = new UserRemoteStorage();
	}
	
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		return user = userRemote.login(user);
	}
	
	public User addUser(User user) throws ConnectionError, UserExistsException {
		return userRemote.addUser(user);
	}
	
	public void deleteUser(String login) throws ConnectionError, NoUserFoundException {
		userRemote.deleteUser(login);
	}
	
	public void updateUser(User user) throws ConnectionError, NoUserFoundException {
		userRemote.updateUser(user);
	}
	
	public List<User> getUsers() throws ConnectionError {
		return userRemote.getUsers();
	}
	
	public void sellCart(Cart cart) throws InvalidCartException {
		if (!cart.isValid()) throw new InvalidCartException();
	}
	
}
