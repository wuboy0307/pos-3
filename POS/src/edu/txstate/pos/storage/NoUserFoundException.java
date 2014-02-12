package edu.txstate.pos.storage;

public class NoUserFoundException extends StorageException {
	public NoUserFoundException (String message) {
		super(message);
	}
}
