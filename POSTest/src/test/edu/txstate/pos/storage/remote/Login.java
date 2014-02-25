package test.edu.txstate.pos.storage.remote;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.UserRemoteStorage;

/**
 * Tests the login function
 * 
 * TEST DATA ASSUMED:  User geoff/5555 is in the 
 * database.  This data is a part of the DDL for
 * the remote database so should always be there.
 *
 */
public class Login extends AndroidTestCase {
	
	private static String LOG_TAG = "JUNIT_REMOTE_LOGIN";
	
	private UserRemoteStorage remote = null;
	
	/**
	 * User with a good login and password.
	 */
	public void testValid() {
		Log.d(LOG_TAG, "testValid");
		User user = new User("geoff","5555");
		
		try {
			user = remote.login(user);
			assertEquals(false, user.isAdmin());
			assertEquals(true, user.isActive());
			assertEquals(1, user.getId());
		} catch (ConnectionError e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			assertTrue(false);
		} catch (BadPasswordException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * User with a good login and bad password
	 */
	public void testBadPassword() {
		Log.d(LOG_TAG, "testBadPassword");
		User user = new User("geoff","WRONG");
		
		try {
			remote.login(user);
			assertTrue(false);
		} catch (ConnectionError e) {
			assertTrue(false);
		} catch (NoUserFoundException e) {
			assertTrue(false);
		} catch (BadPasswordException e) {
			assertTrue(true);
		}
	}

	/**
	 * User that doesn't exist
	 */
	public void testNoUser() {
		Log.d(LOG_TAG, "testNoUser");
		User user = new User("WRONGUSER","5555");
		
		try {
			remote.login(user);
			assertTrue(false);
		} catch (ConnectionError e) {
			assertTrue(false);
		} catch (NoUserFoundException e) {
			assertTrue(true);
		} catch (BadPasswordException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Junit setup...run before each test.
	 */
	public void setUp() {
		remote = new UserRemoteStorage("XX");
	}
}
