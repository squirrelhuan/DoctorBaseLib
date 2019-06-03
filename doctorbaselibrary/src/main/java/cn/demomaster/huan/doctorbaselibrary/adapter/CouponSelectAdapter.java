package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CouponModelApi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class CouponSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_coupon_title, tv_coupon_amount, tv_coupon_unit, tv_coupon_time, tv_coupon_requirements;
        public CheckBox cb_set_default;

        public VH(View v) {
            super(v);
            tv_coupon_title = v.findViewById(R.id.tv_coupon_title);
            tv_coupon_amount = v.findViewById(R.id.tv_coupon_amount);
            tv_coupon_unit = v.findViewById(R.id.tv_coupon_unit);
            tv_coupon_time = v.findViewById(R.id.tv_coupon_time);
            tv_coupon_requirements = v.findViewById(R.id.tv_coupon_requirements);
            cb_set_default = v.findViewById(R.id.cb_set_default);
        }
    }

    private List<CouponModelApi> mDatas;
    private Map<String, CouponModelApi> checkedCouponList;
    private OnItemClicked onItemClicked;

    public void setCheckedCouponList(Map<String, CouponModelApi> checkedCouponList) {
        this.checkedCouponList = checkedCouponList;
        notifyDataSetChanged();
    }

    public CouponSelectAdapter(List<CouponModelApi> data, Map<String, CouponModelApi> checkedCouponList, OnItemClicked onItemClicked) {
        this.mDatas = data;
        this.checkedCouponList = checkedCouponList;
        this.onItemClicked = onItemClicked;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        VH holder1 = (VH) holder;
        holder1.tv_coupon_title.setText(mDatas.get(position).getType().equals("DEDUCTION") ? "抵用券" : "折扣券");//DEDUCTION","抵用券/ DISCOUNT折扣券
        holder1.tv_coupon_amount.setText(mDatas.get(position).getAmount() + "");
        holder1.tv_coupon_unit.setText(mDatas.get(position).getType().equals("DEDUCTION") ? "元" : "折");
        holder1.tv_coupon_time.setText(mDatas.get(position).getExpireAt() + "");
        ((VH) holder).cb_set_default.setOnCheckedChangeListener(null);
        ((VH) holder).cb_set_default.setChecked(checkedCouponList.containsKey(mDatas.get(position).getId()));
        ((VH) holder).cb_set_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onItemClicked.onCheckChanged(holder.getAdapterPosition());
            }
        });
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onItemClicked(holder.getAdapterPosition());
            }
        });
    }



    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_selector, parent, false);
        return new VH(v);
    }

    public static interface OnItemClicked {
        void onItemClicked(int position);

        void onCheckChanged(int index);
    }

}
