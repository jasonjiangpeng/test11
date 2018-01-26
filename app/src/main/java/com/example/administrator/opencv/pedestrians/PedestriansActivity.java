package com.example.administrator.opencv.pedestrians;

import android.app.Activity;
import android.media.Image;
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
import com.example.administrator.opencv.obejectTriggle.NRect;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.List;

public class PedestriansActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Nview  nview;
    private TextView textView;
    private Image  image;
    private Handler  handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            textView.setText(msg.what+"区域");

        }
    };
    public PedestriansActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.pedestriview);
        textView= (TextView) findViewById(R.id.textView);
        nview= (Nview) findViewById(R.id.nview);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_ANY);
        mOpenCvCameraView.setCvCameraViewListener(this);


    }
    List<NRect> nRects;
    private boolean  toRun=true;
  public void BtnstartWork(View view){
      if (nRects!=null){
          nRects.clear();
      }



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
    private float   mRelativeFaceSize   = 0.3f;
   private  volatile   boolean  startRun=false;
   private Rect rect;
   private   int a=0;
   private   int  b=0;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    Mat  newmat =inputFrame.gray().clone();
        if (startRun){
          startRun=false;
        final  long  stat=System.currentTimeMillis();
          new PThread(newmat, oldmat,rect, new PThread.CallData() {
              @Override
              public void finish(Mat oldmats, double v) {
                  oldmat=oldmats.clone();
                             long  end=System.currentTimeMillis()-stat;
                             if (v>7.5){
                                a++;
                             }else {
                                 if (a!=0){
                                     int  c=a/7+1;
                                     b+=c;
                                     handler.sendEmptyMessage(b);
                                 }

                                 a=0;
                             }
              //    handler.sendEmptyMessage((int) v);
                  Mlog.log("value"+v);
                  Mlog.log("时间"+end);
                  long  time= 300-end;
                  if (time>150){
                      SystemClock.sleep(time);
                  }
                  startRun=true;
              }

          }).start();

      }

        return     newmat;
    }
    private  Mat  oldmat;

    private boolean  inFist=true;
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    public void log(Object o){
        System.out.println("输出结果:"+o.toString());
    }
    public void onClick1(View view){
        nview.setStatu(1);
    }
    public void onClick2(View view){
        nview.setStatu(2);
    }
    public void onClick0(View view){
        rect=nview.getRect();
   //     Mlog.log("x"+rect.x+"h"+rect.y+"w"+rect.width+"h"+rect.width);
        if (rect==null){
            return;
        }
        startRun=true;

    }
}
