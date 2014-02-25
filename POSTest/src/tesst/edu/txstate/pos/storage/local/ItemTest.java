package tesst.edu.txstate.pos.storage.local;

import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ItemLocalStorage;
import edu.txstate.pos.storage.NoItemFoundException;
import edu.txstate.pos.storage.SyncStatus;

/**
 * Tests the local Item storage.
 *
 */
public class ItemTest extends AndroidTestCase {
	
	private static final String LOG_TAG = "JUNIT_TEST_ITEM";

	private ItemLocalStorage local = null;
	private User updUser = null;

	
	public void test_A_addItem() {
		try {
			local.delete("001AA");
		} catch (SQLException e) {
			// Don't care
		}
		
		Item item = new Item("001AA",
				"Junit Test Item 001AAA",
				"11.99");

		try {
			local.addItem(item, updUser);
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	
	}

	public void test_B_getItem() {
		try {
			Item item = local.getItem("001AA");
			assertEquals("Junit Test Item 001AAA",item.getDescription());
			assertEquals("11.99",item.getPrice());
			assertEquals(SyncStatus.PUSH,item.getSyncStatus());
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	public void test_C_updateItem() {
		try {
			Item item = local.getItem("001AA");
			assertEquals("Junit Test Item 001AAA",item.getDescription());
			assertEquals("11.99",item.getPrice());
			
			item.setDescription("New Description");
			item.setPrice("100.01");
			updUser.setId(-100);
			local.update(item, updUser);
			
			item = local.getItem("001AA");
			assertEquals("New Description", item.getDescription());
			assertEquals("100.01", item.getPrice());
			assertEquals(-100, item.getUserID());
			
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}
	
	public void test_D_delete() {
		try {
			local.delete("001AA");
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
		try {
			local.getItem("001AA");
			Log.d(LOG_TAG,"Fail: Item found after delete");
			assertTrue(false);
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			// This is what should happen
			assertTrue(true);
		}
	}

	public void test_E_getAll() {
		try {
			local.delete("X001AA");
			local.delete("X002AA");
			local.delete("X003AA");
		} catch (SQLException e) {
			// Don't care
		}
		try {
			Item i1 = new Item("X001AA","Item 1","9");
			Item i2 = new Item("X002AA","Item 2","10");
			Item i3 = new Item("X003AA","Item 3","12");
			
			local.addItem(i1, updUser);
			local.addItem(i2, updUser);
			local.addItem(i3, updUser);
			
			boolean found1 = false;
			boolean found2 = false;
			boolean found3 = false;
			List<Item> items = local.getItems();
			for (Item item : items) {
				if ("X001AA".equals(item.getId())) {
					found1 = true;
					assertEquals("Item 1", item.getDescription());
					assertEquals("9", item.getPrice());
					assertEquals(SyncStatus.PUSH,item.getSyncStatus());
				} else if ("X002AA".equals(item.getId())) {
					found2 = true;
					assertEquals("Item 2", item.getDescription());
					assertEquals("10", item.getPrice());
					assertEquals(SyncStatus.PUSH,item.getSyncStatus());
				} else if ("X003AA".equals(item.getId())) {
					found3 = true;
					assertEquals("Item 3", item.getDescription());
					assertEquals("12", item.getPrice());
					assertEquals(SyncStatus.PUSH,item.getSyncStatus());
				}
			}
			
			assertEquals(found1,true);
			assertEquals(found2,true);
			assertEquals(found3,true);
			
			local.delete("X001AA");
			local.delete("X002AA");
			local.delete("X003AA");
			
		} catch (SQLException e) {
			e.printStackTrace();
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}

	public void setUp() {

		Log.d(LOG_TAG, "Setting up DB object");
	    
	    SQLiteOpenHelper dbHelper = new POS_DBHelper(getContext());
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
		Log.d(LOG_TAG, "NULL? " + (db == null));

		Log.d(LOG_TAG,"Creating local storage object");
		local = new ItemLocalStorage(db);
		Log.d(LOG_TAG, "NULL? " + (local == null));
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);		
	}

}
