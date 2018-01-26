package com.example.administrator.opencv.ground;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.CheckedOutputStream;

import static org.opencv.core.Core.absdiff;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * Created by Administrator on 2018/1/3.
 */

public class NUtils {
 public static Scalar getScalar(){

     return new Scalar(255,0,0);
 }
   public static double getPSNR( Mat I1,  Mat I2)
    {
        Mat s1=new Mat();
        absdiff(I1, I2, s1);       // |I1 - I2|AbsDiff函数是 OpenCV 中计算两个数组差的绝对值的函数
        s1.convertTo(s1, CV_32F);  // 这里我们使用的CV_32F来计算，因为8位无符号char是不能进行平方计算
        s1 = s1.mul(s1);           // |I1 - I2|^2
        Scalar s=Core.sumElems(s1);


        double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels

        if( sse <= 1e-10) // 对于非常小的值我们将约等于0
            return 0;
        else
        {

            double  mse =sse /(double)(I1.channels() * I1.total());//计算MSE
            double psnr = 10.0*Math.log10((255*255)/mse);
            return psnr;//返回PSNR
        }
    }
    public static void drawRectangle(Mat mat, Rect rect, int rows, int cols){
        Imgproc.rectangle(mat,rect.tl(),rect.br(),getScalar());
        int xdiv=rect.width/cols;
        int  ydiv=rect.height/rows;
        for (int i = 0; i <rows-1 ; i++) {
            Point p1=new Point(rect.x,rect.y+i*ydiv+ydiv);
            Point p2=new Point((rect.x+rect.width),rect.y+i*ydiv+ydiv);
            Imgproc.line(mat,p1,p2,getScalar(),5);
        }
        for (int j = 0; j <cols-1 ; j++) {
            Point p1=new Point(rect.x+xdiv+j*xdiv,rect.y);
            Point p2=new Point(rect.x+xdiv+j*xdiv,(rect.y+rect.height));
            Imgproc.line(mat,p1,p2,getScalar(),5);
        }

    }
    public static List<Mat> getMatArray(Mat mat, Rect rect, int rows, int cols){
         List<Mat>  mats=new ArrayList<>(cols*rows);
        //Imgproc.rectangle(mat,rect.tl(),rect.br(),getScalar());
        int xdiv=rect.width/cols;
        int  ydiv=rect.height/rows;
        for (int i = 0; i <rows ; i++) {
            for (int j = 0; j <cols ; j++) {
                Rect  rect1 =new Rect(rect.x+xdiv*j,rect.y+ydiv*i,xdiv,ydiv);
                mats.add(mat.submat(rect1));
            }
        }
        return mats;
    }
    private  volatile static int a=0;
    private  volatile static boolean start=false;
    public static int  compareMats( List<Mat> mats1,  List<Mat> mats2){

         int posi = -1;
        for (int i = 0; i <mats1.size() ; i++) {
            Mat mas=mats1.get(i).clone();
         //   double v = comparePhoto(mas, mats2.get(i));
            double v = getPSNR(mas,mats2.get(i));
          Mlog.log(v);


        }


        return posi;
    }
    public static void perssionRequest(Activity context){

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.CAMERA)) {


            } else {

              ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.CAMERA},
                        1);
            }
        }
    }
    public static int  compareMats2( List<Mat> mats1,  List<Mat> mats2){
        int posi = -1;
        a=0;
        start=true;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(mats1.size()+1);
        for (int i = 0; i <mats1.size() ; i++) {
            fixedThreadPool.execute(new NRunable(mats1.get(i),mats2.get(i),new NRunable.RunWork() {
                @Override
                public void startWork(Mat mat1, Mat mat2) {
                    long  stat=System.currentTimeMillis();
                    Mlog.log("结果"+comparePhoto(mat1,mat2));
                    long end=System.currentTimeMillis()-stat;
                    Mlog.log("线程"+Thread.currentThread()+"计算"+end);
                }
                @Override
                public void workfinish() {
                    a++;
                }
            }));

        }
        while (start){
            if (a==6){
                start=false;

            }
        }

        if (!fixedThreadPool.isShutdown()){
            fixedThreadPool.shutdown();
        }

        return posi;
    }
    public static  boolean compareMat(Mat mat1,Mat mat2){
        if (mat1.channels()==3){
            Imgproc.cvtColor(mat1,mat1,COLOR_BGR2GRAY);
        }
        if (mat2.channels()==3){
            Imgproc.cvtColor(mat2,mat2,COLOR_BGR2GRAY);
        }
        Mat mat3=new Mat(mat1.rows(),mat1.cols(),mat1.type());

        absdiff(mat1,mat2,mat3);
        Imgproc.threshold(mat3, mat3, 35, 255, THRESH_BINARY);
 /*       int  c=0;
        int d=0;
        for (int i = 0; i <mat3.rows() ; i++) {
            for (int j = 0; j <mat3.cols() ; j++) {
               byte[]  by=new byte[1];
               mat3.get(i,j,by);
               if (by[0]==0){
                   c++;
               }else {
                   d++;
               }
            }
        }*/
   //     Mlog.log("c"+c+"d"+d);
        return false;
     }
    public static boolean calRate(int a,int b){
        if (a==0||b==0){
            return false;
        }
        float d=0;
        if (a>b){
            d= b/a;
        }else {
            d= a/b;
        }
        if (d>0.01&&d<0.89){
            return true;
        }
        return  false;
    }

    public static double comparePhoto(Mat mat1, Mat  mat2){
        if (mat1.channels()==3) Imgproc.cvtColor(mat1,mat1, 7);
        if (mat2.channels()==3) Imgproc.cvtColor(mat2,mat2, 7);
        int  w =64;
        int  h =64;
        Size size =new Size(w,h);
      /*  Imgproc.equalizeHist(mat1,mat1);
        Imgproc.equalizeHist(mat2,mat2);*/
        Imgproc.resize(mat1,mat1,size,0,0,Imgproc.INTER_CUBIC);
        Imgproc.resize(mat2,mat2,size,0,0,Imgproc.INTER_CUBIC);

        List<Mat>  mats1 =new ArrayList<>();
        Core.split(mat1,mats1);
        List<Mat>  mats2 =new ArrayList<>();
        Core.split(mat2,mats2);
        MatOfInt mating =new MatOfInt(256);
        MatOfFloat matfloat =new MatOfFloat(0,256);
        MatOfInt  channels =new MatOfInt(0);
        Mat  mat3=new Mat();
        Mat  mat4=new Mat();

        Imgproc.calcHist(mats1.subList(0,1),channels,new Mat(),mat3,mating,matfloat);
        Core.normalize(mat3,mat3,0,1,Core.NORM_MINMAX,-1,new Mat());
        Imgproc.calcHist(mats2.subList(0,1),channels,new Mat(),mat4,mating,matfloat);
        Core.normalize(mat4,mat4,0,1,Core.NORM_MINMAX,-1,new Mat());
        double value=Imgproc.compareHist(mat3,mat4,Imgproc.CV_COMP_CORREL);

        return  value;
    }

}
