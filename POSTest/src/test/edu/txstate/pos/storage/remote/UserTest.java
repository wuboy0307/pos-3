package test.edu.txstate.pos.storage.remote;

import android.test.AndroidTestCase;
import android.util.Log;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.BadPasswordException;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.RemoteStorage;
import edu.txstate.pos.storage.UserExistsException;

public class UserTest extends AndroidTestCase {

	private RemoteStorage storage = null;
	
	public void test_A_Existing() {
		User user = new User("geoff","5555");
		
		try {			
			storage.addUser(user);
			assertTrue(false);
		} catch (ConnectionError e) {
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		} catch (UserExistsException e) {
			assertTrue(true);
		}
	}
	
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
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		} catch (BadPasswordException e) {
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		}
		
	}
	
	public void test_D_Delete() {
		try {
			storage.deleteUser("deleteme");
		} catch (ConnectionError e) {
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		} catch (NoUserFoundException e) {
			Log.e("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		}
	}
	
	public void setUp() {
		storage = new RemoteStorage();
		
		User user = new User("geoff","5555");
		
		try {
			storage.addUser(user);
		} catch (ConnectionError e) {
			Log.e("JUNIT_TEST",e.getMessage());
		} catch (UserExistsException e) {
			// This is OK, just making sure the user
			// is there
		}
	}
	
}
