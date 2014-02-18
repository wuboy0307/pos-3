package edu.txstate.pos.storage;

public class NoItemFoundException extends Exception {
	public NoItemFoundException() { super(); }
	public NoItemFoundException(String message) { super(message); }
}
