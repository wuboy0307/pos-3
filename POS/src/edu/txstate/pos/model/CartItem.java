package edu.txstate.pos.model;

public class CartItem implements POSModel {
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
	
	public boolean equals(Item item) {
		if (this.item.getId() == null || item == null) return false;
		if (this.item.getId().equals(item.getId())) return true;
		else return false;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return item.getDescription() + " - $" + item.getPrice() + " x " + quantity;
	}
}
