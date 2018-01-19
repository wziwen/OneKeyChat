package com.ziwenwen.onekeychat;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ziwenwen.onekeychat.entity.TaskEntity;
import com.ziwenwen.onekeychat.widget.OneAppWidget;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName;
    SwitchCompat cbVideoChat;
    SwitchCompat cbGroupChat;

    TaskEntity taskEntity;
    private ImageView ivIcon;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        if (getIntent().hasExtra("task")) {
            taskEntity = (TaskEntity) getIntent().getSerializableExtra("task");
        }

        etName = findViewById(R.id.et_name);
        ivIcon = findViewById(R.id.iv_icon);
        // 视频聊天
        cbVideoChat =  findViewById(R.id.cb_video_chat);
        // 群聊天
        cbGroupChat = findViewById(R.id.cb_group_chat);
        if (taskEntity != null) {
            fillData();
        }


        findViewById(R.id.iv_icon)
                .setOnClickListener(this);
        findViewById(R.id.btn_test)
                .setOnClickListener(this);
        findViewById(R.id.btn_create_shortcut)
                .setOnClickListener(this);
        findViewById(R.id.btn_complete)
                .setOnClickListener(this);
    }

    private void createShotCut() {
        createShotCut(EditTaskActivity.this,
                EditTaskActivity.class,
                etName.getText().toString(),
                cbVideoChat.isChecked(),
                cbVideoChat.isChecked());
    }

    private void onComplete() {
        if (!validate()) {
            return;
        }
        if (taskEntity == null) {
            taskEntity = new TaskEntity();
        }
        taskEntity.setName(etName.getText().toString());
        taskEntity.setIsGroupChat(cbGroupChat.isChecked() ? 1 : 0);
        taskEntity.setIsVideoChat(cbVideoChat.isChecked() ? 1 : 0);
        taskEntity.setImage(imagePath);
        TaskManager.getInstance()
                .insertOrUpdate(taskEntity);
        setResult(RESULT_OK);

        OneAppWidget.updateWidget(this, AppWidgetManager.getInstance(this));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                test();
                break;
            case R.id.btn_complete:
                onComplete();
                break;
            case R.id.btn_create_shortcut:
                createShotCut();
                break;
            case R.id.iv_icon:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        RxGalleryFinal
                .with(this)
                .image()
                .radio()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        //图片选择结果
                        String filePath = imageRadioResultEvent.getResult().getOriginalPath();
                        imagePath = filePath;

                        Glide.with(EditTaskActivity.this)
                                .load(imagePath)
                                .into(ivIcon);
                    }
                })
                .openGallery();
    }


    private boolean validate() {
        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(EditTaskActivity.this, "请输入微信昵称", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void fillData() {
        if (!TextUtils.isEmpty(taskEntity.getName())) {
            etName.setText(taskEntity.getName());
        }
        cbGroupChat.setChecked(taskEntity.getIsGroupChat() == 1);
        cbVideoChat.setChecked(taskEntity.getIsVideoChat() == 1);

        Glide.with(this)
                .load(taskEntity.getImage())
                .placeholder(R.mipmap.addimages)
                .into(ivIcon);
    }

    private void test() {
        if (!validate()) {
            return;
        }
        oneKeyChat(etName.getText().toString(),
                cbVideoChat.isChecked(),
                cbVideoChat.isChecked()
                );
    }

    private void oneKeyChat(String name, boolean isVideoChat, boolean isGroupChat) {
        try {
            // 启动监听
            Intent intent = new Intent(EditTaskActivity.this, MyAccessibility.class);
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

}
