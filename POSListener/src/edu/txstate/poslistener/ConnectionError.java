package edu.txstate.poslistener;

public class ConnectionError extends Exception {
	public ConnectionError() { super(); }
	public ConnectionError(String message) { super(message); }
}
