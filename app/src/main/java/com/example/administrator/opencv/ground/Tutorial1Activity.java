package com.example.administrator.opencv.ground;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.administrator.opencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;
import java.util.List;

public class Tutorial1Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private  NRect nRect;
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

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv1.setText("變化值為："+msg.what);

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        // HelpsUtils.getSurrportCpu();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tutorial1_surface_view2);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {


                } else {



                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                }
            }


        tv1= (TextView) findViewById(R.id.tv1);
        imageView= (ImageView) findViewById(R.id.img);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setMaxFrameSize(1280,720);
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


    double  value=0;

    public void onCameraViewStarted(int width, int height) {
          startCompare();
        if (nRect==null){

            nRect=new NRect(new Rect(100,100,300,75),1,4);
        }
    }

    @Override
    public void onCameraViewStopped() {

    }


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
         souremat=inputFrame.rgba();
   //    grayMat=inputFrame.gray();
        grayMat=souremat.clone();

        NUtils.drawRectangle(souremat,nRect.getRect(),nRect.getW(),nRect.getH());
        return     souremat;
    }
private  Mat grayMat;
    private Mat  souremat;
    private List<Mat>  oldmat,newmat;
public void onClickb(View view){
    if (grayMat==null){

        return;
    }
        Mat mts=grayMat.clone();
    oldmat=NUtils.getMatArray(mts,nRect.getRect(),nRect.getW(),nRect.getH());
    startRun=false;

}
    public void onClickab(View view){
            if (!startRun){
                startRun=true;
            }

    }
volatile  boolean  isRun=true;
volatile  boolean  startRun=false;
public void startCompare(){
    new Thread(){
        @Override
        public void run() {
            while (isRun){
                if (startRun)  {
                    Mat   matclone=grayMat.clone();
                    long  start=System.currentTimeMillis();
                    if (newmat!=null){
                        newmat.clear();
                    }
                    newmat=NUtils.getMatArray(matclone,nRect.getRect(),nRect.getW(),nRect.getH());
                    int i = NUtils.compareMats(oldmat, newmat);
                    long  end=System.currentTimeMillis()-start;
                    Mlog.log("计算时间-------------------"+end);
                    if (i>-1){
                        handler.sendEmptyMessage(i);
                    }
                  if (end<500){
                      SystemClock.sleep(500-end);
                  }
                }
            }
        }
    }.start();
}


}
