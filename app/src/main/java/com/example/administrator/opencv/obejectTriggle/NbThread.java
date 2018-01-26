package com.example.administrator.opencv.obejectTriggle;

import android.os.SystemClock;

import com.example.administrator.opencv.ground.Mlog;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/26.
 */

public class NbThread extends Thread {
  //  private Mat mat;
    private List<Mat> mats;
    List<Rect> rect;
    private DataTriggle dataTriggle;
    public NbThread(List<Mat> mats,List<Rect> rects,DataTriggle dataTriggle) {
        Mlog.log("启动程序");
        this.mats=mats;
        this.dataTriggle=dataTriggle;
        this.rect=rects;
    }



    private final  int TIME=200;
    private final  int Value=6;
    private boolean  isRun=true;
   private  int  count=0;
    private  int  vals=0;
    @Override
    public void run() {
         List<Integer> integers=new ArrayList<>();
        while (isRun){
            integers.clear();
            Mat  mat =dataTriggle.getMat().clone();
            long  start=System.currentTimeMillis();
            for (int i = 0; i <rect.size(); i++) {
                Mat  mat1=mat.submat(rect.get(i));
                   Mat dst=new Mat(mat1.rows(),mat1.cols(),mat1.type());
                 Core.absdiff(mat1, mats.get(i), dst);
                Scalar sd = Core.mean(dst);
                if (sd.val[0] > 5) integers.add(i);
            }
            if (integers.size()==1) count++;
            else count=0;
            if (count>Value){
                isRun=false;
                dataTriggle.callData(integers.get(0));
            }
            vals++;
            if (vals>30){
                isRun=false;
                dataTriggle.callData(-1);
            }
            long  end=System.currentTimeMillis()-start;
        //    Mlog.log("计算时间2--------"+end);
            if (TIME-end>0){
                SystemClock.sleep(TIME-end);
            }
        }
    }
    interface  DataTriggle{
        void  callData(int v);
        Mat  getMat();
    }

}
