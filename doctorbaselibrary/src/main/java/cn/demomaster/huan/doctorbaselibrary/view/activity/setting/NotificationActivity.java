package cn.demomaster.huan.doctorbaselibrary.view.activity.setting;

import android.database.Cursor;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.NotificationAdapter;
import cn.demomaster.huan.doctorbaselibrary.application.MyApp;
import cn.demomaster.huan.doctorbaselibrary.model.DepartMentModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.NotificationModel;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity {

    private RecyclerView rcv_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getActionBarLayoutOld().setTitle("消息中心");

        initView();
    }

    private NotificationAdapter notificationAdapter;
    List<NotificationModel> notificationModels = new ArrayList<>();
    private void initView() {
        rcv_notification = findViewById(R.id.rcv_notification);
        notificationModels = getNotification();
        notificationAdapter = new NotificationAdapter(notificationModels);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        rcv_notification.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        rcv_notification.setAdapter(notificationAdapter);

        //添加动画
        rcv_notification.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        rcv_notification.setItemAnimator(new DefaultItemAnimator());
    }

   public static List<NotificationModel> getNotification() {
        List<NotificationModel> lists = new ArrayList<>();
        Cursor cursor = MyApp.getInstance().db.query("app_message", new String[]{"id", "datetime", "message", "type", "sender"}, "", new String[]{ }, null, null, " \"id\" desc");
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String datetime = cursor.getString(cursor.getColumnIndex("datetime"));
            //String type = cursor.getString(cursor.getColumnIndex("type"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            NotificationModel departMentModel = new NotificationModel();
            departMentModel.setId(id);
            departMentModel.setTime(datetime);
            departMentModel.setMessage(message);
            lists.add(departMentModel);
            // Log.i(TAG, "query------->" + "categoryName：" + name + " " + ",categoryCode：" + code + ",type：" + type + ",iconName：" + iconName);
        }

        return lists;
    }
}
