package com.example.administrator.opencv.pedestrians;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Putils {
    public static void calculater(Mat  mat){
         if (mat.channels()==3){
             Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);
         }
        MatOfDouble  matOfDouble1=new MatOfDouble();
        MatOfDouble  matOfDouble2 =new MatOfDouble();
        Core.meanStdDev(mat,matOfDouble1,matOfDouble2);

        double  v1=matOfDouble1.toArray()[0];
        double  v2=matOfDouble1.toArray()[0];
    }
}
