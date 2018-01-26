package com.example.administrator.opencv.obejectTriggle;

/**
 * Created by Administrator on 2018/1/26.
 */

import android.os.SystemClock;

import com.example.administrator.opencv.ground.Mlog;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class NoThread extends Thread {
    private Mat newmat,oldmat;
    private CallData callData;
  //  private List<Rect> rect;
    private List<Mat>  mats;
    private List<Integer>  integers=null;

    public NoThread(Mat newmat, List<Mat> oldmats, List<Rect> rect,CallData callData) {
        this.mats=oldmats;
        this.newmat=newmat;
        this.callData=callData;


}
private boolean  isTriggle=false;
    private boolean  isRun=true;
    private  int  changeValue=10;
    private final  int TIME=200;
    @Override
    public void run() {
        List<Rect> rect = callData.getRects();
        int  matsize=rect.size();
        List<Mat>   preMat=new ArrayList<>(matsize);
        List<Mat>   backupMat=new ArrayList<>(matsize);
        List<Mat>   newMat=new ArrayList<>(matsize);
        List<Mat>   saveMat=new ArrayList<>(matsize);
        while (isRun){
            integers.clear();
            long  start=System.currentTimeMillis();
              Mat  mat =callData.getMat();
            for (int i = 0; i <matsize; i++) {
                Mat  mat1=mat.submat(rect.get(i));
                newMat.add(mat1);
                backupMat.add(mat1);
            }
            if (isTriggle){
                for (int i = 0; i <matsize ; i++) {
                    Mat  dst=new Mat(newMat.get(i).rows(),newMat.get(i).cols(),newMat.get(i).type());
                //    Mat  dst2=new Mat(newMat.get(i).rows(),newMat.get(i).cols(),newMat.get(i).type());
                    Core.absdiff(saveMat.get(i), newMat.get(i), dst);
                //    Core.absdiff(saveMat.get(i), newMat.get(i), dst);
                    Scalar sd = Core.mean(dst);
                    if (sd.val[0] > 5) integers.add(i);
                }

                if (integers.size()==1){
                    changeValue++;
                }else {
                    changeValue=0;
                }
                if (changeValue>4){
                    callData.triggle();
                }

            }else {
                if (preMat.size()>0){
                    saveMat.clear();
                    saveMat.addAll(preMat);
                    for (int i = 0; i <matsize ; i++) {
                        Mat  dst=new Mat(newMat.get(i).rows(),newMat.get(i).cols(),newMat.get(i).type());
                        Core.absdiff(preMat.get(i), newMat.get(i), dst);
                        Scalar sd = Core.mean(dst);
                        if (sd.val[0] > 5) {
                            isTriggle=true;
                            break;
                        }
                    }
                }
            }
            preMat.clear();
            preMat.addAll(backupMat);
            long  end=System.currentTimeMillis()-start;
            Mlog.log("计算时间--------"+end);
             if (TIME-end>0){

                 SystemClock.sleep(TIME-end);
             }
        }
    }

    public interface CallData{
        void  finish(List<Mat> mats,List<Integer> integers);
        Mat  getMat();
        List<Rect>   getRects();
        void   triggle();
    }
}
