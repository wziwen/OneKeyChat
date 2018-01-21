package com.ziwenwen.onekeychat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tencent.stat.StatService;
import com.ziwenwen.onekeychat.entity.TaskEntity;
import com.ziwenwen.onekeychat.utils.OpenHelper;

/**
 * Created by ziwen.wen on 2018/1/18.
 */
public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ADD_OR_MODIFY = 100;
    private static final String TAG = "ListActivity";
    GridView gridView;
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("name")) {
            oneKeyChat(getIntent());
            Log.d(TAG, "has task on onCreate, just finish");
            finish();
            return;
        }

        setContentView(R.layout.activity_list);
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(this);
        gridAdapter.addAll(TaskManager.getInstance().allTasks);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "on new intent");
        if (intent.hasExtra("name")) {
            oneKeyChat(intent);
            moveTaskToBack(true);
        }
    }

    private void oneKeyChat(Intent intent) {
        String type = getIntent().getStringExtra("type");
        if ("type_shot_cut".equals(type)) {
            StatService.trackCustomKVEvent(this, "make_call_shot_cut", null);
        } else {
            StatService.trackCustomKVEvent(this, "make_call_app_widget", null);
        }

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.loadFromIntent(intent);
        OpenHelper.oneKeyChat(this, taskEntity.getName(), taskEntity.getIsVideoChat() == 1, taskEntity.getIsVideoChat() == 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(this, EditTaskActivity.class);
            StatService.trackCustomKVEvent(this, "create_task", null);
            startActivityForResult(intent, REQUEST_ADD_OR_MODIFY);
        } else if (item.getItemId() == R.id.menu_open_setting) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_OR_MODIFY) {
                gridAdapter.clear();
                gridAdapter.addAll(TaskManager.getInstance().allTasks);
                gridAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskEntity entity = gridAdapter.getItem(position);
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("task", entity);
        StatService.trackCustomKVEvent(this, "task_detail", null);
        startActivityForResult(intent, REQUEST_ADD_OR_MODIFY);
    }
}
