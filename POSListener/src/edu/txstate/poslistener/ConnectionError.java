package edu.txstate.poslistener;

/**
 * Error thrown when a connection problem occurs.
 * 
 * @author gmarinsk
 *
 */
public class ConnectionError extends Exception {
	public ConnectionError() { super(); }
	public ConnectionError(String message) { super(message); }
}
