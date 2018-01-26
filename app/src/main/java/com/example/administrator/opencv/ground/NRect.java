package com.example.administrator.opencv.ground;

import org.opencv.core.Rect;

/**
 * Created by Administrator on 2018/1/4.
 */

public class NRect {
    Rect rect;
    int w;
    int h;
    public NRect(Rect rect,int w,int h) {
        this.rect=rect;
        this.w=w;
        this.h=h;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
