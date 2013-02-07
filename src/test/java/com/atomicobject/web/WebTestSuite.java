package com.atomicobject.web;

import com.atomicobject.web.helpers.WebTestHelper;
import com.atomicobject.web.suite.PeopleAPITest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PeopleAPITest.class
})
public class WebTestSuite {

    @BeforeClass
    public static void beforeSuite() {
        try {
            WebTestHelper.buildWarAndStartJettyFromSuite();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @AfterClass
    public static void afterSuite() {
        try {
            WebTestHelper.stopJetty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
