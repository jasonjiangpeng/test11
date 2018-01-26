package com.example.administrator.opencv.pedestrians;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Administrator on 2018/1/24.
 */

public class PThread extends Thread {
  private Mat newmat,oldmat;
  private CallData callData;
  private Rect  rect;
    public PThread(Mat newmat,Mat oldmats,Rect rect,CallData callData) {
        this.oldmat=oldmats;
        this.newmat=newmat;
        this.callData=callData;
        this.rect=rect;
    }

    @Override
    public void run() {

        double  va=-999;
        if (oldmat==null){
            oldmat=newmat.submat(rect);
            if (oldmat.channels()==3){
                Imgproc.cvtColor(oldmat,oldmat,Imgproc.COLOR_BGR2GRAY);
            }
        }
        else {
           Mat  mat=newmat.submat(rect);
           if (mat.channels()==3){
               Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);
           }
           Mat  dst=new Mat(mat.rows(),mat.cols(),mat.type());
           Core.absdiff(mat,oldmat,dst);
          Scalar sd= Core.mean(dst);
            va=sd.val[0];
        }


        callData.finish(oldmat,va);
    }
    public interface CallData{

        void  finish(Mat oldmats,double v);
    }
}
