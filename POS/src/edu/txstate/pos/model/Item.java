package edu.txstate.pos.model;

import java.math.BigDecimal;

/**
 * Item is something to be purchased.
 * 
 */
public class Item {
	private String id;
	private String price;
	private String description;
	private int userID;
	private int syncStatus;
	
	/**
	 * Constructor
	 * 
	 * @param id  The string of the UPC code
	 * @param description
	 * @param price
	 */
	public Item(String id, String description, String price) {
		this.description = description;
		setPrice(price);
		this.id = id;
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param description
	 * @param price
	 * @param userID  The user ID that created it.
	 */
	public Item(String id, String description, String price, int userID) {
		this.description = description;
		setPrice(price);
		this.id = id;
		this.userID = userID;
	}
	
	/**
	 * Get the item ID
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the item ID
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the item price.
	 * 
	 * @return
	 */
	public String getPrice() {
		return price;
	}
	
	public BigDecimal getBigDecimalPrice() {
		if (price == null) price = "0";
		return new BigDecimal(price);
	}
	
	/**
	 * Set the price
	 * 
	 * @param price
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	/**
	 * Get the description.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/** 
	 * Get the user ID that created this item.
	 * 
	 * @return
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * Set the user ID
	 * 
	 * @param userID
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @return the syncStatus
	 */
	public int getSyncStatus() {
		return syncStatus;
	}

	/**
	 * @param syncStatus the syncStatus to set
	 */
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	}
	
	@Override
	public boolean equals(Object item) {
		if (item instanceof Item) {
			return (item != null && ((Item) item).getId().equals(id));
		} else return false;
	}
}
