package com.ziwenwen.onekeychat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ziwenwen.onekeychat.entity.TaskEntity;

/**
 * Created by ziwen.wen on 2018/1/18.
 */
public class GridAdapter extends ArrayAdapter<TaskEntity> {
    public GridAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.bind(position, getItem(position));
        return convertView;
    }

    private static class Holder {
        View itemView;
        ImageView imageView;
        TextView tvName;

        public Holder(View itemView) {
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.image);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(int position, TaskEntity item) {
            Glide.with(itemView.getContext())
                    .load(item.getImage())
                    .placeholder(R.color.widget_bg)
                    .into(imageView);
            if (TextUtils.isEmpty(item.getName())) {
                tvName.setText("");
            } else {
                tvName.setText(item.getName());
            }
        }
    }
}
