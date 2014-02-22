package test.edu.txstate.pos.storage.remote;

import java.text.DecimalFormat;
import java.util.List;

import edu.txstate.pos.model.Item;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.ItemRemoteStorage;
import edu.txstate.pos.storage.NoItemFoundException;
import android.test.AndroidTestCase;
import android.util.Log;

public class ItemTest extends AndroidTestCase {

	private ItemRemoteStorage remote = null;
	DecimalFormat df = new DecimalFormat(".##");
	
	public void testAll() {
		try {
			List<Item> items = remote.getAll();
			boolean found1 = false;
			boolean found2 = false;
			
			for (Item item : items) {
				if ("001".equals(item.getId())) {
					found1 = true;
					assertEquals("Item 1", item.getDescription());
					String x = df.format(item.getPrice());
					assertEquals("9.99", x);
					assertEquals(-1, item.getUserID());
				} else if ("002".equals(item.getId())) {
					found2 = true;
					assertEquals("Item 2", item.getDescription());
					assertEquals(5.5, item.getPrice());
					assertEquals(-1, item.getUserID());
				}
			}
			
			assertEquals(found1,true);
			assertEquals(found2,true);
		} catch (ConnectionError e) {
			Log.d("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		}
	}
	
	public void testSync() {
		try {
			
			remote.sync();
			
			List<Item> items = remote.getAll();
			boolean found1 = false;
			boolean found2 = false;
			boolean found3 = false;
			
			for (Item item : items) {
				if ("001".equals(item.getId())) {
					found1 = true;
				} else if ("002".equals(item.getId())) {
					found2 = true;
				} else if ("003".equals(item.getId())) {
					found3 = true;
				}
			}
			
			assertEquals(found1,true);
			assertEquals(found2,true);
			assertEquals(found3,true);
		} catch (ConnectionError e) {
			Log.d("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		}
	}
	
	public void testGetItem() {
		try {
			Item item = remote.getItem("001");
			assertNotNull(item);
			assertEquals("001",item.getId());
			assertEquals("Item 1",item.getDescription());
			String x = df.format(item.getPrice());
			assertEquals("9.99", x);
		} catch (ConnectionError e) {
			Log.d("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		} catch (NoItemFoundException e) {
			Log.d("JUNIT_TEST",e.getMessage());
			assertTrue(false);
		}
	}
	
	public void setUp() {
		remote = new ItemRemoteStorage("XX");
	}
	
}
