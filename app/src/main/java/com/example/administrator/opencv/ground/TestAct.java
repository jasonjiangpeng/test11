package com.example.administrator.opencv.ground;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.administrator.opencv.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 */

public class TestAct extends Activity {
    static {
        if ( OpenCVLoader.initDebug(false)){

        }

    }
    List<Mat>  mats;
    ImageView im;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);
         im= (ImageView) findViewById(R.id.img);
        try {
            Mat mat = Utils.loadResource(this, R.drawable.a1);
            mats=     NUtils.getMatArray(mat,new Rect(0,0,mat.width(),mat.height()),3,3);
            mat.release();


        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.postDelayed(runnable,1000);
    }
    Handler  handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    int  a=0;
    Runnable  runnable =new Runnable() {
        @Override
        public void run() {
           if (a>=mats.size()){
               a=0;
           }

            Bitmap bitmap =Bitmap.createBitmap(mats.get(a).width(),mats.get(a).height(), Bitmap.Config.ARGB_8888);
            Mat  mat=mats.get(a);
            Mlog.logDivideLine();

         if (mat.channels()==3) {

             Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
         }

             if (mat.channels()==1){
                 Mlog.log(mat.channels());
                 byte[] bytes=new byte[mat.channels()];
                 Imgproc.threshold(mat,mat,45,255,Imgproc.THRESH_BINARY);

                 mat.get(mat.width()/2,mat.height()/2,bytes);
                 for (int i = 0; i <bytes.length ; i++) {
                     Mlog.log("bytes"+bytes[i]);
                 }
             }








            Utils.matToBitmap(mats.get(a),bitmap);
            im.setBackground(new BitmapDrawable(getResources(),bitmap));
            handler.postDelayed(runnable,1000);
            a++;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
