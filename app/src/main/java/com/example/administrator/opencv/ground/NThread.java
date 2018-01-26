package com.example.administrator.opencv.ground;

import org.opencv.core.Mat;

/**
 * Created by Administrator on 2018/1/4.
 */

public class NThread extends Thread {
    private Mat mat1,mat2;
    public NThread(Mat mat1,Mat mat2) {
        this.mat1=mat1;
        this.mat2=mat2;
    }

    @Override
    public void run() {

    }

}
