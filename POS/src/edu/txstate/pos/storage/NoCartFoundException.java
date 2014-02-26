package edu.txstate.pos.storage;

/**
 * No item was found.
 * 
 */
public class NoCartFoundException extends Exception {
	public NoCartFoundException() { super(); }
	public NoCartFoundException(String message) { super(message); }
}
