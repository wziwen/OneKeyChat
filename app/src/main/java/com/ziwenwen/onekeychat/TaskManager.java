package com.ziwenwen.onekeychat;

import com.ziwenwen.onekeychat.db.DBController;
import com.ziwenwen.onekeychat.entity.TaskEntity;

import java.util.List;

/**
 * Created by ziwen.wen on 2018/1/18.
 */
public class TaskManager {
    private static final TaskManager ourInstance = new TaskManager();

    public static TaskManager getInstance() {
        return ourInstance;
    }

    DBController dbController;

    List<TaskEntity> allTasks;

    private TaskManager() {
        dbController = DBController.instance();
        allTasks = dbController.getAllTasks();
    }

    public List<TaskEntity> getAllTasks() {
        return allTasks;
    }

    public void insertOrUpdate(TaskEntity taskEntity) {
        if (dbController.insertOrUpdate(taskEntity)) {
            allTasks = dbController.getAllTasks();
        }
    }

    public boolean deleteTask(long id) {
        return dbController.deleteTask(id);
    }
}
