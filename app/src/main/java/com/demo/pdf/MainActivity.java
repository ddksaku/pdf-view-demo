package com.demo.pdf;


import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.artifex.mupdfdemo.MuPDFActivity;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends Activity {

    private static final String TEST_FILE_NAME = "test.pdf";
    private static final int PICKFILE_RESULT_CODE = 10;
    Intent intent;
    File outFile;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
            setContentView(R.layout.activity_main);

            Button showPDFBtn = (Button)findViewById(R.id.btn_show_pdf);
            showPDFBtn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                            openFileBrowser();
                         }
                 });
         }

    private void openFileBrowser() {
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("application/pdf");
        try {
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        if (data == null)
            return;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    //FilePath is your file as a string
                    showPdfContent(FilePath);
                }
        }
    }
    private void showPdfContent(String filePath) {

        Uri uri = Uri.parse(filePath);


        Intent intent = new Intent(this, MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }
    public static File getFilesDirectory(Context context) {
        File appFilesDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appFilesDir = getExternalFilesDir(context);
        }
        if (appFilesDir == null) {
            appFilesDir = context.getFilesDir();
        }
        return appFilesDir;
    }
    private static File getExternalFilesDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appFilesDir = new File(new File(dataDir, context.getPackageName()), "files");
        if (!appFilesDir.exists()) {
            if (!appFilesDir.mkdirs()) {
                //L.w("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appFilesDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                //L.i("Can't create \".nomedia\" file in application external cache directory");
                return null;
            }
        }
        return appFilesDir;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

