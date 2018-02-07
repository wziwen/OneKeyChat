package com.ziwenwen.onekeychat;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

public class WeixinCallAS extends AccessibilityService {

    private static final String TAG = "WeixinCallAS";
    private static final int MAX_WATCH_TIME = 8 * 60 * 1000; // 避免一直监听, 如果超过时间就不再解析

    public static final int MSG_INIT = 100;
    public static final int MSG_DO_TASK = 101;

    private String name;
    private boolean isVideoChat;
    private boolean isGroupChat;
    int currentStep = -1;
    long taskStartTime = 0;

    private boolean hasPermission = false;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    try {
                        parseInitData((Intent) msg.obj);
                        handler.removeMessages(MSG_DO_TASK);
                        doTask();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                    break;
                case MSG_DO_TASK:
                    try {
                        doTask();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "on create");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        hasPermission = true;
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!hasPermission) {
            Toast.makeText(this, "无障碍权限没有开启", Toast.LENGTH_SHORT).show();
        } else {
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "权限开启", Toast.LENGTH_SHORT).show();
            }
        }
        handler.obtainMessage(MSG_INIT, intent)
                .sendToTarget();
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseInitData(Intent intent) {
        if (intent != null) {
            taskStartTime = System.currentTimeMillis();
            currentStep = 0;
            name = intent.getStringExtra("name");
            isVideoChat = intent.getBooleanExtra("isVideoChat", false);
            isGroupChat = intent.getBooleanExtra("isGroupChat", false);
            Log.d(TAG, "onStartCommand:" + name);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    private void doTask() {
        // 确定需要时才执行
        long startTime = System.currentTimeMillis();
        boolean disable = TextUtils.isEmpty(name);
        if (disable) {
            Log.d(TAG, "name is empty, disable");
            return;
        }
        AccessibilityNodeInfo rootWindowNode = getRootInActiveWindow();
        if (rootWindowNode == null || !"com.tencent.mm".equals(rootWindowNode.getPackageName())) {
            Log.d(TAG, "root view is null, return");
            checkIfResume();
            return;
        }
        Log.d(TAG, "Contetn desc: " + rootWindowNode.getContentDescription());
        if (0 == currentStep) {
            AccessibilityNodeInfo node = findNodeByName(rootWindowNode, name);
            if (node != null) {
                Log.d(TAG, "step 0 finish");
                clickNodeAndParent(node);
                currentStep++;
                node.recycle();
            }
        } else if (1 == currentStep) {
            // 找到更多按钮
            AccessibilityNodeInfo node = findNodeByDescription(rootWindowNode, "更多功能按钮，");
            if (node != null) {
                Log.d(TAG, "step 1 finish");
                clickNodeAndParent(node);
                currentStep++;
                node.recycle();
            }
        } else if (2 == currentStep) {
            // 找到视频聊天
            AccessibilityNodeInfo node = findNodeByNames(rootWindowNode, "视频聊天", "视频通话", "语音聊天");
            if (node != null) {
                Log.d(TAG, "step 2 finish");
                clickNodeAndParent(node);
                currentStep++;
                node.recycle();
            }
        } else if (3 == currentStep) {
            if (isGroupChat) {
                // 找到ListView下面的所有CheckBox
                try {
                    List<AccessibilityNodeInfo> checkList = findNodeByClass(rootWindowNode, CheckBox.class.getName());
                    if (checkList.size() > 0) {
                        for (AccessibilityNodeInfo checkBox : checkList) {
                            if (!checkBox.isChecked()) {
                                clickNodeAndParent(checkBox);
                            }
                            checkBox.recycle();
                        }
                        AccessibilityNodeInfo nodeInfo = findNodeByName(rootWindowNode, "开始");
                        if (nodeInfo != null) {
                            clickNodeAndParent(nodeInfo);
                            nodeInfo.recycle();
                            currentStep++;
                            name = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                clickVideoChat(rootWindowNode);
            }
        } else if (4 == currentStep) {
//            clickVideoChat(rootWindowNode);
        }
        rootWindowNode.recycle();

        checkIfResume();
        Log.d(TAG, "do task time: " + (System.currentTimeMillis() - startTime));
    }

    private void checkIfResume() {
        boolean timeOver= taskStartTime + MAX_WATCH_TIME < System.currentTimeMillis();
        if (!timeOver && !TextUtils.isEmpty(name)) {
            Message msg = handler.obtainMessage(MSG_DO_TASK);
            handler.sendMessageDelayed(msg, 200);
            Log.d(TAG, "wait for next run");
        } else {
            Log.d(TAG, "task finish");
        }
    }

    private List<AccessibilityNodeInfo> findNodeByClass(AccessibilityNodeInfo node, String name) {
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        if (node.getChildCount() == 0) {
            Log.d(TAG, "findNodeByClass:" + node.getClassName());
            if (name.equals(node.getClassName())) {
                list.add(node);
            } else {
                node.recycle();
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    List<AccessibilityNodeInfo> ret = findNodeByClass(node.getChild(i), name);
                    if (ret != null && ret.size() > 0) {
                        list.addAll(ret);
                    }
                }
            }
        }
        return list;
    }

    private AccessibilityNodeInfo findOneNodeByClass(AccessibilityNodeInfo node, String name) {
        if (node.getChildCount() == 0) {
            CharSequence className = node.getClassName();
            Log.d(TAG, "findOneNodeByClass:" + className);
            if (name.equals(node.getClassName())) {
                return node;
            }
            node.recycle();
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo ret = findOneNodeByClass(node.getChild(i), name);
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    private void clickVideoChat(AccessibilityNodeInfo rootWindowNode) {
        // 找到视频聊天
        AccessibilityNodeInfo node;
        if (isVideoChat) {
            node = findNodeByNames(rootWindowNode, "视频聊天", "视频通话");
        } else {
            node = findNodeByNames(rootWindowNode, "语音聊天", "语音通话");
        }
        if (node != null) {
            Log.d(TAG, "step 3 finish");
            clickNodeAndParent(node);
            currentStep++;
            name = null;
            Log.d(TAG, "stop service");
            node.recycle();
            stopSelf();
        }
    }

    /**
     * 点击控件和对应的父控件
     */
    private void clickNodeAndParent(AccessibilityNodeInfo node) {
        boolean result;
        if (node.isClickable()) {
            result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (result) {
                Log.d(TAG, "click success on node");
//                return;
            }
        }
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (result) {
                    Log.d(TAG, "click success on parent");
                }
                break;
            }
            AccessibilityNodeInfo temp = parent;
            parent = parent.getParent();
            temp.recycle();
        }
    }


    /**
     * 根据节点的contentDescription找到对应控件. 如果有多个之后返回第一个
     */
    public AccessibilityNodeInfo findNodeByDescription(AccessibilityNodeInfo node, String name) {
        if (node.getChildCount() == 0) {
            CharSequence charSequence = node.getContentDescription();
            if (charSequence != null) {
                String description = String.valueOf(charSequence);
                Log.d(TAG, "findNodeByStr description:" + description);
                if (description.contains(name)) {
                    return node;
                }
                node.recycle();
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo ret = findNodeByDescription(node.getChild(i), name);
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据节点名称找到对应控件. 如果有多个只会返回最先找到的一个
     */
    public AccessibilityNodeInfo findNodeByName(AccessibilityNodeInfo node, String name) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                Log.d(TAG, "findNodeByName:" + node.getText().toString());
                if (name.equals(node.getText().toString())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    AccessibilityNodeInfo ret = findNodeByName(child, name);
                    if (child != ret) {
                        child.recycle();
                    }
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByNames(AccessibilityNodeInfo node, String... names) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                Log.d(TAG, "findNodeByName:" + node.getText().toString());
                for (String name : names) {
                    if (name.equals(node.getText().toString())) {
                        return node;
                    }
                }
//                node.recycle();
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    AccessibilityNodeInfo ret = findNodeByNames(child, names);
                    if (child != ret) {
                        child.recycle();
                    }
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }
}
