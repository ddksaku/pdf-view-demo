package com.artifex.mupdfdemo;

/**
 * Created by admin on 3/24/2015.
 */
import android.graphics.RectF;


public class TextWord extends RectF {
    public String w;


    public TextWord() {
        super();
        w = new String();
    }


    public void Add(TextChar tc) {
        super.union(tc);
        w = w.concat(new String(new char[]{tc.c}));
    }
}