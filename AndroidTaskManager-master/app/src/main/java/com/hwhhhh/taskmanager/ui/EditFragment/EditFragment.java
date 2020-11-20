package com.hwhhhh.taskmanager.ui.EditFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.hwhhhh.taskmanager.ui.main.MainFragment;
import com.hwhhhh.taskmanager.util.JSONResult;
import com.hwhhhh.taskmanager.util.MessageEvent;
import com.hwhhhh.taskmanager.util.MySet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditFragment extends Fragment {
    private static final String TAG = "EditFragment";
    private View rootView;
    private MessageEvent messageEvent;
    private MyHandler myHandler;
    private int btn_add_id;
    private List<MySet<TextView, EditText, Button>> contentList;


    public void setMessageEvent(MessageEvent messageEvent) {
        this.messageEvent = messageEvent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        contentList = new ArrayList<>();
        myHandler = new MyHandler(EditFragment.this);
        initView();
    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (contentList.size() > 0) {
//                    myHandler.sendEmptyMessage(MessageEvent.INIT_DELETE);
//                }
                if (messageEvent.getType() == MessageEvent.EDIT) {
                    myHandler.sendEmptyMessage(MessageEvent.EDIT);
                } else {
                    myHandler.sendEmptyMessage(MessageEvent.CREATE);
                }
                myHandler.sendEmptyMessage(MessageEvent.INIT_BUTTON);
            }
        }).start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(TAG, "onHiddenChanged: ");
            initView();
        } else {
            if (contentList.size() > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myHandler.sendEmptyMessage(MessageEvent.INIT_DELETE);
                    }
                }).start();
            }
        }
    }

    /**
     * 同步信息。
     */
    private void refreshTaskDto() {
        EditText et_title = rootView.findViewById(R.id.edit_title);
        EditText et_startTime = rootView.findViewById(R.id.edit_startTime);
        EditText et_endTime = rootView.findViewById(R.id.edit_endTime);
        EditText et_level = rootView.findViewById(R.id.edit_level);
        TaskDto taskDto = messageEvent.getTaskDto();
        taskDto.setTaskTitle(et_title.getText().toString());
        taskDto.setStartTime(et_startTime.getText().toString());
        taskDto.setEndTime(et_endTime.getText().toString());
        taskDto.setTaskLevel(et_level.getText().toString());
        List<String> list = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i ++) {
            list.add(contentList.get(i).getValue().getText().toString());
        }
        //同步任务内容
        taskDto.synchronizationContent(list);
    }

    /**
     * 初始化任务内容
     * @param index    第几个任务内容
     * @param taskContent   任务内容
     * @param mySet    最后一行的view
     * @return  最后一个view的id
     */
    private MySet<TextView, EditText, Button> initContent(int index, String taskContent, MySet<TextView, EditText, Button> mySet) {
        final ConstraintLayout layout = rootView.findViewById(R.id.edit_layout);
        //label
        final TextView label = new TextView(getActivity());
        label.setId(View.generateViewId());
        label.setText("任务内容" + index);
        label.setTextSize(24);
        //任务内容
        final EditText content = new EditText(getActivity());
        content.setId(View.generateViewId());
        content.setText(taskContent);
        content.setTextSize(24);
        content.setPadding(getPixelsFromDp(15), content.getPaddingTop(), content.getPaddingRight(), content.getPaddingBottom());
        content.setTextColor(getResources().getColor(R.color.black));
        //删除按钮
        final Button btn_delete = new Button(getActivity());
        btn_delete.setId(View.generateViewId());
        btn_delete.setText("删除");

        //设置任务内容Label的参数
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, getPixelsFromDp(40)
        );
        params.endToEnd = mySet.getKey().getId();
        params.topToTop = content.getId();
        params.bottomToBottom = content.getId();
        layout.addView(label, params);
        //设置具体任务内容的参数
        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params1.endToStart = btn_delete.getId();
        params1.startToEnd = label.getId();
        params1.topToBottom = mySet.getValue().getId();
        params.topMargin = getPixelsFromDp(15);
        layout.addView(content, params1);
        //删除按钮的参数
        ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params2.endToEnd = R.id.edit_layout;
        params2.startToEnd = content.getId();
        params2.topToTop = content.getId();
        params2.bottomToBottom = content.getId();
        layout.addView(btn_delete, params2);

        final MySet<TextView, EditText, Button> set = new MySet<>();
        set.put(label, content, btn_delete);
        contentList.add(set);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContent(contentList.indexOf(set));
            }
        });
        return set;
    }

    /**
     * 用于创建第一个的任务内容
     * @param index    第几个任务内容
     * @param taskContent   具体内容
     * @param labelId   最后一个label的id
     * @param contentId     最后一个内容的id
     * @return
     */
    private MySet<TextView, EditText, Button> initContent(int index, String taskContent, int labelId, int contentId) {

        return this.initContent(index, taskContent,
                new MySet<TextView, EditText, Button>((TextView) rootView.findViewById(labelId),(EditText) rootView.findViewById(contentId)));
    }

    private void initButton() {
        ConstraintLayout layout = rootView.findViewById(R.id.edit_layout);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        Button btn_add = new Button(getActivity());
        btn_add.setText("增加");
        btn_add.setId(View.generateViewId());
        btn_add_id = btn_add.getId();
        params.leftToLeft = R.id.edit_layout;
        params.topToBottom = contentList.get(contentList.size() - 1).getKey().getId();
        params.topMargin = getPixelsFromDp(15);
        params.rightToRight = R.id.edit_layout;
        layout.addView(btn_add, params);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContent();
            }
        });
    }

    private void addContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(MessageEvent.ADD);
            }
        }).start();
    }

    /**
     * 删除任务内容
     */
    private void deleteContent(final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = myHandler.obtainMessage();
                message.what = MessageEvent.DELETE;
                message.arg1 = index;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 将dp转换为Pixels （因为xml习惯使用的是dp，而此时写params需要的单位是pixels）公式为px = dp * (dpi / 160)
     * DisplayMetrics.DENSITY_DEFAULT 即为160
     * DisplayMetrics是安卓提供的封装像素密度以及大小信息的类。前两行代码是对他的初始化
     * @param size  dp对应的值
     * @return
     */
    private int getPixelsFromDp(int size) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return size * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
    }

    private static class MyHandler extends Handler {
        WeakReference<EditFragment> weakReference;

        private MyHandler(EditFragment editFragment) {
            weakReference = new WeakReference<>(editFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            final EditFragment editFragment = weakReference.get();
            final MainActivity mainActivity = (MainActivity) editFragment.getActivity();
            EditText et_title = editFragment.rootView.findViewById(R.id.edit_title);
            EditText et_startTime = editFragment.rootView.findViewById(R.id.edit_startTime);
            EditText et_endTime = editFragment.rootView.findViewById(R.id.edit_endTime);
            EditText et_level = editFragment.rootView.findViewById(R.id.edit_level);
            Button btn_save = editFragment.rootView.findViewById(R.id.edit_save);
            switch (msg.what) {
                case MessageEvent.CREATE:
                    et_title.setText("");
                    et_startTime.setText("");
                    et_endTime.setText("");
                    et_level.setText("");
                    editFragment.initContent(1, "", R.id.edit_label_level, R.id.edit_level);
                    btn_save.setText("创建");
                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editFragment.refreshTaskDto();
                            TaskServiceImpl taskService = new TaskServiceImpl();
                            taskService.createTask(editFragment.messageEvent.getTaskDto());
                            //暂时还没有判定是否创建成功
                            Toast.makeText(mainActivity, "创建成功", Toast.LENGTH_SHORT).show();
                            mainActivity.onBackPressed();
                        }
                    });
                    break;
                case MessageEvent.EDIT:
                    TaskDto taskDto = editFragment.messageEvent.getTaskDto();
                    et_title.setText(taskDto.getTaskTitle());
                    et_startTime.setText(taskDto.getStartTime());
                    et_endTime.setText(taskDto.getEndTime());
                    et_level.setText(taskDto.getTaskLevel());
                    btn_save.setText("保存");
                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editFragment.refreshTaskDto();
                            TaskServiceImpl service = new TaskServiceImpl();
                            service.modifyTask(editFragment.messageEvent.getTaskDto());
                            Toast.makeText(mainActivity, "修改成功！", Toast.LENGTH_SHORT).show();
                            mainActivity.onBackPressed();
                        }
                    });
                    List<TaskContent> taskContents = taskDto.getTaskContentBeanList();
                    MySet<TextView, EditText, Button> set = null;
                    for (int i = 0; i < taskContents.size(); i ++) {
                        if (set == null) {
                            set = editFragment.initContent(i + 1, taskContents.get(i).getTaskContent(), R.id.edit_label_level, R.id.edit_level);
                        } else {
                            set = editFragment.initContent(i + 1, taskContents.get(i).getTaskContent(), set);
                        }
                    }
                    break;
                case MessageEvent.INIT_BUTTON:
                    editFragment.initButton();
                    break;
                case MessageEvent.DELETE:
                    int index = msg.arg1;
                    //首先删除对应的任务内容
                    ConstraintLayout layout = editFragment.rootView.findViewById(R.id.edit_layout);
                    MySet<TextView, EditText, Button> set1 = editFragment.contentList.get(index);
                    layout.removeView(set1.getKey());
                    layout.removeView(set1.getValue());
                    layout.removeView(set1.getBtn());
                    editFragment.contentList.remove(index);
                    ConstraintSet constraintSet = new ConstraintSet();
                    //从根布局复制约束
                    constraintSet.clone(layout);
                    //判断是否是删除第一个。index为0表示删除的是第一个
                    if (index == 0) {
                        //删除完之后如果还有任务内容
                        if (editFragment.contentList.size() > 0) {
                            MySet<TextView, EditText, Button> set2 = editFragment.contentList.get(0);
                            TextView label = set2.getKey();
                            EditText editText = set2.getValue();
                            Button button = set2.getBtn();
                            //清除约束
                            constraintSet.clear(label.getId());
                            constraintSet.clear(editText.getId());
                            constraintSet.clear(button.getId());
                            //设置对应的宽高
                            constraintSet.constrainWidth(label.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            constraintSet.constrainHeight(label.getId(), editFragment.getPixelsFromDp(40));
                            constraintSet.constrainWidth(editText.getId(), 0);
                            constraintSet.constrainHeight(editText.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            constraintSet.constrainWidth(button.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            constraintSet.constrainHeight(button.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            //设置相应的约束
                            constraintSet.connect(label.getId(), ConstraintSet.END, R.id.edit_label_level, ConstraintSet.END);
                            constraintSet.connect(label.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.TOP);
                            constraintSet.connect(label.getId(), ConstraintSet.BOTTOM, editText.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(editText.getId(), ConstraintSet.TOP, R.id.edit_level, ConstraintSet.BOTTOM);
                            constraintSet.connect(editText.getId(), ConstraintSet.START, label.getId(), ConstraintSet.END);
                            constraintSet.connect(editText.getId(), ConstraintSet.END, button.getId(), ConstraintSet.START);
                            constraintSet.connect(button.getId(), ConstraintSet.END, R.id.edit_layout, ConstraintSet.END);
                            constraintSet.connect(button.getId(), ConstraintSet.START, editText.getId(), ConstraintSet.END);
                            constraintSet.connect(button.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.TOP);
                            constraintSet.connect(button.getId(), ConstraintSet.BOTTOM, editText.getId(), ConstraintSet.BOTTOM);
                            for (int i = 0; i < editFragment.contentList.size(); i++) {
                                editFragment.contentList.get(i).getKey().setText("任务内容" + (i + 1));
                            }
                        } else {    //如果没有内容了，即只剩添加按钮
                            constraintSet.clear(editFragment.btn_add_id);
                            constraintSet.constrainWidth(editFragment.btn_add_id, ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainHeight(editFragment.btn_add_id, ConstraintSet.WRAP_CONTENT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.LEFT, R.id.edit_layout, ConstraintSet.LEFT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.RIGHT, R.id.edit_layout, ConstraintSet.RIGHT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.TOP, R.id.edit_level, ConstraintSet.BOTTOM);
                        }
                    } else { //删除的不是第一个
                        if (index == editFragment.contentList.size()) {  //如果删除的是最后一个,则只需改动添加按钮
                            constraintSet.clear(editFragment.btn_add_id);
                            constraintSet.constrainWidth(editFragment.btn_add_id, ConstraintSet.WRAP_CONTENT);
                            constraintSet.constrainHeight(editFragment.btn_add_id, ConstraintSet.WRAP_CONTENT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.LEFT, R.id.edit_layout, ConstraintSet.LEFT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.RIGHT, R.id.edit_layout, ConstraintSet.RIGHT);
                            constraintSet.connect(editFragment.btn_add_id, ConstraintSet.TOP, editFragment.contentList.get(index - 1).getValue().getId(), ConstraintSet.BOTTOM);
                        } else {    //不是最后一个
                            MySet<TextView, EditText, Button> set2 = editFragment.contentList.get(index);
                            TextView label = set2.getKey();
                            EditText editText = set2.getValue();
                            Button button = set2.getBtn();
                            //设置相应的约束,无需清楚原有约束，只需修改
                            constraintSet.connect(label.getId(), ConstraintSet.END, R.id.edit_label_level, ConstraintSet.END);
                            constraintSet.connect(label.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.TOP);
                            constraintSet.connect(label.getId(), ConstraintSet.BOTTOM, editText.getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(editText.getId(), ConstraintSet.TOP, editFragment.contentList.get(index - 1).getValue().getId(), ConstraintSet.BOTTOM);
                            constraintSet.connect(editText.getId(), ConstraintSet.START, label.getId(), ConstraintSet.END);
                            constraintSet.connect(editText.getId(), ConstraintSet.END, button.getId(), ConstraintSet.START);
                            constraintSet.connect(button.getId(), ConstraintSet.END, R.id.edit_layout, ConstraintSet.END);
                            constraintSet.connect(button.getId(), ConstraintSet.START, editText.getId(), ConstraintSet.END);
                            constraintSet.connect(button.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.TOP);
                            constraintSet.connect(button.getId(), ConstraintSet.BOTTOM, editText.getId(), ConstraintSet.BOTTOM);
                            for (int i = index; i < editFragment.contentList.size(); i++) {
                                editFragment.contentList.get(i).getKey().setText("任务内容" + (i+1));
                            }
                        }
                    }
                    constraintSet.applyTo(layout);
                    break;
                case MessageEvent.ADD:
                    ConstraintLayout layout1 = editFragment.rootView.findViewById(R.id.edit_layout);
                    ConstraintSet constraintSet1 = new ConstraintSet();
                    constraintSet1.clone(layout1);
                    MySet<TextView, EditText, Button> set2;
                    if (editFragment.contentList.size() > 0) {
                        set2 = editFragment.initContent(editFragment.contentList.size() + 1, "", editFragment.contentList.get(editFragment.contentList.size() - 1));
                    } else {
                        set2 = editFragment.initContent(1, "", R.id.edit_label_level, R.id.edit_level);
                    }
                    constraintSet1.connect(editFragment.btn_add_id, ConstraintSet.TOP, set2.getValue().getId(), ConstraintSet.BOTTOM);
                    constraintSet1.applyTo(layout1);
                    break;
                case MessageEvent.INIT_DELETE:
                    if (editFragment.contentList.size() > 0) {
                        ConstraintLayout layout3 = editFragment.rootView.findViewById(R.id.edit_layout);
                        for (int i = 0; i < editFragment.contentList.size(); i++) {
                            layout3.removeView(editFragment.contentList.get(i).getKey());
                            layout3.removeView(editFragment.contentList.get(i).getValue());
                            layout3.removeView(editFragment.contentList.get(i).getBtn());
                        }
                        layout3.removeView(editFragment.rootView.findViewById(editFragment.btn_add_id));
                        editFragment.contentList.clear();
                    }
                    break;
            }
        }
    }
}
