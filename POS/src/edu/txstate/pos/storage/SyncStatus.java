package edu.txstate.pos.storage;

/**
 * The SyncStatus tells the background process what should
 * be done to synchronize the local data with the POS server.
 *
 */
public final class SyncStatus {
	// The item is in sync
	public static final int DONE = 0;
	// Applies to Cart - in this status while
	// it is being built
	public static final int DRAFT = 1;
	// Push this data to the server
	public static final int PUSH = 2;
	// Delete this item from the server
	public static final int DELETE = 3;
}
