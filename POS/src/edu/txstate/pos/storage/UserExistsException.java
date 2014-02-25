package edu.txstate.pos.storage;

/**
 * The user already exists.
 * 
 */
public class UserExistsException extends StorageException {

	public UserExistsException() {
	}

	public UserExistsException(String message) {
		super(message);
	}

}
