package com.example.administrator.opencv.obejectTriggle;

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
import com.example.administrator.opencv.pedestrians.PThread;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

public class Tutorial1Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private NCountRectView nCountRectView;
    private TextView textView;
    private  int status=0;
    private Handler  handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            status=msg.what;
            if (msg.what==99) textView.setText("已恢复");
            else textView.setText(status+"区域");


        }
    };


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.mainnewact);
        nCountRectView= (NCountRectView) findViewById(R.id.nrect);
        textView= (TextView) findViewById(R.id.textView);
        SetParameter   setParameter= (SetParameter) findViewById(R.id.setvalueview);
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
    List<Rect> oRect;

  public void BtnstartWork(View view){
      if (oRect!=null){
          oRect.clear();
      }

      List<NRect>    nRects=nCountRectView.getRects();
      oRect=new ArrayList<>(nRects.size());
      for (int i = 0; i <nRects.size() ; i++) {
          oRect.add(nRects.get(i).toRect());
      }

     startRun=true;


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

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }




    public void onCameraViewStarted(int width, int height) {


    }

    public void onCameraViewStopped() {
    }



   private int   changeValue=0;
  private Mat  mata;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
          mata=inputFrame.gray().clone();
        if (startRun) {
            startRun = false;
            new NaThread(new NaThread.TriggleSwith() {
                @Override
                public Mat getmat() {

                    return mata;
                }

                @Override
                public void startTriggler() {
                    if (status!=99){
                        handler.sendEmptyMessage(99);
                    }


                }

                @Override
                public void stopTriggler(int v) {
                    handler.sendEmptyMessage(v);
                }


                @Override
                public List<Rect> getRects() {
                    return oRect;
                }
            }).start();


        }
        return     inputFrame.rgba();
    }


    private volatile boolean startRun =false;

}
