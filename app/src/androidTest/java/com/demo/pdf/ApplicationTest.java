package com.demo.pdf;

import android.test.ActivityInstrumentationTestCase2;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.robotium.solo.Solo;


import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@SuppressWarnings("rawtypes")
public class ApplicationTest extends ActivityInstrumentationTestCase2<MuPDFActivity> {


        private Solo solo;
        private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.demo.pdf.MainActivity";
        private static final String TEST_FILE_NAME = "test.pdf";

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
            super(MuPDFActivity.class);
        }

        public void setUp() throws Exception {

            super.setUp();
            solo=newSolo(getInstrumentation(),getActivity());


        }
        @Override
        public void tearDown() throws Exception {
            solo.finishOpenedActivities();
            super.tearDown();
        }
        public void testRun() {
            copyAssets();

            String path = "/storage/emulated/0/Android/data/com.mupdfdemo/files/test.pdf"
            core = openFile(path);

            solo.takeScreenshot();
        }
        private void copyAssets() {
            AssetManager assetManager = getAssets();
            String[] files = null;
            try {
                files = assetManager.list("");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }

            for(String filename : files) {
                if (filename.compareTo(TEST_FILE_NAME) == 0) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        outFile = new File(getExternalFilesDir(getApplicationContext()), filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch (IOException e) {
                        Log.e("tag", "Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                }
            }
        }
        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        }

}