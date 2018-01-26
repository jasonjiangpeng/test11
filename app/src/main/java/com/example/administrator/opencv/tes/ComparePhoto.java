package com.example.administrator.opencv.tes;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

/**
 * Created by Administrator on 2017/12/5.
 */

public class ComparePhoto {
  private final static int PHOTOSIZE=100;
     public static double comparePhoto(Mat mat1, Mat mat2){
        if (mat1.channels()==3) Imgproc.cvtColor(mat1,mat1, 7);
        if (mat2.channels()==3) Imgproc.cvtColor(mat2,mat2, 7);

         int  x =mat1.width()/PHOTOSIZE;
         int  h =mat1.height()/x;
    //    Size size =new Size(PHOTOSIZE,h);
        Size size =new Size(PHOTOSIZE,PHOTOSIZE);

         Imgproc.resize(mat1,mat1,size,0,0, Imgproc.INTER_CUBIC);
         Imgproc.resize(mat2,mat2,size,0,0, Imgproc.INTER_CUBIC);
         Imgproc.equalizeHist(mat1,mat1);
         Imgproc.equalizeHist(mat2,mat2);
         List<Mat> mats1 =new ArrayList<>();
      Core.split(mat1,mats1);
         List<Mat> mats2 =new ArrayList<>();
      Core.split(mat2,mats2);
         MatOfInt mating =new MatOfInt(256);
         MatOfFloat matfloat =new MatOfFloat(0,256);
         MatOfInt channels =new MatOfInt(0);
         Mat mat3=new Mat();
         Mat mat4=new Mat();
         Imgproc.calcHist(mats1.subList(0,1),channels,new Mat(),mat3,mating,matfloat);
         Core.normalize(mat3,mat3,0,1, Core.NORM_MINMAX,-1,new Mat());
         Imgproc.calcHist(mats2.subList(0,1),channels,new Mat(),mat4,mating,matfloat);
         Core.normalize(mat4,mat4,0,1, Core.NORM_MINMAX,-1,new Mat());

         double value= Imgproc.compareHist(mat3,mat4, Imgproc.CV_COMP_CORREL);


         return  value;
     }
     public static Mat frameSub(Mat mat,Mat mat2){
         Mat  different=new Mat();
         Core.subtract(mat,mat2,different);
         Mat morphologyKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(3, 3), new Point(-1, -1));
         Mat  absdifferen=new Mat();
         Core.absdiff(different,Scalar.all(0),absdifferen);
         Mat result=new Mat();
         Imgproc.threshold(absdifferen,result,30,255,0);
         Imgproc.medianBlur(result, result, 3);
         Imgproc.morphologyEx(result,result,3,morphologyKernel, new Point(-1,-1),2,1,Scalar.all(1.7976931348623158e+308));
         return result;
     }
}
