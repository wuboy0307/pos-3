package edu.txstate.pos.storage;

public class ConnectionError extends StorageException {
	public ConnectionError (String message) {
		super(message);
	}
}
