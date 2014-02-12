package edu.txstate.pos.storage;

public class UserExistsException extends StorageException {

	public UserExistsException() {
	}

	public UserExistsException(String message) {
		super(message);
	}

}
