package edu.txstate.pos.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
	
	// -1 means this is an invalid cart
	private long id = -1;
	private User user;
	private List<Payment> payments;
	private List<Item> items;
	private Customer customer;
	private Signature signature;
	private long tax;

	public Cart(User user, long id) {
		this.user = user;
		payments = new ArrayList<Payment>();
		items = new ArrayList<Item>();
	}
	
	public long getTax() {
		return tax;
	}

	public void setTax(long tax) {
		this.tax = tax;
	}

	public User getUser() {
		return user;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public List<Item> getItems() {
		return items;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Signature getSignature() {
		return signature;
	}

	public void addPayment(Payment payment) {
		
	}
	
	public void addItem(Item item) {
		
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public void setSignature(Signature signature) {
		this.signature = signature;
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
