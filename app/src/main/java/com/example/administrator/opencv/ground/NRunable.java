package com.example.administrator.opencv.ground;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * Created by Administrator on 2018/1/5.
 */

class  NRunable implements Runnable {
    private RunWork runWork;
    private Mat mat1;
    private Mat mat2;
    public NRunable(Mat mat1,Mat mat2,RunWork runWork) {
        this.runWork=runWork;
        this.mat1=mat1;
        this.mat2=mat2;
    }

    @Override
    public void run() {
        runWork.startWork(mat1,mat2);
        runWork.workfinish();
    }
    interface RunWork{
        void  startWork(Mat mat1,Mat mat2);
        void  workfinish();
    }
    public   boolean compareMat(Mat mat1,Mat mat2){
        if (mat1.channels()==3){
            Imgproc.cvtColor(mat1,mat1,COLOR_BGR2GRAY);
        }
        if (mat2.channels()==3){
            Imgproc.cvtColor(mat2,mat2,COLOR_BGR2GRAY);
        }
        Mat mat3=new Mat(mat1.rows(),mat1.cols(),mat1.type());
        Core.absdiff(mat1,mat2,mat3);
        Imgproc.threshold(mat3, mat3, 35, 255, THRESH_BINARY);
        int  c=0;
        int d=0;
        for (int i = 0; i <mat3.rows() ; i++) {
            for (int j = 0; j <mat3.cols() ; j++) {
                byte[]  by=new byte[1];
                mat3.get(i,j,by);
                if (by[0]==0){
                    c++;
                }else {
                    d++;
                }
            }
        }
        Mlog.log("c"+c+"d"+d);
        return calRate(c,d);
    }
    public  boolean calRate(int a,int b){
        if (a==0||b==0){
            return false;
        }
        float d=0;
        if (a>b){
            d= b/a;
        }else {
            d= a/b;
        }
        if (d>0.01&&d<0.89){
            return true;
        }
        return  false;
    }

}