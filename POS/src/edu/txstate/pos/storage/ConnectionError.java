package edu.txstate.pos.storage;

/**
 * Network connection or JSON processing error for the 
 * remote POS server calls.
 * 
 */
public class ConnectionError extends StorageException {
	public ConnectionError (String message) {
		super(message);
	}
}
