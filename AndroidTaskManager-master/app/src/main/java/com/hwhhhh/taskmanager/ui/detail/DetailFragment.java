package com.hwhhhh.taskmanager.ui.detail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.hwhhhh.taskmanager.MainActivity;
import com.hwhhhh.taskmanager.R;
import com.hwhhhh.taskmanager.dto.TaskDto;
import com.hwhhhh.taskmanager.entity.TaskContent;
import com.hwhhhh.taskmanager.service.ServiceImpl.TaskServiceImpl;
import com.hwhhhh.taskmanager.service.TaskService;
import com.hwhhhh.taskmanager.ui.EditFragment.EditFragment;
import com.hwhhhh.taskmanager.ui.main.MainFragment;
import com.hwhhhh.taskmanager.util.MessageEvent;
import com.hwhhhh.taskmanager.util.MySet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private View rootView;
    private TaskDto taskDto;
    private List<MySet<TextView, TextView, TextView>> contentList;
    private static final int INIT_VIEW = 1;
    private static final int REFRESH_VIEW = 2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Log.d(TAG, "onCreateView: ");
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        contentList = new ArrayList<>();
        MyHandler handler = new MyHandler(this);
        handler.sendEmptyMessage(INIT_VIEW);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged: ");
        if (!hidden) {
            MyHandler handler = new MyHandler(this);
            handler.sendEmptyMessage(REFRESH_VIEW);
        }
    }

    private void initView() {
        TextView tv_title = rootView.findViewById(R.id.detail_title);
        TextView tv_startTime = rootView.findViewById(R.id.detail_startTime);
        TextView tv_endTime = rootView.findViewById(R.id.detail_endTime);
        TextView tv_level = rootView.findViewById(R.id.detail_level);
        TextView tv_status = rootView.findViewById(R.id.detail_completeStatus);

        tv_title.setText(this.taskDto.getTaskTitle());
        tv_startTime.setText(this.taskDto.getStartTime());
        tv_endTime.setText(this.taskDto.getEndTime());
        tv_level.setText(this.taskDto.getTaskLevel());
        tv_status.setText(this.taskDto.getCompleteStatus());
        String label;
        int lastId = R.id.detail_label_completeStatus;
        final List<TaskContent> taskContentList = this.taskDto.getTaskContentBeanList();
        for (int i = 0; i < taskContentList.size(); i++) {
            label = "任务内容" + (i + 1);
            lastId = addContent(label, taskContentList.get(i).getTaskContent(), lastId);
        }

        Button button = rootView.findViewById(R.id.detail_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.switchFragment(DetailFragment.this, mainActivity.getEditFragment());
                    mainActivity.getEditFragment().setMessageEvent(new MessageEvent(MessageEvent.EDIT, getTaskDto()));
                }
            }
        });

        Button btn_delete = rootView.findViewById(R.id.detail_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskService taskService = new TaskServiceImpl();
                taskService.deleteTask(taskDto.getId());
                Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchFragment(DetailFragment.this, mainActivity.getMainFragment());
            }
        });
    }

    /**
     * 动态添加任务内容
     * @param mLabel    对应任务内容的Label
     * @param taskContent   任务内容
     * @param lastId    最后一个view的id
     * @return  最后一个view的id
     */
    private int addContent(String mLabel, String taskContent, int lastId) {
        ConstraintLayout layout = rootView.findViewById(R.id.detail_layout);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, getPixelsFromDp(40)
        );
        //设置任务内容Label的参数
        params.endToEnd = lastId;
        params.topToBottom = lastId;
        params.topMargin = getPixelsFromDp(15);
        TextView label = new TextView(getActivity());
        label.setId(View.generateViewId());
        label.setText(mLabel);
        label.setTextSize(24);
        layout.addView(label, params);
        //设置具体任务内容
        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(
                0, getPixelsFromDp(40)
        );
        params1.endToEnd = R.id.detail_layout;
        params1.startToEnd = label.getId();
        params1.topToTop = label.getId();
        params1.bottomToBottom = label.getId();

        TextView content = new TextView(getActivity());
        content.setId(View.generateViewId());
        content.setText(taskContent);
        content.setTextSize(24);
        content.setPadding(getPixelsFromDp(15), 0, 0, 0);
        content.setTextColor(getResources().getColor(R.color.black));
        layout.addView(content, params1);

        contentList.add(new MySet<TextView, TextView, TextView>(label, content));

        return label.getId();
    }

    private TaskDto getTaskDto() {
        return taskDto;
    }

    public void setTaskDto(TaskDto taskDto) {
        this.taskDto = taskDto;
    }

    /**
     * 将dp转换为Pixels （因为xml习惯使用的是dp，而此时写params需要的单位是pixels）公式为px = dp * (dpi / 160)
     * DisplayMetrics.DENSITY_DEFAULT 即为160
     * DisplayMetrics是安卓提供的封装像素密度以及大小信息的类。前两行代码是对他的初始化
     * @param size
     * @return
     */
    private int getPixelsFromDp(int size) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
    }

    private static class MyHandler extends Handler {
        WeakReference<DetailFragment> weakReference;
        public MyHandler(DetailFragment detailFragment) {
            weakReference = new WeakReference<>(detailFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DetailFragment fragment = weakReference.get();
            MainActivity activity = (MainActivity) fragment.getActivity();
            switch (msg.what) {
                case INIT_VIEW:
                    fragment.initView();
                    break;
                case REFRESH_VIEW:
                    if (fragment.contentList.size() > 0) {
                        ConstraintLayout layout = activity.findViewById(R.id.detail_layout);
                        for (int i = 0; i < fragment.contentList.size(); i++) {
                            layout.removeView(fragment.contentList.get(i).getKey());
                            layout.removeView(fragment.contentList.get(i).getValue());
                        }
                        fragment.contentList.clear();
                    }
                    fragment.initView();
                    break;
            }
        }
    }
}
