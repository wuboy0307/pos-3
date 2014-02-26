package edu.txstate.pos.model;

public class CartItem {
	private Item item;
	private int quantity;
	
	public CartItem(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}
}
