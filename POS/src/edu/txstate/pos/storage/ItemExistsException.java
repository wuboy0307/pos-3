package edu.txstate.pos.storage;

public class ItemExistsException extends StorageException {

	public ItemExistsException() {
	}

	public ItemExistsException(String message) {
		super(message);
	}

}
