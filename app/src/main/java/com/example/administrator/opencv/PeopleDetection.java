package com.example.administrator.opencv;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PeopleDetection {
private static HOGDescriptor hogDescriptor;

    public PeopleDetection() {

    }
    public static List<Rect> detecctionPeople(Mat mat){
        if (hogDescriptor==null){
            hogDescriptor=new HOGDescriptor();
            hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        }
        MatOfRect matOfRect =new MatOfRect();
        MatOfDouble matOfDouble = new MatOfDouble();
        hogDescriptor.detectMultiScale(mat,matOfRect,matOfDouble,0,new Size(8,8),new Size(32,32),1.05,2,false);
        if (matOfRect.total()<1){
            return null;
        }
        System.out.println(matOfRect.total());
     /*   if (matOfRect.total()>0){
            for (int i = 0; i <matOfRect.total() ; i++) {
                Rect r =matOfRect.toArray()[i];
                Imgproc.rectangle(mat, r.tl(), r.br(), s, 3);
            }
        }*/

        return matOfRect.toList();

    }
}
