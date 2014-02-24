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

public class CartTest extends AndroidTestCase {
	
	public static final String LOG_TAG = "JUNIT_TEST_CART";
		
	private CartLocalStorage local = null;
	private User updUser = null;
	
	public void testCreate() {
		try {
			local.deleteCart(updUser);
		} catch (SQLException e) {
			// Don't care - need clean slate
		}
		
		try {
			Cart cart = local.createCart(updUser);
			Log.d(LOG_TAG, "CART ID: " + cart.getId());
			assertTrue(cart.getId() > -1);
		} catch (SQLException e) {
			Log.d(LOG_TAG,e.getMessage());
			assertTrue(false);
		}
	}

	public void setUp() {
	    SQLiteOpenHelper dbHelper = new POS_DBHelper(getContext());
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
		local = new CartLocalStorage(db);
		updUser = new User("JUNIT","JUNIT");
		updUser.setId(-1);	
	}
}
