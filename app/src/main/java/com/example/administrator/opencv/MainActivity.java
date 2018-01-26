package com.example.administrator.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.administrator.opencv.log.LogShow;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.photo.Photo;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatFlagsException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        if(!OpenCVLoader.initDebug()){
            LogShow.logShow("OpenCV not loaded");

        } else {
            LogShow.logShow("OpenCV loaded");
        }
    }
    PeopleDetection peopleDetection;
    Mat mat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         peopleDetection =new PeopleDetection();
     
        try {
             mat = Utils.loadResource(this, R.drawable.timg);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void onClickb(View view){
        long start=System.currentTimeMillis();
      //  long l = peopleDetection.detecctionPeople(mat, new Scalar(0, 255, 0));
        long end=System.currentTimeMillis()-start;

        System.out.println(end+"===");
    }
}
