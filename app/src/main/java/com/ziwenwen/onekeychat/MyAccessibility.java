package com.ziwenwen.onekeychat;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibility extends AccessibilityService {

    private static final String TAG = "MyAccessibility";
    private String name;
    private boolean isVideoChat;
    private boolean isGroupChat;
    int currentStep =  -1;

    @Override
    public void onInterrupt() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            currentStep = 0;
            name = intent.getStringExtra("name");
            isVideoChat = intent.getBooleanExtra("isVideoChat", false);
            isGroupChat = intent.getBooleanExtra("isGroupChat", false);
            Log.d(TAG, "onStartCommand:" + name);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 确定需要时才执行
        if (currentStep > 3 || currentStep < 0) {
            return;
        }
        Log.d(TAG, "onAccessibilityEvent: " + event.toString());
        String className = event.getClassName().toString();
        Log.d(TAG, "onAccessibilityEvent class: " + className);
        boolean disable = TextUtils.isEmpty(name);
        if (disable) {
            return;
        }
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                if (className.equals("com.tencent.mm.ui.LauncherUI")) { // 进入首页, 点击对应的用户. 因为列表刷新有延迟, 需要sleep一下
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    findViewByNameAndClick(name);//state change
//                } else
//                if (className.equals("com.tencent.mm.ui.base.k")) {// 视频语音聊天选择
//                    sleep(1000);
//                    findViewByNameAndClick("语音聊天");
//                    name = null; // 只点击一次
//                }
//                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (0 == currentStep) {
                    AccessibilityNodeInfo node = findNodeByName(getRootInActiveWindow(), name);
                    if (node != null) {
                        Log.d(TAG, "step 0 finish");
                        clickNodeAndParent(node);
                        currentStep++;
                        node.recycle();
                    }
                } else if (1 == currentStep) {
                    // 找到更多按钮
                    AccessibilityNodeInfo node = findNodeByDescription(getRootInActiveWindow(), "更多功能按钮，");
                    if (node != null) {
                        Log.d(TAG, "step 1 finish");
                        clickNodeAndParent(node);
                        currentStep++;
                        node.recycle();
                    }
                } else if (2 == currentStep) {
                    // 找到视频聊天
                    AccessibilityNodeInfo node = findNodeByName(getRootInActiveWindow(), "视频聊天");
                    if (node != null) {
                        Log.d(TAG, "step 2 finish");
                        clickNodeAndParent(node);
                        currentStep++;
                        node.recycle();
                    } else {
                        // 旧版本微信语音聊天入口叫 "语音聊天", 新版本才叫视频聊天. 这里做一下兼容
                        node = findNodeByName(getRootInActiveWindow(), "语音聊天");
                        if (node != null) {
                            Log.d(TAG, "step 2 finish");
                            clickNodeAndParent(node);
                            currentStep++;
                            node.recycle();
                        }
                    }
                } else if (3 == currentStep) {
                    if (isGroupChat) {
                        // 找到ListView下面的所有CheckBox
                        try {
                                List<AccessibilityNodeInfo> checkList = findNodeByClass(getRootInActiveWindow(), CheckBox.class.getName());
                            if (checkList.size() > 0) {
                                for (AccessibilityNodeInfo checkBox : checkList) {
                                    if (!checkBox.isChecked()) {
                                        clickNodeAndParent(checkBox);
                                    }
                                    checkBox.recycle();
                                }
                                AccessibilityNodeInfo nodeInfo = findNodeByName(getRootInActiveWindow(), "开始");
                                if (nodeInfo != null) {
                                    clickNodeAndParent(nodeInfo);
                                    nodeInfo.recycle();
                                    currentStep ++;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        clickVideoChat();
                    }
                } else if (4 == currentStep) {
                    clickVideoChat();
                }
                break;
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

    private void clickVideoChat() {
        // 找到视频聊天
        AccessibilityNodeInfo node;
        if (isVideoChat) {
            node = findNodeByName(getRootInActiveWindow(), "视频聊天");
        } else {
            node = findNodeByName(getRootInActiveWindow(), "语音聊天");
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
     * 模拟点击
     */
    private boolean findViewByNameAndClick(String name) {
        Log.d(TAG, "findViewByNameAndClick: " + name);
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo node = findNodeByName(rootNode, name);

        if (node == null) {
            Log.d(TAG, "findViewByNameAndClick fail: " + name);
            return false;
        } else {
            clickNodeAndParent(node);
            return true;
        }
    }

    /**
     * 找到对应的控件并且点击
     *
     * @param name
     */
    private void findViewByDescriptionAndClick(String name) {
        Log.d(TAG, "findViewByDescriptionAndClick: " + name);
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo node = findNodeByDescription(rootNode, name);

        if (node == null) {
            Log.d(TAG, "findViewByNameAndClick fail: " + name);
        } else {
            clickNodeAndParent(node);
        }
    }

    /**
     * 点击控件和对应的父控件
     *
     * @param node
     */
    private void clickNodeAndParent(AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
     *
     * @param node
     * @param name
     * @return
     */
    public AccessibilityNodeInfo findNodeByName(AccessibilityNodeInfo node, String name) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                Log.d(TAG, "findNodeByName:" + node.getText().toString());
                if (name.equals(node.getText().toString())) {
                    return node;
                }
                node.recycle();
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    AccessibilityNodeInfo ret = findNodeByName(node.getChild(i), name);
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }
}
