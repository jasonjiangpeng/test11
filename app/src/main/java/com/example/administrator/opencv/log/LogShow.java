package com.example.administrator.opencv.log;

/**
 * Created by Administrator on 2017/8/10.
 */

public class LogShow {
    private static String MYTAG="LogShow==:";
    public static void logShow(String tag,Object o) {
        if (o == null) {
            System.out.println(tag + ":" + null + "对象");
        } else {
            System.out.println(tag + ":" +o.toString());
        }
    }
    public static void logShow(Object o){
        if (o==null){
            return;
        }
            System.out.println(MYTAG+o.toString());
    }
}
