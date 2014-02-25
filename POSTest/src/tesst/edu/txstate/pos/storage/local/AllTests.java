package tesst.edu.txstate.pos.storage.local;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

/**
 * Runs all of the tests in this package
 *
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class)
        .includeAllPackagesUnderHere()
        .build();
    }
}
