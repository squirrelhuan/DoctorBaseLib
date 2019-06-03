package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.util.List;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.DepartMentModel;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DoctorListActivity;

/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class RecycleDepartmentAdapter extends RecyclerView.Adapter<RecycleDepartmentAdapter.ViewHolder> {


    private List<DepartMentModel> lists = null;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RecycleDepartmentAdapter(Context context, List<DepartMentModel> lists) {
        this.context = context;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_department, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DepartMentModel departMentModel = lists.get(position);
        holder.tv_depart_title.setText(lists.get(position).getName());
        int id = getResId(departMentModel.getIconName(), R.mipmap.class);
        try {
            BitmapFactory.decodeResource(context.getResources(), id);
            holder.iv_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //holder.itemView.setTag(departMentModel.getCode());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int)v.getTag();
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v,p);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("category", lists.get(p).getCode());
                    ((BaseActivity) context).startActivity(DoctorListActivity.class, bundle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_depart_title;
        private ImageView iv_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_depart_title = itemView.findViewById(R.id.tv_depart_title);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static interface OnItemClickListener {
        void onClick(View v, int position);
    }

}

