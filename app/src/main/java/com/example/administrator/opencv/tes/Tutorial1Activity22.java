package com.example.administrator.opencv.tes;

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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


import static org.opencv.imgproc.Imgproc.MORPH_RECT;

public class Tutorial1Activity22 extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
     private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    private TextView tv1;
    private ImageView imageView;
    private double  LimitMax=0.992;
    private boolean linimax=true;
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
/*        else
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);*/
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



    /*
        public void onClickb(View view){
            mAbsoluteFaceSize=0;
            if (mRelativeFaceSize>0.9){
                mRelativeFaceSize=0.1f;
            }
            mRelativeFaceSize+=0.1f;
            log(mRelativeFaceSize+"");

        }*/
    double  value=0;
    double  maxValue,minValue=0;
    long  matChange=0;
    private Mat  savemat;
    private Mat  sd;


    public void onCameraViewStarted(int width, int height) {

        //  cascadeClassifier=cvGender.getCascadeClassifier();
        new Thread(){
            @Override
            public void run() {
                while (isRun){
                    long  start = System.currentTimeMillis();
                    if (mymat==null){
                        continue;
                    }
                      savemat=mymat.clone();
                    if (previousMat==null){
                        previousMat=savemat.clone();
                    }else {
                        sd=  frameSub(mymat,previousMat);
                        if (sd==null){
                            continue;
                        }
                         value = Core.countNonZero(sd);
                        previousMat=savemat.clone();
                        if (value>800){
                                 times=0;
                            handler.sendEmptyMessage(1);
                       }else {
                            if (value<200){
                                times+=1;
                                if (times>30){
                                    times=0;
                                    matmode= savemat.clone();
                                    System.out.println("testjason");
                                }
                            }else {
                                times=0;
                            }
                            if (matmode!=null){
                              int  compareValue=Core.countNonZero(frameSub(matmode,savemat));
                              System.out.println("compareValue"+"compareValue"+compareValue);
                              if (compareValue<800){
                                  handler.sendEmptyMessage(0);
                              }
                          }else {
                              handler.sendEmptyMessage(0);
                          }
                        }
                     //  Utils.matToBitmap(sd,bitmap);

                    }
                    long end= System.currentTimeMillis()-start;
                    System.out.println("檢測時間"+end);
                    System.out.println("檢測值"+value+"最大值"+maxValue);
                    if (end<TIMEHOLD){
                        Runtime.getRuntime().gc();

                        SystemClock.sleep(TIMEHOLD-end);
                    }
                }
            }
        }.start();

    }
    //  private PeopleDetection peopleDetection;
    private Bitmap  bitmap;
    private int  times =0;
    private boolean isFirst=true;
    private int TIMEHOLD=300;

    public void onCameraViewStopped() {
        isRun=false;
    }

    private float                  mRelativeFaceSize   = 0.3f;
    private int                    mAbsoluteFaceSize   = 0;
    private CascadeClassifier cascadeClassifier;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

           Mat  old=inputFrame.gray();
                  mymat=old.clone();
                   mymat2=old.clone();
        return     old;
    }
    private Mat  previousMat;
    private Mat mymat,mymat2;
    private Mat matmode;
     private boolean isRun=true;
    private static final Scalar FACE_RECT_COLOR  = new Scalar(0, 255, 0, 255);
    Mat morphologyKernel;
    public  Mat frameSub(Mat mat,Mat mat2){
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
        Core.absdiff(mat2,Scalar.all(0),mat2);
        Imgproc.threshold(mat2,mat2,48,255,0);
        Imgproc.medianBlur(mat2, mat2, 3);
        Imgproc.morphologyEx(mat2,mat2,3,morphologyKernel, new Point(-1,-1),2,1,Scalar.all(1.7976931348623158e+308));
        return mat2;
    }
    public  Mat frameSub2(Mat mat,Mat mat2){
        //  Mat  different=new Mat();
        if (mat.width()<10||mat2.width()<10){
            return null;
        }
        int w=mat.width()/2;
        int h=mat.height()/2;
        Imgproc.resize(mat,mat,new Size(w,h));
        Imgproc.resize(mat2,mat2,new Size(w,h));
        Core.subtract(mat,mat2,mat2);
        if (morphologyKernel==null){
            morphologyKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(3, 3), new Point(-1, -1));
        }
        Core.absdiff(mat2,Scalar.all(0),mat2);
        Imgproc.threshold(mat2,mat2,48,255,0);
        Imgproc.medianBlur(mat2, mat2, 3);
        Imgproc.morphologyEx(mat2,mat2,3,morphologyKernel, new Point(-1,-1),2,1,Scalar.all(1.7976931348623158e+308));
        morphologyKernel.release();
        mat.release();
        return mat2;
    }

}
