package com.example.administrator.opencv.tes;

import android.os.Handler;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/11.
 */

public class WorkThread  extends Thread{
   private boolean  isRun =true;
  private Handler  handler ;
    private int Size=10;
    private double value=0;
    private enum AdsStatus{
          Active,IDle
    }
    private List<Mat>  mats=new ArrayList<>(Size);
    private Mat  mat;
    public WorkThread(Mat  mat,Handler  handler) {
        this.handler=handler;
        this.mat=mat;

    }

    @Override
    public void run() {
        while (isRun){
            if (mats.size()<Size+1){
                Mat  newMat =getMat();
                Mat  clone =newMat.clone();
                mats.add(clone);
            }else {
                Mat  newMat =getMat();
                Mat  clone =newMat.clone();
                if (mats.size()>0){
                    for (int i = mats.size()-1; i >=0 ; i--) {
                        double  result=ComparePhoto.comparePhoto(newMat,mats.get(i));

                    }
                }
                changeList(clone);
            }

        }
    }
    public Mat  getMat(){
        return  mat;
    }
    public void stopThread(){
        isRun=false;
    }
    public void changeList(Mat newMat){
        mats.remove(0);
        mats.add(newMat);

    }

}
