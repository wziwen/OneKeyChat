package com.ziwenwen.onekeychat;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatService;

/**
 * Created by ziwen.wen on 2018/1/17.
 * Application
 */
public class App extends Application {

    private static Application context;


    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initBugly();

        initMTA();
    }

    private void initMTA() {
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            StatService.startStatService(this, null,
                    com.tencent.stat.common.StatConstants.VERSION);
            Log.d("MTA", "MTA初始化成功");
        } catch (MtaSDkException e) {
            // MTA初始化失败
            Log.d("MTA", "MTA初始化失败");
            e.printStackTrace();
        }
    }

    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppVersion(BuildConfig.VERSION_NAME + "_" + BuildConfig.BUILD_TYPE);      //App的版本

        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
        CrashReport.initCrashReport(this, "bf4958041a", true, strategy);
    }

    public static Application getAppContext() {
        return context;
    }
}
