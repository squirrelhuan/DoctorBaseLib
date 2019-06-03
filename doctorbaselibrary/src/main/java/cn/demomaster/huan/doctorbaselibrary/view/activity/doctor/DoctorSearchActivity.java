package cn.demomaster.huan.doctorbaselibrary.view.activity.doctor;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.RecycleDepartmentAdapter;
import cn.demomaster.huan.doctorbaselibrary.application.MyApp;
import cn.demomaster.huan.doctorbaselibrary.model.DepartMentModel;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.FlowLayout;

public class DoctorSearchActivity extends BaseActivity {

    RadioButton rbLeft;
    RadioButton rbRight;
    RecyclerView recyDrag;
    RadioGroup rgTab;
    TextView tv_search;
    FlowLayout flHistory;
    private LinearLayout ll_history;
    private LinearLayoutManager linearLayoutManager;
    private List<DepartMentModel> lists;
    private RecycleDepartmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_search);
        getActionBarLayoutOld().setTitle("查找专家");

        rbLeft = findViewById(R.id.rb_left);
        rbRight = findViewById(R.id.rb_right);
        recyDrag = findViewById(R.id.recy_drag);
        rgTab = findViewById(R.id.rg_tab);
        tv_search = findViewById(R.id.tv_search);
        flHistory = findViewById(R.id.fl_history);

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DoctorSearchDetailActivity.class);
            }
        });

        //List<DoctorModelApi> doctorModelApiList = new ArrayList<>();
        //flHistory.setAdapter(mContext, doctorModelApiList, null);

        //获取历史列表
        ll_history = findViewById(R.id.ll_history);

        init();
    }


    private void init() {

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new CallBack());
        mItemTouchHelper.attachToRecyclerView(recyDrag);

        //模拟一些数据加载
        lists = getDataByType(0);
        //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //使用网格布局展示
        recyDrag.setLayoutManager(new GridLayoutManager(this, 3));
        //recy_drag.setLayoutManager(linearLayoutManager);
        adapter = new RecycleDepartmentAdapter(this, lists);
        //设置分割线使用的divider
        //recy_drag.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL));
        recyDrag.setAdapter(adapter);

        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                lists.clear();
                if (checkedId == R.id.rb_left) {
                    lists.addAll(getDataByType(0));
                } else {
                    lists.addAll(getDataByType(1));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    List<DepartMentModel> getDataByType(int type) {
        List<DepartMentModel> lists = new ArrayList<>();
        Cursor cursor = MyApp.getInstance().db.query("inner_department_category", new String[]{"id", "categoryName", "categoryCode", "type", "iconName"}, "isValid=? and type=? ORDER BY \"index\" ASC", new String[]{"1", "" + type}, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("categoryName"));
            String code = cursor.getString(cursor.getColumnIndex("categoryCode"));
            //String type = cursor.getString(cursor.getColumnIndex("type"));
            String iconName = cursor.getString(cursor.getColumnIndex("iconName"));
            DepartMentModel departMentModel = new DepartMentModel();
            departMentModel.setName(name);
            departMentModel.setIconName(iconName);
            departMentModel.setCode(code);
            lists.add(departMentModel);
            Log.i(TAG, "query------->" + "categoryName：" + name + " " + ",categoryCode：" + code + ",type：" + type + ",iconName：" + iconName);
        }
        return lists;
    }


    public class CallBack extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(lists, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(lists, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            Log.i(TAG, "actionState=" + actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                Log.i(TAG, "IDLE。。。。。。");
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
                Log.i(TAG, "ACTION_STATE_DRAG。。。。。。");
            }
            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
                Log.i(TAG, "ACTION_STATE_SWIPE。。。。。。");
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundResource(R.drawable.department_grid);
            //viewHolder.itemView.setBackgroundColor(0);
        }
    }

}
