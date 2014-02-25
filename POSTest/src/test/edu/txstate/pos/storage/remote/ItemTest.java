package test.edu.txstate.pos.storage.remote;

import java.util.List;

import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.ItemExistsException;
import edu.txstate.pos.storage.ItemRemoteStorage;
import edu.txstate.pos.storage.NoItemFoundException;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 *	Tests remote management of Item data.  Uses the 
 *  ItemRemoteStorage object directly.
 *  
 *  TEST DATA ASSUMED: Three test items are a part of the 
 *  DDL for the remote database setup and are assumed in
 *  testing getAll().
 *
 */
public class ItemTest extends AndroidTestCase {

	private static final String LOG_TAG = "JUNIT_TEST_ITEM";
	
	private static final String DEVICE_ID = "XX";

	private ItemRemoteStorage remote = null;
	private User updUser = null;

	/**
	 * Add an item
	 */
	public void test_A_addItem() {
		// Delete the item if it exists
		try {
			remote.delete("001AA", updUser);
			Log.d(LOG_TAG,"Deleted test item");
		} catch (ConnectionError e) {
			// Don't care
		}
		
		Item item = new Item("001AA",
							"Junit Test Item 001AAA",
							"11.99");
		try {
			remote.add(item, updUser);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (ItemExistsException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Add an existing item
	 */
	public void test_B_addItemAgain() {
		Item item = new Item("001AA",
							"Junit Test Item 001AAA",
							"11.99");
		try {
			remote.add(item, updUser);
			Log.d(LOG_TAG,"FAIL: Item added again w/o exception");
			assertTrue(false);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (ItemExistsException e) {	
			assertTrue(true);
		}
	}

	/**
	 * Retrieve the item
	 */
	public void test_C_GetItem() {
		try {
			Item item = remote.get("001AA");
			assertNotNull(item);
			assertEquals("001AA",item.getId());
			assertEquals("Junit Test Item 001AAA",item.getDescription());
			assertEquals("11.99", item.getPrice());
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Update an existing item.
	 */
	public void test_D_UpdateItem() {
		Item item = new Item("001AA",
						"Updated Description for JUNIT",
						"100.01");
		try {
			remote.update(item, updUser);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (ItemExistsException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
		
		try {
			item = remote.get("001AA");
			assertNotNull(item);
			assertEquals("001AA",item.getId());
			assertEquals("Updated Description for JUNIT",item.getDescription());
			assertEquals("100.01", item.getPrice());
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Delete an item
	 */
	public void test_E_DeleteItem() {
		try {
			remote.delete("001AA", updUser);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
		
		try {
			remote.get("001AA");
			Log.d(LOG_TAG,"FAIL: Item found after delete");
			assertTrue(false);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			// This is what should happen
			assertTrue(true);
		}
	}
	
	/**
	 * Get all of the items and see if the items known to be
	 * in the test data are in the list.
	 */
	public void test_X_All() {
		try {
			List<Item> items = remote.getAll();
			boolean found1 = false;
			boolean found2 = false;
			
			for (Item item : items) {
				if ("001".equals(item.getId())) {
					found1 = true;
					assertEquals("Item 1", item.getDescription());
					assertEquals("9.99", item.getPrice());
				} else if ("002".equals(item.getId())) {
					found2 = true;
					assertEquals("Item 2", item.getDescription());
					assertEquals("5.50", item.getPrice());
				}
			}
			
			assertEquals(found1,true);
			assertEquals(found2,true);
		} catch (ConnectionError e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	/**
	 * Junit set...run before every test.
	 */
	public void setUp() {
		remote = new ItemRemoteStorage(DEVICE_ID);
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);
	}
	
	
}
