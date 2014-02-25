package edu.txstate.pos.storage;

/**
 * The given password is incorrect.
 * 
 */
public class BadPasswordException extends StorageException {
	public BadPasswordException (String message) {
		super(message);
	}
}
