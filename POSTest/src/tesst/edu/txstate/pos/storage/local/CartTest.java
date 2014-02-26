package tesst.edu.txstate.pos.storage.local;

import edu.txstate.db.POS_DBHelper;
import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.CartLocalStorage;
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
	private static SQLiteDatabase db = null;
	
	public void testCreate() {
		try {
			local.deleteCart(updUser);
		} catch (SQLException e) {
			// Don't care - need clean slate
		}
		
		Cart cart = new Cart(CartTest.db,"1.05",updUser);
		assertTrue(cart.getId() > 0);
		Log.d(LOG_TAG,"Created cart " + cart.getId());
		
		Cart cart2 = new Cart(CartTest.db,"1.05",updUser);
		assertEquals(cart.getId(),cart2.getId());
		Log.d(LOG_TAG,"Found cart " + cart2.getId());
		
	}

	public void setUp() {
	    SQLiteOpenHelper dbHelper = new POS_DBHelper(getContext());
	    if (CartTest.db == null) {
	    	CartTest.db = dbHelper.getWritableDatabase();
	    }
		local = new CartLocalStorage(db);
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);	
	}
}
