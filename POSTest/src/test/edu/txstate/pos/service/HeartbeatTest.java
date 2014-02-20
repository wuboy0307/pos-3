package test.edu.txstate.pos.service;

import edu.txstate.pos.storage.Heartbeat;
import android.test.AndroidTestCase;

public class HeartbeatTest extends AndroidTestCase {
	private Heartbeat hb = null;
	
	public void testPing() {
		assertTrue(hb.ping());
	}
	public void setUp() {
		hb = new Heartbeat("XX");
	}
}
