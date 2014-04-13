package edu.txstate.pos.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.txstate.db.POSContract;
import edu.txstate.pos.callback.ServiceCallback;
import edu.txstate.pos.storage.CartLocalStorage;
import edu.txstate.pos.storage.NoCartFoundException;
import edu.txstate.pos.storage.StorageException;
import edu.txstate.pos.storage.SyncStatus;

/**
 * Cart is more than just a simple wrapper for the cart information.  It
 * makes the calls to the CartLocalStorage object so that every time a value
 * is set on the object, it is saved into the database.  The amounts are also
 * updated any time the items and tax rates are changed.
 * 
 * The amounts kept in the cart are:
 * sub total: the total of all the items
 * tax amount:  the sub total times the tax rate
 * total:  the sub total plus tax amount
 * 
 * When a Cart is created, it is in 'draft' mode.  
 * 
 * A Cart is associated to a User.  For any User, there can be only one Cart in
 * 'draft' mode.
 * 
 * When the cart contains items, the customer email, the user, and a payment, then
 * the cart is considered valid and you can sell() the cart.  That will take the 
 * 'draft' status off the cart and put it into 'push' so that the background thread
 * can sync the data to the server.  Since the cart is no longer in draft, the user
 * can create another one.
 * 
 * To remove an item, use the updateQuantity() method and set the quantity to 0.
 *
 */
public class Cart implements POSModel {
	
	public static final String LOG_TAG = "CART";
	
	public static MathContext mathContext = new MathContext(2,RoundingMode.UP);
	
	// -1 means this is an invalid cart
	protected long id = -1;
	protected User user;
	protected Payment payment;
	protected List<CartItem> items;
	protected String customerEmail;
	protected Signature signature;
	protected String taxAmount = "0";
	protected String taxRate = "0";
	protected String subTotal = "0";
	protected String total = "0";
	
	protected CartLocalStorage storage = null;
	protected ServiceCallback syncService = null;
	
	/**
	 * Constructor.
	 * 
	 * @param db local database
	 * @param taxRate the current tax rate
	 * @param user user creating the cart
	 * @throws StorageException
	 */
	public Cart(SQLiteDatabase db, String taxRate, User user, ServiceCallback syncService) throws StorageException {
		this.user = user;
		this.syncService = syncService;
		
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

		calculate();
	}
	
	/**
	 * Constructor for use by RemoteCart.
	 * 
	 */
	protected Cart() {
		
	}

	/**
	 * Utility method to get the ContentValues with the tax rate and amounts.  Anything
	 * changed beyond this can be added before sending to the update() method.
	 * 
	 * @return the ContentValues
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
	 * Updates the cart in the database with the given ContentValues.
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
	
	/**
	 * Updates the amounts.
	 * 
	 * @throws StorageException
	 */
	private void calculate() throws StorageException {
		// Calculate the subtotal by the prices of the items
		BigDecimal dSubTotal = new BigDecimal("0");
		for (CartItem ci : items) {
			Log.d(LOG_TAG, "Item Price: " + ci.getItem().getPrice() + " x " + ci.getQuantity());
			BigDecimal dItem = new BigDecimal(ci.getItem().getPrice());
			dItem = dItem.multiply(new BigDecimal(ci.getQuantity()));
			dSubTotal = dSubTotal.add(dItem);
		}
		subTotal = dSubTotal.toString();
		Log.d(LOG_TAG, "Subtotal: " + subTotal);
		
		// Calculate the tax amount
		BigDecimal dTaxRate = new BigDecimal(taxRate);
		BigDecimal dTaxAmount = dTaxRate.multiply(dSubTotal);
		dTaxAmount = dTaxAmount.round(mathContext);
		taxAmount = dTaxAmount.toString();
		Log.d(LOG_TAG,"Tax amount: " + taxAmount);
				
		BigDecimal dTotal = new BigDecimal(subTotal);
		dTotal = dTotal.add(dTaxAmount);
		
		total = dTotal.toString();
		Log.d(LOG_TAG, "Total: " + total);
		
		update(getValues());
	}

	/**
	 * Finds the CartItem in the items list that matches the given
	 * item.
	 * 
	 * @param item the item being looked for
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
	 * Sets the tax rate
	 * 
	 * @param taxRate
	 * @throws StorageException
	 */
	public void setTax(String taxRate) throws StorageException {
		this.taxRate = taxRate;
		calculate();
	}

	/**
	 * Add the payment for the cart
	 * 
	 * @param payment the payment card
	 * @throws StorageException
	 */
	public void addPayment(Payment payment) throws StorageException {
		ContentValues values = getValues();
		values.put(POSContract.Cart.COLUMN_NAME_PAYMENT_CARD, payment.getCardNumber());
		if (payment.getPin() != null)
			values.put(POSContract.Cart.COLUMN_NAME_PAYMENT_PIN, payment.getPin());
		
		update(values);
	}
		
	/**
	 * Add an item to the cart
	 * 
	 * @param item	item to add
	 * @param quantity quantity of that item
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
	 * Updates the item quantity on the cart.  Set the quantity to 
	 * zero to remove the item from the cart.
	 * 
	 * @param item  Item to update
	 * @param quantity quantity to set to
	 * @throws StorageException
	 */
	public void updateQuantity(Item item, int quantity) throws StorageException {
		CartItem ci = findItem(item);
		if (quantity <= 0) {
			if (ci != null) {
				storage.deleteItem(id, item.getId());
				items.remove(ci);
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
	 * Sets the email address of the customer making the purchase.
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

	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (customerEmail != null 
				&& items.size() > 0
				&& user != null
				&& id > -1) {
			return true;
		}
		return false;
	}

	/**
	 * When the cart is complete, sell() it.  The payment will
	 * be processed.
	 * 
	 * @return true if all the fields are filled in, and the payment is valid
	 * @throws StorageException
	 */
	public boolean sell() throws StorageException {
		if (!isValid()) return false;
		//calculate();
		ContentValues values = getValues();
		values.put(POSContract.Cart.COLUMN_NAME_SYNC,SyncStatus.PUSH);
		update(values);
		syncService.push();
		return true;
	}
	
	// *********************************
	//	Getters
	// *********************************
	
	/**
	 * Gets the quantity for a specific item.  This is needed
	 * for the Junit tests.
	 * 
	 * @param item  The item to look for.
	 * @return
	 * @throws StorageException 
	 */
	public int getItemQuatity(Item item) throws StorageException {
		CartItem ci = findItem(item);
		if (ci == null) return 0;
		else return ci.getQuantity();
	}
	
	/**
	 * 
	 * @return the tax rate
	 */
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

	/**
	 * 
	 * @return the User
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 
	 * @return the payment
	 */
	public Payment getPayments() {
		return payment;
	}

	/**
	 * 
	 * @return the items in the cart
	 */
	public List<CartItem> getItems() {
		return items;
	}

	/**
	 * 
	 * @return the customer email
	 */
	public String getCustomer() {
		return customerEmail;
	}

	/**
	 * 
	 * @return the signature object
	 */
	public Signature getSignature() {
		return signature;
	}

}
