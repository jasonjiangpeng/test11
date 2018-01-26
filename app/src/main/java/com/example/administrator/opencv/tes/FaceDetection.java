/*
package com.example.administrator.opencv.testt;

import android.content.Context;

import com.example.administrator.myapplication.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

*/
/**
 * Created by Administrator on 2017/12/5.
 *//*


public class FaceDetection {
    private CascadeClassifier cascadeClassifier;
    public FaceDetection(Context context) {
      //  String value= Utils.exportResource(context, R.raw.haarcascade_frontalface_alt);
     //   cascadeClassifier=new CascadeClassifier(value);

    }
    public List<Rect> detecctionFace(Mat mat){
        System.out.println("detecctionPeople");
        MatOfRect matOfRect =new MatOfRect();
        cascadeClassifier.detectMultiScale(mat,matOfRect);
        System.out.println(matOfRect.total());
        //  hogDescriptor.detectMultiScale(mat,matOfRect,new MatOfDouble(),0,new Size(4,4),new Size(32,32),1.05,2,false);
        if (matOfRect.total()>0){
            return  matOfRect.toList();
        }
        return null;
    }
}
*/
