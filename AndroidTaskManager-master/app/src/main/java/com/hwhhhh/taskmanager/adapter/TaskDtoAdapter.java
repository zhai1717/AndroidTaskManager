package com.hwhhhh.taskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hwhhhh.taskmanager.R;
import com.hwhhhh.taskmanager.dto.TaskDto;

import java.util.List;

public class TaskDtoAdapter extends BaseAdapter {
    private List<TaskDto> taskDtoList;
    private Context mContext;

    public TaskDtoAdapter(Context context, List<TaskDto> data) {
        this.taskDtoList = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.taskDtoList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.taskDtoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.taskDtoList.get(i).getId();     //拿到的是对应TaskDto的id！
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TaskDto taskDto = (TaskDto) getItem(i);
        ViewHolder viewHolder;
        View convertView;
        if (view == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_taskdto, null);
            viewHolder = new ViewHolder();
            viewHolder.beginTime = convertView.findViewById(R.id.item_taskDto_beginTime);
            viewHolder.endTime = convertView.findViewById(R.id.item_taskDto_endTime);
            viewHolder.title = convertView.findViewById(R.id.item_taskDto_taskTitle);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.beginTime.setText(taskDto.getStartTime());
        viewHolder.endTime.setText(taskDto.getEndTime());
        viewHolder.title.setText(taskDto.getTaskTitle());
        return convertView;
    }

    private class ViewHolder {
        TextView beginTime, endTime, title;
    }
}
