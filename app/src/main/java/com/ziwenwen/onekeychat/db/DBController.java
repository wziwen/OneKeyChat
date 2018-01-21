package com.ziwenwen.onekeychat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ziwenwen.onekeychat.App;
import com.ziwenwen.onekeychat.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class DBController {
    public final static String TASK_TABLE_NAME = "task";
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
        final Cursor c = db.rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " order by " + TaskEntity.CREATE_TIME + " ASC", null);

        final List<TaskEntity> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                TaskEntity model = new TaskEntity();
                model.setId(c.getLong(c.getColumnIndex(TaskEntity.ID)));
                model.setName(c.getString(c.getColumnIndex(TaskEntity.NAME)));
                model.setImage(c.getString(c.getColumnIndex(TaskEntity.IMAGE)));
                model.setIsVideoChat(c.getInt(c.getColumnIndex(TaskEntity.IS_VIDOP_CHAT)));
                model.setIsGroupChat(c.getInt(c.getColumnIndex(TaskEntity.IS_GROUP_CHAT)));
                model.setCreatetime(c.getLong(c.getColumnIndex(TaskEntity.CREATE_TIME)));
                list.add(model);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return list;
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

    public boolean deleteTask(long id) {
        try {
            return 0 != db.delete(TASK_TABLE_NAME, "id=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean insertOrUpdate(TaskEntity taskEntity) {
        if (taskEntity.getCreatetime() == 0) {
            taskEntity.setCreatetime(System.currentTimeMillis());
        }
        long id = db.insertWithOnConflict(TASK_TABLE_NAME, null, taskEntity.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        if (id != -1) {
            taskEntity.setId(id);
            return true;
        }
        return false;
    }
}