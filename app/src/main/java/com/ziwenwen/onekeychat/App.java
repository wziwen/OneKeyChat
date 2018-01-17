package com.ziwenwen.onekeychat;

import android.app.Application;

/**
 * Created by ziwen.wen on 2018/1/17.
 */
public class App extends Application {

    private static Application context;


    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
    }

    public static Application getAppContext() {
        return context;
    }
}
