package com.artifex.mupdfdemo;
/**
 * Created by admin on 3/24/2015.
 */
import android.graphics.Bitmap;


public class BitmapHolder {
    private Bitmap bm;


    public BitmapHolder() {
        bm = null;
    }


    public synchronized void setBm(Bitmap abm) {
        if (bm != null && bm != abm)
            bm.recycle();
        bm = abm;
    }


    public synchronized void drop() {
        bm = null;
    }


    public synchronized Bitmap getBm() {
        return bm;
    }
}