package com.artifex.mupdfdemo;

/**
 * Created by admin on 3/24/2015.
 */
public class OutlineItem {
    public final int    level;
    public final String title;
    public final int    page;


    OutlineItem(int _level, String _title, int _page) {
        level = _level;
        title = _title;
        page  = _page;
    }


}