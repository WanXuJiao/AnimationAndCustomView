package com.jwx.animationandcustomview.activity;

import android.app.Application;

/**
 * Created by jwx on 2017/12/2
 */

public class MyApplication extends Application {

    /**
     * static instance.
     */
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public MyApplication() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }
}
