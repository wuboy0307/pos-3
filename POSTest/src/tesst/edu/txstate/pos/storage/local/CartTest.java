package tesst.edu.txstate.pos.storage.local;

import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.callback.ServiceCallback;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.Payment;
import edu.txstate.pos.model.User;
import edu.txstate.pos.service.JunitSyncStub;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.ItemLocalStorage;
import edu.txstate.pos.storage.StorageException;
import edu.txstate.pos.storage.SyncStatus;
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
	private static ServiceCallback syncService = new JunitSyncStub();
	
	/**
	 * Create a cart.  A cart created again for the same
	 * user will return the same cart.
	 */
	public void test_A_Create() {
		try {
			local.deleteCurrentCart(updUser);
			local.deleteCurrentCart(user2);
		} catch (SQLException e) {
			// Don't care - need clean slate
		}
		
		try {
			cart = new Cart(CartTest.db,".01",updUser,syncService);
			assertTrue(cart.getId() > 0);
			Log.d(LOG_TAG,"Created cart " + cart.getId());
			
			Cart cart2 = new Cart(CartTest.db,".01",updUser,syncService);
			assertEquals(cart.getId(),cart2.getId());
			Log.d(LOG_TAG,"Found cart " + cart2.getId());
			
			Cart cart3 = new Cart(CartTest.db,".01",user2,syncService);
			assertTrue(cart3.getId() > cart2.getId());
			Log.d(LOG_TAG,"User 2 cart: " + cart3.getId());
			
			local.deleteCurrentCart(user2);
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Add items to the cart and check the subtotal.
	 */
	public void test_B_addItems() {
		Item item = null, item2 = null;
		item = new Item("C001","Item 1","9.99");
		item2 = new Item("C002","Item 2","11.99");
		try {
			itemLocal.addItem(item, SyncStatus.DONE, updUser);
		} catch (SQLException e) {
			// Don't care - just need them there
		}

		try {
			itemLocal.addItem(item2, SyncStatus.DONE, updUser);
		} catch (SQLException e) {
			// Don't care - just need them there
		}
		
		try {
			cart.addItem(item, 1);
			assertEquals("9.99",cart.getSubTotal());
			cart.addItem(item2, 1);
			assertEquals("21.98",cart.getSubTotal());
			
			// Add same item again - quantity updates
			cart.addItem(item, 1);

			assertEquals(2,cart.getItemQuatity(item));
			assertEquals("31.97",cart.getSubTotal());

		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Update the quantity of an item
	 */
	public void test_C_updateQuantity() {
		Item item2 = null;
		item2 = new Item("C002","Item 2","11.99");
		
		try {
			cart.updateQuantity(item2, 5);
			assertEquals(5,cart.getItemQuatity(item2));
			assertEquals("79.93",cart.getSubTotal());
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Delete an item by setting the quantity to 0.
	 */
	public void test_D_deleteItem() {
		Item item2 = null;
		item2 = new Item("C002","Item 2","11.99");
		
		try {
			cart.updateQuantity(item2, 0);
			assertEquals(0,cart.getItemQuatity(item2));
			assertEquals("19.98",cart.getSubTotal());
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Update the tax rate and check the tax amount
	 * calculation.
	 */
	public void test_E_updateTax() {
		try {
			cart.setTax(".01");
			assertEquals("0.20",cart.getTaxAmount());
			assertEquals("20.18",cart.getTotal());
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Set the customer.
	 */
	public void test_F_updateCustomer() {
		try {
			cart.setCustomer("geoffm@txstate.edu");
			assertEquals("geoffm@txstate.edu",cart.getCustomer());
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Set the payment
	 */
	public void test_G_addPayment() {
		Payment payment = new Payment("1234","XXXX");
		try {
			cart.addPayment(payment);
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Now that everything is populated on the cart,
	 * it should be valid.
	 */
	public void test_H_valid() {
		assertTrue(cart.isValid());
	}
	
	public void test_I_sell() {
		try {
			assertTrue(cart.sell());
		} catch (StorageException e) {
			Log.e(LOG_TAG, e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Junit setUp...runs before every test.
	 */
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
