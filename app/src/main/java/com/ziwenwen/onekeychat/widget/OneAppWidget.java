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

import com.ziwenwen.onekeychat.ListActivity;
import com.ziwenwen.onekeychat.R;
import com.ziwenwen.onekeychat.TaskManager;
import com.ziwenwen.onekeychat.entity.TaskEntity;
import com.ziwenwen.onekeychat.utils.BitmapUtils;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class OneAppWidget extends AppWidgetProvider {
    private static final String TAG = "OneAppWidget";

    static final int WIDGET_RES = R.id.view_one;
    static final int TEXT_RES = R.id.tvName_one;
    static final int IMG_RES = R.id.image_one;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, TaskEntity entity) {

        RemoteViews remoteViews;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_one_or_two);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int maxWidth = 500;
        if (wm != null) {
//            Display display = wm.getDefaultDisplay();
//            maxWidth = display.getWidth();
        }
        if (entity != null) {
            if (!TextUtils.isEmpty(entity.getName())) {
                remoteViews.setTextViewText(TEXT_RES, entity.getName());
            }
            if (!TextUtils.isEmpty(entity.getImage())) {
                Bitmap bitmap = BitmapUtils.getImageFromPath(entity.getImage(), maxWidth, maxWidth);
                if (bitmap != null) {
                    remoteViews.setImageViewBitmap(IMG_RES, bitmap);
                }
            }
            Intent configIntent = new Intent(context, ListActivity.class);
            entity.saveToIntent(configIntent);
//            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setViewVisibility(WIDGET_RES, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(WIDGET_RES, configPendingIntent);
//        }
        } else {
            remoteViews.setTextViewText(TEXT_RES, "");
            remoteViews.setImageViewBitmap(IMG_RES, null);
            remoteViews.setOnClickPendingIntent(WIDGET_RES, null);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateWidget(context, appWidgetManager);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager) {
        int[] appWidgetIds;List<TaskEntity> taskEntityList = TaskManager.getInstance().getAllTasks();
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

