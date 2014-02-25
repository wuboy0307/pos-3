package edu.txstate.pos.model;

public class Payment {

	private String cardNumber = null;
	
	public Payment(String cardNumber) {
		this.cardNumber = cardNumber;
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
}
