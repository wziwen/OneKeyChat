package com.ziwenwen.onekeychat.entity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by ziwen.wen on 2018/1/17.
 * TaskEntity
 */
public class TaskEntity implements Serializable {
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String IMAGE = "image";
    public final static String IS_VIDOP_CHAT = "isVideoChat";
    public final static String IS_GROUP_CHAT = "isGroupChat";
    public final static String CREATE_TIME = "createtime";


    private Long id;
    private String name;
    private int isVideoChat;
    private int isGroupChat;
    private String image;
    private long createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getIsVideoChat() {
        return isVideoChat;
    }

    public void setIsVideoChat(int isVideoChat) {
        this.isVideoChat = isVideoChat;
    }

    public int getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(int isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(NAME, name);
        cv.put(IMAGE, image);
        cv.put(IS_VIDOP_CHAT, isVideoChat);
        cv.put(IS_GROUP_CHAT, isGroupChat);
        cv.put(CREATE_TIME, createtime);
        return cv;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void saveToIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putLong("task_id", id);
        bundle.putString("name", name);
        bundle.putBoolean("isVideoChat", isVideoChat == 1);
        bundle.putBoolean("isGroupChat", isGroupChat == 1);
        intent.putExtras(bundle);
    }

    public void loadFromIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle == null) {
            return;
        }
        id = bundle.getLong("task_id");
        name = bundle.getString("name");
        isVideoChat = bundle.getBoolean("isVideoChat") ? 1 : 0;
        isGroupChat = bundle.getBoolean("isGroupChat") ? 1 : 0;
    }
}
