package com.ziwenwen.onekeychat;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    CheckBox cbVideoChat;
    CheckBox cbGroupChat;

    String name;
    boolean isVideoChat;
    boolean isGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 有名字时表示桌面快捷方式进来的, 直接打开微信聊天
        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
            isVideoChat = getIntent().getBooleanExtra("isVideoChat", false);
            isGroupChat = getIntent().getBooleanExtra("isGroupChat", false);
            oneKeyChat(name, isVideoChat, isGroupChat);
            finish();
            return;
        }
        // 保存数据后下一次直接显示拨打按钮
        SharedPreferences preferences = openSharedPreferences();
        name = preferences.getString("name", null);
        isVideoChat = preferences.getBoolean("isVideoChat", false);
        isGroupChat = preferences.getBoolean("isGroupChat", false);
        if (!TextUtils.isEmpty(name)) {
            showOneKeyBtn();
        }

        etName = (EditText) findViewById(R.id.et_name);
        // 视频聊天
        cbVideoChat = (CheckBox) findViewById(R.id.cb_video_chat);
        cbVideoChat.setChecked(isVideoChat);
        cbVideoChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isVideoChat = isChecked;
            }
        });
        // 群聊天
        cbGroupChat = (CheckBox) findViewById(R.id.cb_group_chat);
        cbGroupChat.setChecked(isGroupChat);
        cbGroupChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isGroupChat = isChecked;
            }
        });

        findViewById(R.id.btn_open_accessibility)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.btn_test)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oneKeyChat(etName.getText().toString(), isVideoChat, isGroupChat);
                    }
                });

        findViewById(R.id.btn_create_shortcut)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createShotCut(MainActivity.this, MainActivity.class, etName.getText().toString(), isVideoChat, isGroupChat);
                    }
                });
        findViewById(R.id.btn_one_key_chat)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oneKeyChat(name, isVideoChat, isGroupChat);
                    }
                });
        findViewById(R.id.btn_complete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = etName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(MainActivity.this, "请输入微信昵称", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences preferences = openSharedPreferences();
                        preferences.edit()
                                .putString("name", name)
                                .putBoolean("isVideoChat", isVideoChat)
                                .putBoolean("isGroupChat", isGroupChat)
                                .apply();
                        showOneKeyBtn();
                    }
                });
    }

    private SharedPreferences openSharedPreferences() {
        return getSharedPreferences("cache", MODE_PRIVATE);
    }

    private void showOneKeyBtn() {
        findViewById(R.id.btn_one_key_chat)
                .setVisibility(View.VISIBLE);
        findViewById(R.id.ll_setting)
                .setVisibility(View.GONE);
    }

    private void showSettingBtn() {
        findViewById(R.id.btn_one_key_chat)
                .setVisibility(View.GONE);
        findViewById(R.id.ll_setting)
                .setVisibility(View.VISIBLE);
    }

    private void oneKeyChat(String name, boolean isVideoChat, boolean isGroupChat) {
        try {
            // 启动监听
            Intent intent = new Intent(MainActivity.this, MyAccessibility.class);
            intent.putExtra("name", name);
            intent.putExtra("isVideoChat", isVideoChat);
            intent.putExtra("isGroupChat", isGroupChat);
            startService(intent);
            // 打开微信首页
            intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 确保没有微信时不会崩溃
            Toast.makeText(this, "请安装好微信", Toast.LENGTH_SHORT).show();
        }
    }

    public void createShotCut(Context context, Class<?> clazz, String name, boolean isVideoChat, boolean isGroupChat) {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);

        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClass(context, clazz);
        /**
         * 创建一个Bundle对象让其保存将要传递的值
         */
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putBoolean("isVideoChat", isVideoChat);
        bundle.putBoolean("isGroupChat", isGroupChat);
        shortcutIntent.putExtras(bundle);
        /**
         * 设置这条属性，可以使点击快捷方式后关闭其他的任务栈的其他activity，然后创建指定的acticity
         */
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent shortcut = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), R.mipmap.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(shortcut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("打开设置");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSettingBtn();
        return super.onOptionsItemSelected(item);
    }
}
