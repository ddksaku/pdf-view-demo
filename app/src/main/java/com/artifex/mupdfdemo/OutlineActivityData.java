package com.artifex.mupdfdemo;

/**
 * Created by admin on 3/24/2015.
 */
public class OutlineActivityData {
    public OutlineItem items[];
    public int         position;
    static private OutlineActivityData singleton;

    static public void set(OutlineActivityData d) {
        singleton = d;
    }

    static public OutlineActivityData get() {
        if (singleton == null)
            singleton = new OutlineActivityData();
        return singleton;
    }
}