package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.AppointmentTimeChangeCommunication;
import cn.demomaster.huan.doctorbaselibrary.model.api.ClosedOrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderDoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.BaseOrderListActivity;

import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.ACCEPTED;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.MODIFY_TIME;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.NONE;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.ONLYCANCEL;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.REFUSE;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.REFUSE_MODIFY;
import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.WAITING_PAY;

//import cn.demomaster.huan.doctorbaselibrary.view.activity.order.PayActivity;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class ClosedOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tv_category;
        TextView tv_requestNumber;
        TextView tv_startTime;
        Context context;

        public VH(View v) {
            super(v);
            context = v.getContext();
            tv_category = v.findViewById(R.id.tv_category);
            tv_requestNumber = v.findViewById(R.id.tv_requestNumber);
            tv_startTime = v.findViewById(R.id.tv_startTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position != 0 ? 1 : 0;
    }

    private static Context context;
    private int type;

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    private List<ClosedOrderModelApi.OrderInfo> mDatas;

    public ClosedOrderAdapter(Context context, List<ClosedOrderModelApi.OrderInfo> data, int type) {
        this.mDatas = data;
        this.context = context;
        this.type = type;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VH) {
            final VH vh = (VH) holder;
            String title = mDatas.get(position).getRequestName().replace("_", "   ");
            // 设置标题上的文本信息
            vh.tv_category.setText(title);
            vh.tv_startTime.setText(mDatas.get(position).getStartAt());
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (OnItemClickListener != null) {
                        OnItemClickListener.onItemClick(v,vh.getAdapterPosition(),mDatas.get(vh.getAdapterPosition()));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = null;
        RecyclerView.ViewHolder vh = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_closed, parent, false);
        vh = new VH(v);
        return vh;
    }

    private OnItemClickListener OnItemClickListener;

    public void setOnItemClickListener(ClosedOrderAdapter.OnItemClickListener onItemClickListener) {
        OnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, ClosedOrderModelApi.OrderInfo orderInfo);
    }
}
