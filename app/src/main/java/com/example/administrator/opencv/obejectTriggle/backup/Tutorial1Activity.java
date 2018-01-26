package com.example.administrator.opencv.obejectTriggle.backup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.opencv.R;
import com.example.administrator.opencv.ground.Mlog;
import com.example.administrator.opencv.ground.NUtils;
import com.example.administrator.opencv.obejectTriggle.ChangeView;
import com.example.administrator.opencv.obejectTriggle.NCountRectView;
import com.example.administrator.opencv.obejectTriggle.NRect;
import com.example.administrator.opencv.obejectTriggle.SetParameter;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;

public class Tutorial1Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private NCountRectView nCountRectView;
    private SetParameter setParameter;
    private TextView textView;
    private Handler  handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            textView.setText(msg.what+"区域");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText("已初始化");
                    toRun=true;
                }
            },5*1000);
        }
    };
    public Tutorial1Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.mainnewact);
        nCountRectView= (NCountRectView) findViewById(R.id.nrect);
        textView= (TextView) findViewById(R.id.textView);
        setParameter= (SetParameter) findViewById(R.id.setvalueview);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY);
        mOpenCvCameraView.setCvCameraViewListener(this);
        setParameter.setChangeView(new ChangeView() {
            @Override
            public void styleChange(int value) {
                nCountRectView.setStyle(value);
            }

            @Override
            public void focusChange(int value) {
           nCountRectView.setFocusPosition(value);
            }

            @Override
            public void rcChange(int counts) {
              nCountRectView.setrc(counts);
            }
        });
    }
    List<NRect> nRects;
    private boolean  toRun=true;
  public void BtnstartWork(View view){
      if (nRects!=null){
          nRects.clear();
      }
      nRects=nCountRectView.getRects();
      new Thread(){
          @Override
          public void run() {
              while (inFist){
                  long  t=System.currentTimeMillis();
                  if (newmat==null){
                      Mlog.log(oldmat.rows()+"h"+oldmat.cols());
                      continue;
                  }
                  if (oldmat==null){
                      oldmat=newmat.clone();
                      continue;
                  }
                  if (nRects==null){
                      inFist=false;
                  }
                  if (!toRun){
                      continue;
                  }
                  for (int i = 0; i <nRects.size() ; i++) {
                      Rect  rect =nRects.get(i).toRect();
                      Mat mat=newmat.submat(rect);
                      Mat mat2=oldmat.submat(rect);
                      double v = NUtils.comparePhoto(mat, mat2);
                        if (v>0.9){
                            toRun=false;
                            handler.sendEmptyMessage(i+1);
                            break;
                        }
                      Mlog.log("对比结果"+v);
                  }
                  long e=System.currentTimeMillis()-t;
                  Mlog.log("计算时间"+e);
                  if (e<200){
                      SystemClock.sleep(200-e);
                  }
              }

          }
      }.start();

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
    {if (OpenCVLoader.initDebug(false)){
        mOpenCvCameraView.enableView();
    }
        super.onResume();

    }

    public void onDestroy() {
        super.onDestroy();
        inFist=false;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }




    public void onCameraViewStarted(int width, int height) {
      //  cascadeClassifier=cvGender.getCascadeClassifier();
        inFist =true;

    }

    public void onCameraViewStopped() {
    }

    private float          mRelativeFaceSize   = 0.3f;


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


        newmat =inputFrame.gray().clone();



        return     inputFrame.rgba();
    }
    private Mat  oldmat,newmat;
    private boolean  inFist=true;
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    public void log(Object o){
        System.out.println("输出结果:"+o.toString());
    }
}
