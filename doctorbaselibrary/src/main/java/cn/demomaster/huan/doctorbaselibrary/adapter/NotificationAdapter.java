package cn.demomaster.huan.doctorbaselibrary.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.NotificationModel;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder


    public class VH2 extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_message;

        public VH2(View v) {
            super(v);
            iv_icon = v.findViewById(R.id.iv_icon);
            iv_icon.setImageResource(iconResId);
            tv_message = v.findViewById(R.id.tv_message);
        }
    }

    private List<NotificationModel> mDatas;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    int iconResId;

    public NotificationAdapter(List<NotificationModel> data) {
        this.mDatas = data;
        boolean isPatient = AppConfig.getInstance().isPatient();
        iconResId = getResId(isPatient ? "ic_launcher_patient" : "ic_launcher_doctor", R.mipmap.class);
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        VH2 holder1 = (VH2) holder;
        holder1.tv_message.setText(mDatas.get(position).getMessage());
        holder1.itemView.setTag(position);
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(v, (int) v.getTag(), mDatas.get((int) v.getTag()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_notification, parent, false);
        return new VH2(v);
    }

    public static interface OnItemClickListener {
        void onItemClicked(View view, int position, NotificationModel notificationModel);
    }

    public int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
