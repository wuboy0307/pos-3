package test.edu.txstate.pos.storage.remote;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class)
        .includeAllPackagesUnderHere()
        .build();
    }
}