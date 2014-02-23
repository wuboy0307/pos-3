package edu.txstate.pos.model;

import java.math.BigDecimal;


public class Item {
	private String id;
	private String price;
	private String description;
	private int userID;
	private int syncStatus;
	
	public Item(String id, String description, String price) {
		this.description = description;
		setPrice(price);
		this.id = id;
		
	}
	
	public Item(String id, String description, String price, int userID) {
		this.description = description;
		setPrice(price);
		this.id = id;
		this.userID = userID;
	}
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPrice() {
		return price;
	}
	
	public BigDecimal getBigDecimalPrice() {
		if (price == null) price = "0";
		return new BigDecimal(price);
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getUserID() {
		return userID;
	}
	
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
}
