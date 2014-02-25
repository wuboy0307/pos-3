package edu.txstate.pos.storage;

/**
 * Item already exists.
 * 
 */
public class ItemExistsException extends StorageException {

	public ItemExistsException() {
	}

	public ItemExistsException(String message) {
		super(message);
	}

}
