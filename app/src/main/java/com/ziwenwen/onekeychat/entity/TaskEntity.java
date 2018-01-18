package com.ziwenwen.onekeychat.entity;

import android.content.ContentValues;

/**
 * Created by ziwen.wen on 2018/1/17.
 */
public class TaskEntity {
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String IMAGE = "image";
    public final static String IS_VIDOP_CHAT = "isVideoChat";
    public final static String IS_GROUP_CHAT = "isGroupChat";
    public final static String CREATE_TIME = "createtime";


    private Long id;
    private String name;
    private Integer isVideoChat;
    private Integer isGroupChat;
    private long createtime;
    private String image;

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

    public Integer getIsVideoChat() {
        return isVideoChat;
    }

    public void setIsVideoChat(Integer isVideoChat) {
        this.isVideoChat = isVideoChat;
    }

    public Integer getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(Integer isGroupChat) {
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
}
