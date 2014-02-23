package test.edu.txstate.pos.service;

import edu.txstate.pos.storage.Ping;
import android.test.AndroidTestCase;

public class PingTest extends AndroidTestCase {
	private Ping hb = null;
	
	public void testPing() {
		assertTrue(hb.ping());
	}
	public void setUp() {
		hb = new Ping("XX");
	}
}
