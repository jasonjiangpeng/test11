package com.example.administrator.opencv.tes;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

public class Tutorial1Activity3 extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;
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
                 /* //  tv1.setText("變化值為："+value);
                    tv1.setText("最小值："+value);
                    if (value<0.002){
                     *//*   if (linimax){
                            double weight=(LimitMax-value)*0.3;
                            value=value+weight;
                            linimax=false;
                        }*//*
                        imageView.setVisibility(View.GONE);
                    } else if (value>0.01) {
                        imageView.setVisibility(View.VISIBLE);
                    }else {

                    }*/
                    break;
                case  1:
                    imageView.setVisibility(View.VISIBLE);
                    break;

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        // HelpsUtils.getSurrportCpu();
        mats=new ArrayList<>(40);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tutorial1_surface_view2);
        tv1= (TextView) findViewById(R.id.tv1);
        imageView= (ImageView) findViewById(R.id.img);
     //   imageView.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemSwitchCamera = menu.add("Toggle Native/Java camera");
        return true;
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
    public void onCameraViewStarted(int width, int height) {
        //  cascadeClassifier=cvGender.getCascadeClassifier();
        new Thread(){
            @Override
            public void run() {
                while (isRun){

                    long  start = System.currentTimeMillis();
                    double minvalue=0;
                    double maxvalue=0;
                    if (mymat==null){
                        continue;
                    }
                    if (savemat!=null){
                        value =ComparePhoto.comparePhoto(mymat,savemat);
                        double arlfa=maxValue-value;
                        System.out.println("arlfa"+arlfa);
                        if (arlfa>0.02){
                            handler.sendEmptyMessage(1);
                        }else {

                            handler.sendEmptyMessage(0);
                        }
                    }

                    if (mats.size()<10){
                         mats.add(mymat);
                     }else {
                         boolean  isfirst=true;
                         for (int i = 0; i <mats.size() ; i++) {
                          double   a=ComparePhoto.comparePhoto(mymat,mats.get(i));
                             if (isfirst){
                                 isfirst=false;
                                 minvalue=a;
                                 maxvalue=a;
                             }else {
                                 minvalue=Math.min(a,minvalue);
                                 maxvalue=Math.max(a,maxvalue);
                             }

                         }
                         double delta=maxvalue-minvalue;
                         System.out.println("delta"+delta);
                         if (delta<0.007&&savemat==null){
                             maxValue=(minvalue+maxvalue)/2;
                             System.out.println("NULLDATA"+maxValue);
                             savemat=mymat.clone();
                         }
                         mats.remove(0);
                         mats.add(mymat);
                     }
                    if (matChange>50){
                        matChange=0;
                        System.gc();
                    }
                    matChange++;
                    long end= System.currentTimeMillis()-start;
                    System.out.println("檢測時間"+end);
                    System.out.println("檢測值"+value+"最大值"+maxValue);
                    if (end<TIMEHOLD){
                        SystemClock.sleep(TIMEHOLD-end);
                    }
                }
            }
        }.start();
    }
    //  private PeopleDetection peopleDetection;

    private boolean isFirst=true;
    private int TIMEHOLD=250;
    public void onCameraViewStopped() {
        isRun=false;
    }
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat mRgba=inputFrame.rgba();
        mymat=inputFrame.gray();
        Mat nowMat=inputFrame.gray();


/*   if (list!=null&&list.size()>0){
                    for (int i = 0; i <list.size() ; i++) {
                        Rect  r =list.get(i);
                        Core.rectangle(mRgba, r.tl(), r.br(), FACE_RECT_COLOR, 3);
                    }
                }*/
        return     mRgba;
    }
    private List<Mat> mats;
    private Mat mymat;
    private Mat oldmat;
    private Mat savemat=null;
    private List<Rect> list=new ArrayList<>(50);
    private boolean isRun=true;
    private static final Scalar FACE_RECT_COLOR  = new Scalar(0, 255, 0, 255);
    public void log(Object o){
        System.out.println("输出结果:"+o.toString());
    }
    enum Stutas{
        MYSTTIC,MYSPORT
    }
    public void getStutas(Mat mat){
        double  compareValue=0;
        if (oldmat==null){
            oldmat=mat.clone();
        }else {
            compareValue=ComparePhoto.comparePhoto(oldmat,mat);
        }



    }
}
