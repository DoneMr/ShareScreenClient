package com.done.sharescreenclient;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.done.sharescreenclient.util.DoneLogger;
import com.done.sharescreenclient.util.ScreenUtils;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * Created by Done on 2017/12/11.
 *
 * @author by Done
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public static Context gCONTEXT;
    public static Handler gHANDLER;
    public static int gSCREEN_WIDTH;
    public static int gSCREEN_HEIGHT;


    @Override
    public void onCreate() {
        super.onCreate();
        gCONTEXT = this;
        gHANDLER = new Handler(Looper.getMainLooper());
        gSCREEN_WIDTH = ScreenUtils.getScreenWidth(this);
        gSCREEN_HEIGHT = ScreenUtils.getScreenHeight(this);
        DoneLogger.i(TAG, "init get screen params:" + gSCREEN_WIDTH + "x" + gSCREEN_HEIGHT);
    }
}
