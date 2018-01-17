package com.ziwenwen.onekeychat.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ziwenwen.onekeychat.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GridWidgetService extends RemoteViewsService{

    private static final String TAG = "GridWidgetService";
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "GridWidgetService");
        return new GridRemoteViewsFactory(this, intent);
    }

    private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;

        private String IMAGE_ITEM = "imgage_item";
        private String TEXT_ITEM = "text_item";
        private ArrayList<HashMap<String, Object>> data ;

        private String[] arrText = new String[]{
                "Picture 1", "Picture 2", "Picture 3",
                "Picture 4", "Picture 5", "Picture 6",
                "Picture 7", "Picture 8", "Picture 9"
        };

        /**
         * 构造GridRemoteViewsFactory
         * @author skywang
         */
        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG, "GridRemoteViewsFactory mAppWidgetId:"+mAppWidgetId);
        }

        @Override
        public RemoteViews getViewAt(int position) {
            HashMap<String, Object> map;

            Log.d(TAG, "GridRemoteViewsFactory getViewAt:"+position);
            // 获取 grid_view_item.xml 对应的RemoteViews
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.grid_view_item);

            // 设置 第position位的“视图”的数据
            map = data.get(position);
//            rv.setImageViewResource(R.id.image, (Integer) map.get(IMAGE_ITEM));
            rv.setTextViewText(R.id.tvName, (String)map.get(TEXT_ITEM));

            // 设置 第position位的“视图”对应的响应事件
            Intent fillInIntent = new Intent();
//            fillInIntent.putExtra(GridWidgetProvider.COLLECTION_VIEW_EXTRA, position);
//            rv.setOnClickFillInIntent(R.id.itemLayout, fillInIntent);

            return rv;
        }

        /**
         * 初始化GridView的数据
         * @author skywang
         */
        private void initGridViewData() {
            data = new ArrayList<HashMap<String, Object>>();

            for (int i=0; i<9; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put(IMAGE_ITEM, arrImages[i]);
                map.put(TEXT_ITEM, arrText[i]);
                data.add(map);
            }
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "onCreate");
            // 初始化“集合视图”中的数据
            initGridViewData();
        }

        @Override
        public int getCount() {
            // 返回“集合视图”中的数据的总数
            return data.size();
        }

        @Override
        public long getItemId(int position) {
            // 返回当前项在“集合视图”中的位置
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            // 只有一类 GridView
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            data.clear();
        }
    }
}