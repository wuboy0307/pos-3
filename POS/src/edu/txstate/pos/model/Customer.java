package edu.txstate.pos.model;

/**
 * The Customer making the purchase.  They are identified by their
 * email address.
 * 
 */
public class Customer {
	
	private String email = null;
	
	public Customer(String email) {
		this.email = email;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
