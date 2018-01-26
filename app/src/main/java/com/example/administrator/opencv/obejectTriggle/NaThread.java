package com.example.administrator.opencv.obejectTriggle;

import android.os.SystemClock;

import com.example.administrator.opencv.ground.Mlog;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.ml.Ml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/26.
 */

public class NaThread extends Thread {
    TriggleSwith triggleSwith;
    public NaThread(TriggleSwith triggleSwiths) {
        this.triggleSwith = triggleSwiths;
    }

    public boolean isTrig() {
        return isTrig;
    }

    public void setTrig(boolean trig) {
        isTrig = trig;
    }

    private final  int TIME=100;
    private final  int CountValue=40;
    private boolean  isRun=true;
    private boolean  isTrig=false;
    private  int  count=0;
    private NbThread nbThread;
   private    Mat      mat;
    @Override
    public void run() {
        List<Rect> rect = triggleSwith.getRects();

        int  matsize=rect.size();
        List<Mat>   preMat=new ArrayList<>(matsize);
        List<Mat>   backupMat=new ArrayList<>(matsize);
        List<Mat>   newMat=new ArrayList<>(matsize);
        List<Mat>   saveMat=new ArrayList<>(matsize);
        List<Integer>   integers=new ArrayList<>();
        while (isRun){
            integers.clear();
            newMat.clear();
            backupMat.clear();
            long  start=System.currentTimeMillis();
                    mat =triggleSwith.getmat().clone();
            for (int i = 0; i <matsize; i++) {
                Mat  mat1=mat.submat(rect.get(i));
                newMat.add(mat1);
                backupMat.add(mat1);
            }
            if (preMat.size()>0){
                for (int i = 0; i <matsize ; i++) {
                        Mat  dst=new Mat(newMat.get(i).rows(),newMat.get(i).cols(),newMat.get(i).type());
                        Core.absdiff(preMat.get(i), newMat.get(i), dst);
                        Scalar sd = Core.mean(dst);
                        if (sd.val[0]>6){
                            integers.add(i);
                        }
                    }
                    if (integers.size()>0){
                        count=0;
                        if (!isTrig&&saveMat.size()>0){

                            isTrig=true;
                               new NbThread(saveMat, rect, new NbThread.DataTriggle() {
                                   @Override
                                   public void callData(int v) {
                                       triggleSwith.stopTriggler(v);

                                       isTrig=false;
                                   }
                                   @Override
                                   public Mat getMat() {
                                       return mat;
                                   }
                               }).start();

                    }
                    }else {

                        count++;
                        if (count>CountValue){
                            saveMat.clear();
                            saveMat.addAll(backupMat);
                            triggleSwith.startTriggler();
                        }
                    }

                }
            preMat.clear();
            preMat.addAll(backupMat);
            long  end=System.currentTimeMillis()-start;

            if (TIME-end>0){
                SystemClock.sleep(TIME-end);
            }
        }
    }
    interface TriggleSwith{
        Mat  getmat();
        void   startTriggler(  );
        void   stopTriggler(int v);
        List<Rect>    getRects(  );
    }
}
