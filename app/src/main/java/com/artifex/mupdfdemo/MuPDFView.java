package com.artifex.mupdfdemo;

/**
 * Created by admin on 3/24/2015.
 */
import android.graphics.PointF;
import android.graphics.RectF;

enum Hit {Nothing, Widget, Annotation};

public interface MuPDFView {
    public void setPage(int page, PointF size);
    public void setScale(float scale);
    public int getPage();
    public void blank(int page);
    public Hit passClickEvent(float x, float y);
    public LinkInfo hitLink(float x, float y);
    public void selectText(float x0, float y0, float x1, float y1);

    public void setSearchBoxes(RectF searchBoxes[]);
    public void setLinkHighlighting(boolean f);
    public void deselectAnnotation();
    public void startDraw(float x, float y);
    public void continueDraw(float x, float y);

    public void setChangeReporter(Runnable reporter);
    public void update();
    public void addHq(boolean update);
    public void removeHq();
    public void releaseResources();
}