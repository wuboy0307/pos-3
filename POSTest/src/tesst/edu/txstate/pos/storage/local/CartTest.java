package tesst.edu.txstate.pos.storage.local;

import edu.txstate.pos.POSApplication;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.CartLocalStorage;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

public class CartTest extends AndroidTestCase {
	
	public static final String LOG_TAG = "JUNIT_TEST_CART";
	
	private static SQLiteDatabase db = null;
	
	private CartLocalStorage local = null;
	private User updUser = null;
	/*
	public void testCreate() {
		try {
			Cart cart = local.createCart(updUser);
			Log.d(LOG_TAG, "CART ID: " + cart.getId());
			assertTrue(cart.getId() > -1);
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
*/
	public void setUp() {
		if (CartTest.db == null) {
			POSApplication pos = (POSApplication) getContext().getApplicationContext();
			db = pos.getDb();
		}
		local = new CartLocalStorage(db);
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);	
	}
}
