package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModel2Api;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;

import java.util.List;

//import cn.demomaster.huan.doctorbaselibrary.view.activity.order.PayActivity;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class OrderAdapter2 extends RecyclerView.Adapter<OrderAdapter2.VH> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderName;
        TextView tvOrderStartTime;
        TextView tv_order_state;
        Context context;

        public VH(View v) {
            super(v);
            context = v.getContext();
            tvOrderName = v.findViewById(R.id.tv_order_name);
            tvOrderStartTime = v.findViewById(R.id.tv_order_start_time);
            tv_order_state = v.findViewById(R.id.tv_order_state);
        }
    }

    private List<OrderModel2Api> mDatas;
    private static Context context;
    private int type;

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public OrderAdapter2(Context context, List<OrderModel2Api> data, int type) {
        this.mDatas = data;
        this.context = context;
        this.type = type;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.tvOrderName.setText(mDatas.get(position).getTrxName().replace("_","  "));
        holder.tvOrderStartTime.setText(mDatas.get(position).getAppointTime());
        holder.tv_order_state.setText(mDatas.get(position).getStatus().equalsIgnoreCase("closed")?"已经结束":"进行中");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order2, parent, false);
        return new VH(v);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int adapterPosition);
    }



}
