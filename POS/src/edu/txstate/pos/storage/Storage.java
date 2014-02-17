package edu.txstate.pos.storage;

import java.util.List;

import android.content.Context;
import android.util.Log;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;

/**
 * API for persistent storage for the POS application.  This class abstracts
 * the differences in where data is stored - locally or remotely.  The logic
 * for synchronization between local and remote tables is also encapsulated or
 * called from here.
 * 
 * Activities should get an instance of this object from the POSActvity using the
 * getStorage() method.  The setting of the context, deviceID, and logged in user
 * is taken care of when accessed via that method.
 * 
 * @see edu.txstate.pos.POSActivity
 * 
 * @author Geoff Marinski
 *
 */
public class Storage {

	private UserRemoteStorage userRemote = null;
	private ItemRemoteStorage itemRemote = null;
	
	private String mDeviceID = null;
	private User mLoggedInUser = null;
	private Context mContext = null;
	
	
	/**
	 * Constructor.
	 * 
	 * @param context  		Android Context object
	 * @param deviceID		Unique device ID used for synchronization
	 * @param loggedInUser	The currently logged in user (for logging)
	 */
	public Storage(Context context, String deviceID, User loggedInUser) {
		mContext = context;
		mDeviceID = deviceID;
		mLoggedInUser = loggedInUser;
		
		userRemote = new UserRemoteStorage(mDeviceID);
		itemRemote = new ItemRemoteStorage(mDeviceID);
	}
	
	/**
	 * Used to check a user's login and password.  If the login/password is valid,
	 * then a User object is returned with all of the fields populated.  Any data
	 * passed into the User object is overwritted based on what comes back from the
	 * remote storage.
	 * 
	 * @param user	User object with login and password populated.
	 * @return		Fully populated user object
	 * @throws ConnectionError		A network, protocol, server side, or API error
	 * @throws NoUserFoundException
	 * @throws BadPasswordException
	 */
	public User login(User user) throws ConnectionError, NoUserFoundException, BadPasswordException {
		return user = userRemote.login(user);
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 * @throws ConnectionError		A network, protocol, server side, or API error
	 * @throws UserExistsException
	 */
	public User addUser(User user) throws ConnectionError, UserExistsException {
		return userRemote.addUser(user);
	}
	
	/**
	 * 
	 * @param login
	 * @throws ConnectionError		A network, protocol, server side or API error
	 * @throws NoUserFoundException
	 */
	public void deleteUser(String login) throws ConnectionError, NoUserFoundException {
		userRemote.deleteUser(login);
	}
	
	/**
	 * Update a user record.
	 * 
	 * If there is no userID in the User object, then the record is looked up
	 * by the login record.  If there is a userID, then the record to be updated
	 * is looked up by the userID.  This implies that if the login is to be updated,
	 * then the userID needs to be passed in.
	 * 
	 * @param user					User object containing the new values for a user
	 * @throws ConnectionError		A network, protocol, server side, or API error
	 * @throws NoUserFoundException The user record wasn't found.
	 */
	public void updateUser(User user) throws ConnectionError, NoUserFoundException {
		userRemote.updateUser(user);
	}
	
	/**
	 * Gets all of the users of the application including inactive users.
	 * 
	 * @return List of users
	 * @throws ConnectionError		A network, protocol, or API error
	 */
	public List<User> getUsers() throws ConnectionError {
		return userRemote.getUsers();
	}
	
	public void sellCart(Cart cart) throws InvalidCartException {
		if (!cart.isValid()) throw new InvalidCartException();
	}
	

	public void syncItems() throws ConnectionError {
		
	}
	
	public void resendReceipt() throws ConnectionError, NoUserFoundException, InvalidCartException {
		
	}
	
}

