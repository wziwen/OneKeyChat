package com.ziwenwen.onekeychat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.ziwenwen.onekeychat.entity.TaskEntity;

/**
 * Created by ziwen.wen on 2018/1/18.
 */
public class ListActivity extends AppCompatActivity {

    GridView gridView;
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(this);
        gridAdapter.add(new TaskEntity());
        gridAdapter.add(new TaskEntity());
        gridAdapter.add(new TaskEntity());
        gridView.setAdapter(gridAdapter);
    }
}
