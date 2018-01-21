package com.ziwenwen.onekeychat.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ziwenwen.onekeychat.entity.TaskEntity;

public class DBOpenHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "on_key.db";
    private final static int DATABASE_VERSION = 1;

    DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion == 2 && newVersion == 3) {
//            // 播放记录
//            createPlayRecordTable(db);
//        }
    }
    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DBController.TASK_TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY, " // id, download id
                        + "%s VARCHAR, " // name
                        + "%s VARCHAR, " // IMAGE
                        + "%s INTEGER, " // group chat
                        + "%s INTEGER, " // video chat
                        + "%s INTEGER " // create time
                        + ")"
                , TaskEntity.ID
                , TaskEntity.NAME
                , TaskEntity.IMAGE
                , TaskEntity.IS_GROUP_CHAT
                , TaskEntity.IS_VIDOP_CHAT
                , TaskEntity.CREATE_TIME

        ));
    }

}