package edu.txstate.pos.model;

public class Payment implements POSModel {

	private String cardNumber = null;
	private String pin = null;
	
	public Payment(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public Payment(String cardNumber, String pin) {
		this.cardNumber = cardNumber;
		this.pin = pin;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
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
