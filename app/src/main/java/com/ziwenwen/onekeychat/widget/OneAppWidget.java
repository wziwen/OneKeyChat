package com.ziwenwen.onekeychat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.ziwenwen.onekeychat.MainActivity;
import com.ziwenwen.onekeychat.R;
import com.ziwenwen.onekeychat.TaskManager;
import com.ziwenwen.onekeychat.entity.TaskEntity;
import com.ziwenwen.onekeychat.BitmapUtils;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class OneAppWidget extends AppWidgetProvider {
    private static final String TAG = "OneAppWidget";

    static final int[] WIDGET_RES = {R.id.view_one, R.id.view_two};
    static final int[] TEXT_RES = {R.id.tvName_one, R.id.tvName_two};
    static final int[] IMG_RES = { R.id.image_one, R.id.image_two};

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, TaskEntity entity) {

        RemoteViews remoteViews;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_one_or_two);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int maxWidth = 400;
        if (wm != null) {
            Display display = wm.getDefaultDisplay();
            maxWidth = display.getWidth();
        }
//        for (int i = 0; i < taskEntityList.size() && i < 2; i ++) {
        int i = 0;
        if (entity != null) {
            if (!TextUtils.isEmpty(entity.getName())) {
                remoteViews.setTextViewText(TEXT_RES[i], entity.getName());
            }
            if (!TextUtils.isEmpty(entity.getImage())) {
                Bitmap bitmap = BitmapUtils.getImageFromPath(entity.getImage(), maxWidth, maxWidth);
                if (bitmap != null) {
                    remoteViews.setImageViewBitmap(IMG_RES[i], bitmap);
                }
            }
            Intent configIntent = new Intent(context, MainActivity.class);
            configIntent.putExtra("task", entity);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            remoteViews.setViewVisibility(WIDGET_RES[i], View.VISIBLE);
            remoteViews.setOnClickPendingIntent(WIDGET_RES[i], configPendingIntent);
//        }
        } else {
            remoteViews.setTextViewText(TEXT_RES[i], "");
            remoteViews.setImageViewBitmap(IMG_RES[i], null);
            remoteViews.setOnClickPendingIntent(WIDGET_RES[i], null);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        List<TaskEntity> taskEntityList = TaskManager.getInstance().getAllTasks();
        int i = 0;
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, OneAppWidget.class));
        for (; i < appWidgetIds.length && i < taskEntityList.size(); i ++) {
            int appWidgetId = appWidgetIds[i];
            TaskEntity entity = null;
            if (i < taskEntityList.size()) {
                entity = taskEntityList.get(i);
            }
            updateAppWidget(context, appWidgetManager, appWidgetId, entity);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxWidth  = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight  = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        Log.d(TAG, String.format("values: minWidth:%d    minHeight:%d    maxWidth:%d    maxHeight:%d", minWidth, minHeight, maxWidth, maxHeight));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }



}

