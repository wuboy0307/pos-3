package edu.txstate.pos.storage;

/**
 * No item was found.
 * 
 */
public class NoItemFoundException extends Exception {
	public NoItemFoundException() { super(); }
	public NoItemFoundException(String message) { super(message); }
}
