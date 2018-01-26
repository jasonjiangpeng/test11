package com.example.administrator.opencv.obejectTriggle;

/**
 * Created by Administrator on 2018/1/26.
 */

import com.example.administrator.opencv.ground.Mlog;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class NoThread2 extends Thread {
    private Mat newmat,oldmat;
    private CallData callData;
    private List<Rect> rect;
    private List<Mat>  mats;
    private List<Integer>  integers=null;

    public NoThread2(Mat newmat, List<Mat> oldmats, List<Rect> rect, CallData callData) {
        this.mats=oldmats;
        this.newmat=newmat;
        this.callData=callData;
        this.rect=rect;
        if (oldmats!=null){
            Mlog.log(oldmats.size()+"oldmat");
        }

}
private boolean  isTriggle=false;
    @Override
    public void run() {
        integers=new ArrayList<>();
        List<Mat>   backupMat=new ArrayList<>(rect.size());

        for (int i = 0; i <rect.size() ; i++) {
                Mat  mat1=newmat.submat(rect.get(i));
                 backupMat.add(mat1);
            }

        if (mats!=null){

            for (int i = 0; i <rect.size() ; i++) {
                Mat  mat1=newmat.submat(rect.get(i));
                Mat  dst=new Mat(mat1.rows(),mat1.cols(),mat1.type());
                Core.absdiff(mat1,mats.get(i),dst);
                Scalar sd= Core.mean(dst);
                //  Mlog.log(sd.val[0]+"值位置"+i);
                if (sd.val[0]>5)integers.add(i);
            }
        }

        callData.finish(backupMat,integers);
    }

    public interface CallData{

        void  finish(List<Mat> mats, List<Integer> integers);
    }
}
