package com.ziwenwen.onekeychat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ziwenwen.onekeychat.entity.TaskEntity;

/**
 * Created by ziwen.wen on 2018/1/18.
 */
public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ADD_OR_MODIFY = 100;
    GridView gridView;
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(this);
        gridAdapter.addAll(TaskManager.getInstance().allTasks);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(this);
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
        startActivityForResult(intent, REQUEST_ADD_OR_MODIFY);
    }
}
