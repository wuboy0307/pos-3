package test.edu.txstate.pos.storage.remote;

import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.UserExistsException;
import edu.txstate.pos.storage.UserRemoteStorage;

/**
 * Tests remote user management functions.  This instantiates
 * the UserRemoteStorage object directly.
 * 
 * Tests are named test_X_blah where X is a letter that
 * is used to run the tests in order.
 * 
 * @see setUp()
 * 
 *
 */
public class UserTest extends AndroidTestCase {

	private static final String LOG_TAG = "JUNIT_REMOTE_USER";
	
	private UserRemoteStorage storage = null;
	
	/**
	 * Attempt to create a user that already exists.
	 */
	public void test_A_Existing() {
		User user = new User("geoff","5555");
		
		try {			
			storage.addUser(user);
			assertTrue(false);
		} catch (ConnectionError e) {
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (UserExistsException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Add a new user.
	 */
	public void test_B_addUser() {
		User user = new User("deleteme","XXXX");
		user.setAdmin(true);
		
		try {
			storage.addUser(user);
		} catch (ConnectionError e) {
			e.printStackTrace();
		} catch (UserExistsException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Update the user that was just added
	 */
	public void test_C_updateUser() {
		User user = new User("deleteme","XXXX");
		try {
			user = storage.login(user);
			assertEquals(true,user.isAdmin());
			assertEquals("XXXX",user.getPIN());
			assertEquals("deleteme",user.getLogin());
			assertEquals(true,user.isActive());
			
			user.setActive(false);
			user.setAdmin(false);
			//user.setLogin("deleteme");
			user.setPIN("YYYY");
			storage.updateUser(user);
			
			user = storage.login(user);
			assertEquals(false,user.isAdmin());
			assertEquals("YYYY",user.getPIN());
			assertEquals("deleteme",user.getLogin());
			assertEquals(false,user.isActive());
			
		} catch (ConnectionError e) {
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (BadPasswordException e) {
			e.printStackTrace();
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
		
	}
	
	/**
	 * Get all the users and see if the two known to be there
	 * are in the list.
	 */
	public void test_D_All() {
		List<User> users;
		try {
			users = storage.getUsers();
			boolean foundDeleteMe = false;
			boolean foundGeoff = false;
			for (User user : users) {
				if (user.getLogin().equals("deleteme")) foundDeleteMe = true;
				if (user.getLogin().equals("geoff")) foundGeoff = true;
			}
			assertEquals(true,foundDeleteMe);
			assertEquals(true,foundGeoff);
		} catch (ConnectionError e) {
			assertTrue(false);
		}

	}
	
	/**
	 * Delete a user
	 */
	public void test_E_Delete() {
		try {
			storage.deleteUser("deleteme");
		} catch (ConnectionError e) {
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			Log.e(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Junit setup...run before each test
	 */
	public void setUp() {
		// Remote storage objects just need a device ID
		// for the constructor.  Just use a fake one.
		storage = new UserRemoteStorage("XX");
		
		// Existing user
		User user = new User("geoff","5555");
		
		try {
			storage.addUser(user);
			
		} catch (ConnectionError e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (UserExistsException e) {
			// This is OK, just making sure the user
			// is there
		}

	}
	
}
