package test.edu.txstate.pos.service;

import edu.txstate.pos.storage.Ping;
import android.test.AndroidTestCase;

/**
 * Tests the ping functionality plus the ability
 * to fake down conditions.
 *
 */
public class PingTest extends AndroidTestCase {
	private Ping hb = null;
	
	/**
	 * Basic ping test
	 */
	public void testPing() {
		assertTrue(hb.ping());
	}
	
	/**
	 * Junit setup method...runs before every test.
	 */
	public void setUp() {
		hb = new Ping("XX");
	}
}
