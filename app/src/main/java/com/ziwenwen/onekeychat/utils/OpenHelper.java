package com.ziwenwen.onekeychat.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.Toast;

import com.ziwenwen.onekeychat.WeixinCallAS;

/**
 * Created by ziwen.wen on 2018/1/19.
 * OpenHelper
 */
public class OpenHelper {
    public static void oneKeyChat(Activity activity, String name, boolean isVideoChat, boolean isGroupChat) {
        try {
            // 启动监听
            Intent intent = new Intent(activity, WeixinCallAS.class);
            intent.putExtra("name", name);
            intent.putExtra("isVideoChat", isVideoChat);
            intent.putExtra("isGroupChat", isGroupChat);
            activity.startService(intent);
            // 打开微信首页
            intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setComponent(cmp);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 确保没有微信时不会崩溃
            Toast.makeText(activity, "请安装好微信", Toast.LENGTH_SHORT).show();
        }
    }
}
