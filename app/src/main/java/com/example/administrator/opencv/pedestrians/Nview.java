package com.example.administrator.opencv.pedestrians;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Administrator on 2018/1/11.
 */

public class Nview extends View {
     private  int statu=0;
    private Point  point1,point2;
    private Rect rect;

    public Nview(Context context) {
        super(context);
    }

    public Nview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
   public Paint  getpaint(){
        Paint paint =new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        return paint;
   }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
      if (rect!=null){
          canvas.drawRect(rect,getpaint());
      }else {
          if (point1!=null)canvas.drawPoint(point1.x,point1.y,getpaint());

          if (point2!=null)canvas.drawPoint(point2.x,point2.y,getpaint());
      }
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (event.getAction()==MotionEvent.ACTION_DOWN){
           int  x= (int) event.getX();
           int y= (int) event.getY();
           switch (statu){
               case 1:
                    point1=new Point(x,y);
                   break;
               case 2:
                   point2=new Point(x,y);
                   break;
           }
       }
           calRect(point1,point2);
          invalidate();

        return true;
    }
    public void calRect(Point p1,Point p2){
       if (p1==null||p2==null){
           return;
       }
        int  a=p1.x-p2.x;
        int b=p1.y-p2.y;
        if (a==0||b==0){
            return;
        }

        if (a>0){
            if (b>0) rect=new Rect(p2.x,p2.y,p1.x,p1.y);
            else  rect=new Rect(p2.x,p1.y,p1.x,p2.y);
        }else {
            if (b>0) rect=new Rect(p1.x,p2.y,p2.x,p1.y);
            else  rect=new Rect(p1.x,p1.y,p2.x,p2.y);
        }
    }
    public org.opencv.core.Rect getRect(){
        if (rect==null){
            return null;
        }
        int  w=rect.right-rect.left;
        int h=rect.bottom-rect.top;
        org.opencv.core.Rect  rects =new org.opencv.core.Rect(rect.left,rect.top,w,h);
        return rects;
    }
}
