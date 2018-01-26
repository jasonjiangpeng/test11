package com.example.administrator.opencv.cvpedection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;


public class BackOperater {
 private  static    BackgroundSubtractorKNN backgroundSubtractorKNN;
  private  static   Mat background;
    private BackOperater() {

    }
    /*背景建模*/
    private static void initBackground(){
        background=new Mat();
        backgroundSubtractorKNN = Video.createBackgroundSubtractorKNN();
        backgroundSubtractorKNN.setHistory(200);
        backgroundSubtractorKNN.setDist2Threshold(600);
        backgroundSubtractorKNN.setShadowThreshold(0.5);
    }

    public static Mat backgroundSubtractorOpeartor(Mat currentFrame){
       if (backgroundSubtractorKNN==null){
           initBackground();
       }
        backgroundSubtractorKNN.apply(currentFrame,background);

       return   background;
    }
    /*幀差法*/
    public  Mat frameSub(Mat mat,Mat mat2){
        Mat morphologyKernel=null;
        //  Mat  different=new Mat();
        if (mat.width()<10||mat2.width()<10){
            return null;
        }
        int w=mat2.width()/2;
        int h=mat2.height()/2;
        Imgproc.resize(mat,mat,new Size(w,h));
        Imgproc.resize(mat2,mat2,new Size(w,h));
        Core.subtract(mat,mat2,mat2);
        if (morphologyKernel==null){
            morphologyKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(3, 3), new Point(-1, -1));
        }
        Core.absdiff(mat2, Scalar.all(0),mat2);
        Imgproc.threshold(mat2,mat2,48,255,0);
        Imgproc.medianBlur(mat2, mat2, 3);
        Imgproc.morphologyEx(mat2,mat2,3,morphologyKernel, new Point(-1,-1),2,1,Scalar.all(1.7976931348623158e+308));
        return mat2;
    }

}
