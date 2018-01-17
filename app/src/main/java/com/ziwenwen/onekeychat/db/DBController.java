package com.ziwenwen.onekeychat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ziwenwen.onekeychat.App;
import com.ziwenwen.onekeychat.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class DBController {
    public final static String TASK_TABLE_NAME = "taks";
    private final SQLiteDatabase db;

    private static DBController instance = new DBController();

    private DBController() {
        DBOpenHelper openHelper = new DBOpenHelper(App.getAppContext());
        db = openHelper.getWritableDatabase();
    }

    public static DBController instance() {
        return instance;
    }

    public List<TaskEntity> getAllTasks() {
        final Cursor c = db.rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " order by " + TaskEntity.CREATE_TIME, null);

        final List<TaskEntity> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }

            do {
                TaskEntity model = new TaskEntity();
                model.setId(c.getLong(c.getColumnIndex(TaskEntity.ID)));
                model.setName(c.getString(c.getColumnIndex(TaskEntity.NAME)));
                model.setIsVideoChat(c.getInt(c.getColumnIndex(TaskEntity.IS_VIDOP_CHAT)));
                model.setIsGroupChat(c.getInt(c.getColumnIndex(TaskEntity.IS_GROUP_CHAT)));
                model.setCreatetime(c.getLong(c.getColumnIndex(TaskEntity.CREATE_TIME)));
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return list;
    }

    public TaskEntity addTask(String name,
                              int isVideoChat,
                              int isGroupChat) {
        TaskEntity model = new TaskEntity();
        model.setName(name);
        model.setIsVideoChat(isVideoChat);
        model.setIsGroupChat(isGroupChat);
        model.setCreatetime(System.currentTimeMillis());

        long id = db.insert(TASK_TABLE_NAME, null, model.toContentValues());
        if (id != -1) {
            model.setId(id);
            return model;
        }
        return null;
    }


    public boolean clearAllTask() {
        try {
            db.delete(TASK_TABLE_NAME, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}