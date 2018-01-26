package com.example.administrator.opencv.cvpedection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

public class BackGrountTrain1 extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private int TIMEHOLD=300;
    double  value=0;
     private TextView tv1;
    private ImageView imageView;

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv1.setText("變化值為："+value);
            switch (msg.what){
                case 0:
                    imageView.setVisibility(View.GONE);

                    break;
                case  1:
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    break;

            }
        }
    };
    private final static float TARGET_HEAP_UTILIZATION = 0.75f;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tutorial1_surface_view2);
        tv1= (TextView) findViewById(R.id.tv1);
        imageView= (ImageView) findViewById(R.id.img);
        imageView.setVisibility(View.GONE);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }
    @Override
    public void onResume()
    {
        if ( OpenCVLoader.initDebug(false)){
            System.out.println("initOk");
            mOpenCvCameraView.enableView();

        }

        super.onResume();

    }
    public void onDestroy() {
        super.onDestroy();
        isRun=false;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }




    public void onCameraViewStarted(int width, int height) {

        //  cascadeClassifier=cvGender.getCascadeClassifier();
        new Thread(){
            @Override
            public void run() {
                while (isRun){
                    long  start = System.currentTimeMillis();
                    if (myMat==null){
                        continue;
                    }
                    background = BackOperater.backgroundSubtractorOpeartor(myMat);
                    if (bitmap==null){
                        bitmap=Bitmap.createBitmap(background.width(),background.height(), Bitmap.Config.RGB_565);
                    }else {
                        Utils.matToBitmap(background,bitmap);
                        handler.sendEmptyMessage(1);
                    }
                    long end= System.currentTimeMillis()-start;
                    System.out.println("檢測時間"+end);
                    System.out.println("檢測值"+value);
                    if (end<TIMEHOLD){
                        Runtime.getRuntime().gc();

                        SystemClock.sleep(TIMEHOLD-end);
                    }

                }
            }
        }.start();

    }

  private Mat myMat,background;
  private Bitmap bitmap;
    public void onCameraViewStopped() {
        isRun=false;
    }
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat  old=inputFrame.gray();
        myMat=inputFrame.gray();
             return     old;
    }
    private boolean isRun=true;
    private static final Scalar FACE_RECT_COLOR  = new Scalar(0, 255, 0, 255);



}
