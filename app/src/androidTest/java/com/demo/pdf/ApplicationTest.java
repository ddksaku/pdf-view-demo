package com.demo.pdf;

import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;


import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@SuppressWarnings("rawtypes")
public class ApplicationTest extends ActivityInstrumentationTestCase2 {

        private Solo solo;
        private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.demo.pdf.MainActivity";
        private static Class<?> launcherActivityClass;

        static{
            try {
                launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        @SuppressWarnings("unchecked")
        public ApplicationTest() throws ClassNotFoundException {
            super(launcherActivityClass);
        }

        public void setUp() throws Exception {
            super.setUp();
            solo = new Solo(getInstrumentation());
            getActivity();
        }
        @Override
        public void tearDown() throws Exception {
            solo.finishOpenedActivities();
            super.tearDown();
        }
        public void testRun() {

            solo.waitForActivity("MainActivity", 2000);
            solo.clickOnButton("SHOW PDF CONTENT");
            solo.scrollDown();
            solo.scrollToBottom();
            solo.scrollUp();
            solo.takeScreenshot();
        }


}