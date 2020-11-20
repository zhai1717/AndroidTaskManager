package com.hwhhhh.taskmanager.ui.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hwhhhh.taskmanager.MainActivity;
import com.hwhhhh.taskmanager.R;
import com.hwhhhh.taskmanager.adapter.TaskDtoAdapter;
import com.hwhhhh.taskmanager.dto.TaskDto;
import com.hwhhhh.taskmanager.service.ServiceImpl.TaskServiceImpl;
import com.hwhhhh.taskmanager.service.TaskService;
import com.hwhhhh.taskmanager.ui.EditFragment.EditFragment;
import com.hwhhhh.taskmanager.ui.detail.DetailFragment;
import com.hwhhhh.taskmanager.util.HttpCallBack;
import com.hwhhhh.taskmanager.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyHandler handler = new MyHandler(this);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            MyHandler handler = new MyHandler(this);
            handler.sendEmptyMessage(2);
        }
    }

    private void initList() {
        TaskService taskService = new TaskServiceImpl();
        ListView lv_tasks = rootView.findViewById(R.id.main_listView);
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("加载中...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        lv_tasks.setAdapter(new TaskDtoAdapter(getContext(), taskService.getAllTask(new HttpCallBack() {
            @Override
            public void load() {
                dialog.show();
            }

            @Override
            public void success() {
                dialog.dismiss();
            }
        })));
        lv_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.switchFragment(MainFragment.this, mainActivity.getDetailFragment());
                    TaskDto taskDto = (TaskDto) adapterView.getItemAtPosition(i);
                    mainActivity.getDetailFragment().setTaskDto(taskDto);
                }
            }
        });
    }

    private void init() {
        initList();

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.main_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.switchFragment(MainFragment.this, mainActivity.getEditFragment());
                    mainActivity.getEditFragment().setMessageEvent(new MessageEvent(MessageEvent.CREATE, new TaskDto()));
                }
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<MainFragment> weakReference;

        public MyHandler(MainFragment mainFragment) {
            weakReference = new WeakReference<>(mainFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainFragment mainFragment = weakReference.get();
            switch (msg.what) {
                case 1:
                    mainFragment.init();
                    break;
                case 2:
                    mainFragment.initList();
                    break;
            }
        }
    }
}
