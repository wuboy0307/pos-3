package edu.txstate.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.txstate.db.POSContract;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.StorageException;

public class Cart {
	
	public static final String LOG_TAG = "CART";
	
	// -1 means this is an invalid cart
	private long id = -1;
	private User user;
	private Payment payment;
	private List<CartItem> items;
	private String customerEmail;
	private Signature signature;
	private String taxAmount = "0";
	private String taxRate = "0";
	private String subTotal = "0";
	private String total = "0";
	
	private CartLocalStorage storage = null;
	
	/**
	 * 
	 * @param db
	 * @param taxRate
	 * @param user
	 * @throws StorageException
	 */
	public Cart(SQLiteDatabase db, String taxRate, User user) throws StorageException {
		this.user = user;
		
		storage = new CartLocalStorage(db);
		
		try {
			Map<String,String> cart = storage.getCart(user);
			Log.d(LOG_TAG, "Pulled existing cart");
			id = Long.valueOf(cart.get(POSContract.Cart._ID));
			
			String val = cart.get(POSContract.Cart.COLUMN_NAME_CUSTOMER_ID);
			if (val != null) 
				customerEmail = val;
			
			val = cart.get(POSContract.Cart.COLUMN_NAME_PAYMENT_CARD);
			if (val != null) 
				payment = new Payment(val);
		
			// Get the items
			items = storage.getItems(id);
		
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new StorageException(e.getMessage());
		} catch (NoCartFoundException e) {
			Log.d(LOG_TAG, "Created new cart");
			id = storage.createCart(user, taxRate);
			items = new ArrayList<CartItem>();
		}
		
		this.taxRate = taxRate;
		
	}
	
	/**
	 * 
	 * @return
	 */
	private ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(POSContract.Cart.COLUMN_NAME_TAX_RATE, taxRate);
		values.put(POSContract.Cart.COLUMN_NAME_TAX_AMOUNT, taxAmount);
		values.put(POSContract.Cart.COLUMN_NAME_SUBTOTAL, subTotal);
		values.put(POSContract.Cart.COLUMN_NAME_TOTAL, total);
		return values;
	}
	
	/**
	 * 
	 * @param values
	 * @throws StorageException
	 */
	private void update(ContentValues values) throws StorageException {
		try {
			storage.updateCart(id, values);
		} catch (SQLException e) {
			throw new StorageException(e.getMessage());
		}
	}
	
	private void calculate() throws StorageException {
		// TODO
		BigDecimal dTaxRate = new BigDecimal(taxRate);
		
		// Calculate the subtotal by the prices of the items
		BigDecimal dSubTotal = new BigDecimal("0");
		for (CartItem ci : items) {
			Log.d(LOG_TAG, "Item Price: " + ci.getItem().getPrice());
			BigDecimal dItem = new BigDecimal(ci.getItem().getPrice());
			dItem.multiply(new BigDecimal(ci.getQuantity()));
			Log.d(LOG_TAG, "dItem Price: " + dItem);
			dSubTotal = dSubTotal.add(dItem);
		}
		subTotal = dSubTotal.toString();
		Log.d(LOG_TAG, "Subtotal: " + subTotal);
		
		update(getValues());
	}

	/**
	 * 
	 * @param item
	 * @return
	 * @throws StorageException
	 */
	private CartItem findItem(Item item) throws StorageException {
		CartItem ret = null;
		for (CartItem c : items) {
			if (c.equals(item)) {
				return c;
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param taxRate
	 * @throws StorageException
	 */
	public void setTax(String taxRate) throws StorageException {
		this.taxRate = taxRate;
		calculate();
	}

	/**
	 * 
	 * @param payment
	 * @throws StorageException
	 */
	public void addPayment(Payment payment) throws StorageException {
		ContentValues values = getValues();
		values.put(POSContract.Cart.COLUMN_NAME_PAYMENT_CARD, payment.getCardNumber());
		if (payment instanceof DebitCard) {
			values.put(POSContract.Cart.COLUMN_NAME_PAYMENT_PIN, ((DebitCard) payment).getPin());
		}
		update(values);
	}
		
	/**
	 * 
	 * @param item
	 * @param quantity
	 * @throws StorageException
	 */
	public void addItem(Item item, int quantity) throws StorageException {
		try {
			if (quantity > 0) {
				CartItem ci = findItem(item);
				if (ci != null) {
					int newQuantity = quantity + ci.getQuantity();
					storage.updateItem(id, item.getId(), newQuantity);
					ci.setQuantity(newQuantity);
				} else {
					storage.addItem(id, item.getId(), quantity);
					CartItem i = new CartItem(item,quantity);
					items.add(i);
				}
			}
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new StorageException(e.getMessage());
		}
		calculate();
	}
	
	/**
	 * 
	 * @param item
	 * @param quantity
	 * @throws StorageException
	 */
	public void updateQuantity(Item item, int quantity) throws StorageException {
		CartItem ci = findItem(item);
		if (quantity <= 0) {
			if (ci != null) {
				storage.deleteItem(id, item.getId());
				items.remove(item);
			} else {
				// do nothing
			}
		} else {
			if (ci != null) {
				storage.updateItem(id, item.getId(), quantity);
				ci.setQuantity(quantity);
			} else {
				storage.addItem(id, item.getId(), quantity);
				CartItem i = new CartItem(item,quantity);
				items.add(i);
			}
		}
		calculate();
	}
	
	/**
	 * 
	 * @param customerEmail
	 * @throws StorageException
	 */
	public void setCustomer(String customerEmail) throws StorageException {
		this.customerEmail = customerEmail;
		ContentValues values = getValues();
		values.put(POSContract.Cart.COLUMN_NAME_CUSTOMER_ID, customerEmail);
		update(values);
	}
	
	// TODO
	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public boolean isValid() {
		// TODO:  This is where you'd check to see if all the values
		// were given (customer, signature, tax), there is at least one
		// item and the payments add up to the total for the items.
		return true;
	}

	public boolean sell() throws StorageException {
		if (!isValid()) return false;
		calculate();
		return true;
	}
	
	// *********************************
	//	Getters
	// *********************************
	public String getTaxRate() {
		return taxRate;
	}
	
	/**
	 * @return the taxAmount
	 */
	public String getTaxAmount() {
		return taxAmount;
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
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
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

	public String getCustomer() {
		return customerEmail;
	}

	public Signature getSignature() {
		return signature;
	}

}
