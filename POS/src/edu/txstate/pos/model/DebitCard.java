package edu.txstate.pos.model;

/**
 * Debit card payment method.
 * 
 *
 */
public class DebitCard extends Payment {
	
	// pin for this credit card
	private String pin = null;
	
	/**
	 * Constructor.
	 * 
	 * @param cardNumber
	 * @param pin
	 */
	public DebitCard(String cardNumber, String pin) {
		super(cardNumber);
		this.pin = pin;
	}

	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
}
