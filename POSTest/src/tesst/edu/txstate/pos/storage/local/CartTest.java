package tesst.edu.txstate.pos.storage.local;

import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.ItemLocalStorage;
import edu.txstate.pos.storage.StorageException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Tests the local Cart storage.
 *
 */
public class CartTest extends AndroidTestCase {
	
	public static final String LOG_TAG = "JUNIT_TEST_CART";
		
	private CartLocalStorage local = null;
	private User updUser = null;
	private User user2 = null;
	private static SQLiteDatabase db = null;
	
	private ItemLocalStorage itemLocal = null;
	
	private static Cart cart = null;
	
	public void test_A_Create() {
		try {
			local.deleteCart(updUser);
			local.deleteCart(user2);
		} catch (SQLException e) {
			// Don't care - need clean slate
		}
		
		try {
			cart = new Cart(CartTest.db,".01",updUser);
			assertTrue(cart.getId() > 0);
			Log.d(LOG_TAG,"Created cart " + cart.getId());
			
			Cart cart2 = new Cart(CartTest.db,".01",updUser);
			assertEquals(cart.getId(),cart2.getId());
			Log.d(LOG_TAG,"Found cart " + cart2.getId());
			
			Cart cart3 = new Cart(CartTest.db,".01",user2);
			assertTrue(cart3.getId() > cart2.getId());
			Log.d(LOG_TAG,"User 2 cart: " + cart3.getId());
			
			local.deleteCart(user2);
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	
	public void test_B_addItems() {
		Item item = null, item2 = null;
		item = new Item("C001","Item 1","9.99");
		item2 = new Item("C002","Item 2","11.99");
		try {
			itemLocal.addItem(item, updUser);
		} catch (SQLException e) {
			// Don't care - just need them there
		}

		try {
			itemLocal.addItem(item2, updUser);
		} catch (SQLException e) {
			// Don't care - just need them there
		}
		
		try {
			cart.addItem(item, 1);
			assertEquals("9.99",cart.getSubTotal());
			cart.addItem(item2, 1);
			assertEquals("21.98",cart.getSubTotal());
			
			// Add same item again - qty update
			cart.addItem(item, 1);
			// TODO - how to check this one
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	public void Atest_X_delete() {
		try {
			local.deleteCart(updUser);
		} catch (SQLException e) {
			// Don't care
		}
	}

	public void setUp() {
	    SQLiteOpenHelper dbHelper = new POS_DBHelper(getContext());
	    if (CartTest.db == null) {
	    	CartTest.db = dbHelper.getWritableDatabase();
	    }
		local = new CartLocalStorage(db);
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);	
		user2 = new User("USER2","USER2");
		user2.setId(-2);
		
		itemLocal = new ItemLocalStorage(db);
	}
}
