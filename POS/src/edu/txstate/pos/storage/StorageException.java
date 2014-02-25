package edu.txstate.pos.storage;

/**
 * Thrown when there is an issue with the storage
 * operation.
 * 
 */
public class StorageException extends Exception {
	public StorageException() {
		
	}
	public StorageException(String message) {
		super(message);
	}
}
