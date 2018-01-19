package com.ziwenwen.onekeychat;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by ziwen.wen on 2018/1/17.
 */
public class App extends Application {

    private static Application context;


    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initBugly();
    }

    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppVersion(BuildConfig.VERSION_NAME +"_" + BuildConfig.BUILD_TYPE);      //App的版本

        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        CrashReport.initCrashReport(this, "bf4958041a", true, strategy);
    }

    public static Application getAppContext() {
        return context;
    }
}
