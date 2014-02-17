package edu.txstate.pos.model;


public class Item {
	private String id;
	private float price;
	private String description;
	private int userID;
	
	public Item(String id, String description, float price, int userID) {
		this.userID = userID;
		this.description = description;
		this.price = price;
		this.id = id;
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(float price) {
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
}
