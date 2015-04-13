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
import com.robotium.solo.Solo;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {


    private Solo solo;
    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.artifex.mupdfdemo.MuPDFActivity";
    private static final String TEST_FILE_NAME = "test.pdf";

    final String CURRENT_NAME_1 = "CurrentScreenshotFile_Page1";
    final String ORIGINAL_NAME_1 = "OriginalScreenshotFile_Page1";
    final String CURRENT_NAME_2 = "CurrentScreenshotFile_Page2";
    final String ORIGINAL_NAME_2 = "OriginalScreenshotFile_Page2";

    final File originalScreenFile1 = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", ORIGINAL_NAME_1 + ".jpg");
    final File currentScreenFile1 = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", CURRENT_NAME_1 + ".jpg");
    final File originalScreenFile2 = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", ORIGINAL_NAME_2 + ".jpg");
    final File currentScreenFile2 = new File(Environment.getExternalStorageDirectory() + "/Robotium-Screenshots/", CURRENT_NAME_2 + ".jpg");
    final int TIMEOUT = 2000;
    int deviceWidth;
    int deviceHeight;

    Context context;


    public ApplicationTest() throws ClassNotFoundException {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {

        super.setUp();

        solo = new Solo(getInstrumentation(), getActivity());
        context = this.getInstrumentation().getTargetContext().getApplicationContext();
            //copy test.pdf from asset to sdcard of emulator
        copyAssets();
        clearHistory();
        getDeviceProperty();
            //set target file path
        String path = Environment.getExternalStorageDirectory().toString() + "/" + TEST_FILE_NAME;
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(getActivity().getApplicationContext(), MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        setActivityIntent(intent);
        getActivity().startActivity(intent);

        solo.waitForActivity(MuPDFActivity.class);
        solo.sleep(TIMEOUT);

    }

    private void getDeviceProperty() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        deviceWidth = display.getWidth();
        deviceHeight = display.getHeight();
    }

    private void clearHistory() {
        //remove first page history image.
        if (currentScreenFile1.exists()) {
            currentScreenFile1.delete();
        }
        //remove second page history image.
        if (currentScreenFile2.exists()) {
            currentScreenFile2.delete();
        }

    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    @SmallTest
    public void testStartRun() {
        //first page test
        if (originalScreenFile1.exists()) {
            solo.takeScreenshot(CURRENT_NAME_1);
        } else {
            solo.takeScreenshot(ORIGINAL_NAME_1);
        }
             //scroll to next page.
        final float fromX = deviceWidth / 2;
        float toX = fromX;
        final float fromY = deviceHeight / 2;
        float toY = deviceHeight / 4;
        solo.drag(fromX,toX,fromY,toY,1);

        solo.sleep(TIMEOUT);
        if (originalScreenFile2.exists()) {
            solo.takeScreenshot(CURRENT_NAME_2);
        } else {
            solo.takeScreenshot(ORIGINAL_NAME_2);
        }
        solo.sleep(TIMEOUT);
        compare();
    }

    public void compare() {
        if ((originalScreenFile1.exists()) && (currentScreenFile1.exists())) {
            assertTrue("First Image Matched", compareScreenShot(originalScreenFile1, currentScreenFile1));
        }

        if ((originalScreenFile2.exists()) && (currentScreenFile2.exists())) {
            assertTrue("Next Image Matched", compareScreenShot(originalScreenFile2, currentScreenFile2));
        }
    }

    //compare two jpeg files
    private boolean compareScreenShot(File file1, File file2) {
        try {
            Bitmap originalBmp = BitmapFactory.decodeFile(file1.getPath());
            Bitmap currentBmp = BitmapFactory.decodeFile(file2.getPath());

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
                    int g1 = (rgb1 >> 8) & 0xff;
                    int b1 = (rgb1) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    int b2 = (rgb2) & 0xff;
                    diff += Math.abs(r1 - r2);
                    diff += Math.abs(g1 - g2);
                    diff += Math.abs(b1 - b2);
                }
            }
            double n = width1 * height1 * 3;
            double p = diff / n / 255.0;
            //0 = < p < = 1
            //the smaller p is , the correctly images match
            return (p < 10);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
        //COPY assets from assets holder to sdcard
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