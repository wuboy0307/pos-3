package edu.txstate.pos.model;

/**
 * User of the application.
 *
 */
public class User implements POSModel {
	
	private boolean isAdmin = false;
	private String PIN;
	private boolean isActive = true;
	private int id = -1;
	private String login;
	
	/**
	 * Constructor.
	 */
	public User() {
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param login
	 * @param PIN
	 */
	public User (String login, String PIN) {
		this.login = login;
		this.PIN = PIN;
	}
	
	/**
	 * If the user is an administrator.
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * Set the administrator flag
	 * 
	 * @param isAdmin
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	/**
	 * Set the admin flag as a Y/N
	 * 
	 * @param isAdmin
	 */
	public void setAdmin(String isAdmin) {
		this.isAdmin = ("Y".equals(isAdmin));
	}

	/**
	 * If the user is active
	 * 
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Set the user activity flag
	 * 
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Set the active flag with a Y/N
	 * 
	 * @param isActive
	 */
	public void setActive(String isActive) {
		this.isActive = ("Y".equals(isActive));
	}

	/**
	 * Get the user ID
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the user ID
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the user's login
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Set the user's login
	 * 
	 * @param login
	 */
	public void setLogin(String login) {
		if (this.login == null || this.id != -1) {
			this.login = login;
		}
	}

	/**
	 * Get the user's PIN (password)
	 * @return
	 */
	public String getPIN() {
		return PIN;
	}

	/**
	 * Set the user's PIN (password)
	 * @param pIN
	 */
	public void setPIN(String pIN) {
		PIN = pIN;
	}


}
