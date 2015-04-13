package com.demo.pdf.test;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.demo.pdf.MainActivity;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;


import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {


        private Solo solo;
        private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.demo.pdf.MainActivity";
        private static final String TEST_FILE_NAME = "test.pdf";
        final File originalScreenFile = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", "OriginalScreenshotFile" + ".jpg");
        final File currentScreenFile = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", "CurrentScreenshotFile" + ".jpg");
        final int TIMEOUT = 2000;
        final String CURRENT_NAME = "CurrentScreenshotFile";
        final String ORIGINAL_NAME = "OriginalScreenshotFile";
        private static Class<?> MuPDFActivityClass;
        Context context;

        static{
            try {
                MuPDFActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public ApplicationTest() throws ClassNotFoundException {
            super(MainActivity.class);
        }

        @Override
        public void setUp() throws Exception {

            super.setUp();

            solo = new Solo(getInstrumentation(),getActivity());
            context = this.getInstrumentation().getTargetContext().getApplicationContext();

            copyAssets();

         //   String path = "/storage/emulated/0/Android/data/com.demo.pdf/files/test.pdf";
            String path = Environment.getExternalStorageDirectory().toString() +"/"+ TEST_FILE_NAME;
            Uri uri = Uri.parse(path);
            if(currentScreenFile.exists()){
                currentScreenFile.delete();
            }

            Intent intent = new Intent(getActivity().getApplicationContext(), MuPDFActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);


            setActivityIntent(intent);
            getActivity().startActivity(intent);
            solo.waitForActivity(MuPDFActivity.class);
            solo.sleep(2000);

        }
        @Override
        public void tearDown() throws Exception {
            solo.finishOpenedActivities();
            super.tearDown();
        }

        @SmallTest
        public void testStartRun() {
            final int TIMEOUT = 1000;

            if(originalScreenFile.exists()){
                solo.takeScreenshot(CURRENT_NAME);
                compareScreenShot();
            }else{
                solo.takeScreenshot(ORIGINAL_NAME);
            }
        }

    private void compareScreenShot() {
         //wait for screenshot
         assertTrue(solo.waitForCondition(new Condition() {
                @Override
                public boolean isSatisfied() {
                    return currentScreenFile.exists();
                }
            }, TIMEOUT));

        try {
            Bitmap originalBmp = BitmapFactory.decodeFile(originalScreenFile.getPath());
            Bitmap currentBmp = BitmapFactory.decodeFile(currentScreenFile.getPath());

            int width1 = originalBmp.getWidth();
            int width2 = currentBmp.getWidth();
            int height1 = originalBmp.getHeight();
            int height2 = currentBmp.getHeight();
            if ((width1 != width2) || (height1 != height2)) {
                System.err.println("Error: Images dimensions mismatch");
                System.exit(1);
            }

            long diff = 0;
            for (int y = 0; y < height1; y++) {
                for (int x = 0; x < width1; x++) {
                    int rgb1 = originalBmp.getPixel(x, y);
                    int rgb2 = currentBmp.getPixel(x, y);
                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >>  8) & 0xff;
                    int b1 = (rgb1      ) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >>  8) & 0xff;
                    int b2 = (rgb2      ) & 0xff;
                    diff += Math.abs(r1 - r2);
                    diff += Math.abs(g1 - g2);
                    diff += Math.abs(b1 - b2);
                }
            }
            double n = width1 * height1 * 3;
            double p = diff / n / 255.0;
            Assert.assertTrue(p * 100 < 5 );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //http://rosettacode.org/wiki/Percentage_difference_between_images
    private void copyAssets() {
            AssetManager assetManager = context.getAssets();
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
                        File outFile = new File(Environment.getExternalStorageDirectory().toString(), filename);
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