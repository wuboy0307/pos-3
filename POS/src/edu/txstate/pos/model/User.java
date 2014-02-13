package edu.txstate.pos.model;

public class User {
	
	private boolean isAdmin = false;
	private String PIN;
	private boolean isActive = true;
	private int id = -1;
	private String login;
	
	public User() {
		
	}
	
	public User (String login, String PIN) {
		this.login = login;
		this.PIN = PIN;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public void setAdmin(String isAdmin) {
		this.isAdmin = ("Y".equals(isAdmin));
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setActive(String isActive) {
		this.isActive = ("Y".equals(isActive));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		if (this.login == null || this.id != -1) {
			this.login = login;
		}
	}

	public String getPIN() {
		return PIN;
	}

	public void setPIN(String pIN) {
		PIN = pIN;
	}


}
