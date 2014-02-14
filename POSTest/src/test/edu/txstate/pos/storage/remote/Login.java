package test.edu.txstate.pos.storage.remote;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.UserRemoteStorage;

public class Login extends AndroidTestCase {
	
	private UserRemoteStorage remote = null;
	
	public void testValid() {
		Log.d("JUNIT_TEST", "testValid");
		User user = new User("geoff","5555");
		
		try {
			user = remote.login(user);
			assertEquals(false, user.isAdmin());
			assertEquals(true, user.isActive());
			assertEquals(1, user.getId());
		} catch (ConnectionError e) {
			Log.e("JUNIT_TEST", e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			assertTrue(false);
		} catch (BadPasswordException e) {
			assertTrue(false);
		}
	}
	
	public void testBadPassword() {
		Log.d("JUNIT_TEST", "testBadPassword");
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

	public void testNoUser() {
		Log.d("JUNIT_TEST", "testNoUser");
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
	
	public void setUp() {
		remote = new UserRemoteStorage("XX");
	}
}
