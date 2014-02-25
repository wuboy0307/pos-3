package edu.txstate.pos.storage;

/**
 * The user record wasn't found.
 *
 */
public class NoUserFoundException extends StorageException {
	public NoUserFoundException (String message) {
		super(message);
	}
}
