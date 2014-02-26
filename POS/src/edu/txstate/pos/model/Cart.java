package edu.txstate.pos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.txstate.db.POSContract;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.NoCartFoundException;

public class Cart {
	
	public static final String LOG_TAG = "CART";
	
	// -1 means this is an invalid cart
	private long id = -1;
	private User user;
	private Payment payment;
	private List<CartItem> items;
	private Customer customer;
	private Signature signature;
	private String taxRate;
	private String subTotal;
	private String total;
	
	private CartLocalStorage storage = null;
	
	public Cart(SQLiteDatabase db, String taxRate, User user) {
		this.user = user;
		
		storage = new CartLocalStorage(db);
		
		try {
			Map<String,String> cart = storage.getCart(user);
			Log.d(LOG_TAG, "Pulled existing cart");
			id = Long.valueOf(cart.get(POSContract.Cart._ID));
			
			String val = cart.get(POSContract.Cart.COLUMN_NAME_CUSTOMER_ID);
			if (val != null) 
				customer = new Customer(val);
			
			val = cart.get(POSContract.Cart.COLUMN_NAME_PAYMENT_CARD);
			if (val != null) 
				payment = new Payment(val);
		
			// Add the items
			
		
			calculate();
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (NoCartFoundException e) {
			Log.d(LOG_TAG, "Created new cart");
			id = storage.createCart(user, taxRate);
			items = new ArrayList<CartItem>();
		}
		
		this.taxRate = taxRate;
		
	}
	
	private void calculate() {
		
	}
	
	public boolean sell() {
		if (!isValid()) return false;
		calculate();
		return true;
	}
	
	public String getTaxRate() {
		return taxRate;
	}

	public void setTax(String taxRate) {
		this.taxRate = taxRate;
		calculate();
	}

	public User getUser() {
		return user;
	}

	public Payment getPayments() {
		return payment;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Signature getSignature() {
		return signature;
	}

	public void addPayment(Payment payment) {
		calculate();
	}
	
	public void addItem(Item item, int quantity) {
		calculate();
	}
	
	public void updateQuantity(Item item, int quantity) {
		
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public void setSignature(Signature signature) {
		this.signature = signature;
	}
	
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @return the subTotal
	 */
	public String getSubTotal() {
		return subTotal;
	}

	public boolean isValid() {
		// TODO:  This is where you'd check to see if all the values
		// were given (customer, signature, tax), there is at least one
		// item and the payments add up to the total for the items.
		return true;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}


}
