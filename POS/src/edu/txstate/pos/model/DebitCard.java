package edu.txstate.pos.model;

public class DebitCard extends Payment {
	
	private String pin = null;
	
	public DebitCard(String cardNumber) {
		super(cardNumber);
	}
	
	public DebitCard(String cardNumber, String pin) {
		super(cardNumber);
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
