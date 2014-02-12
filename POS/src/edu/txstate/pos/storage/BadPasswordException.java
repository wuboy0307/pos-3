package edu.txstate.pos.storage;

public class BadPasswordException extends StorageException {
	public BadPasswordException (String message) {
		super(message);
	}
}
