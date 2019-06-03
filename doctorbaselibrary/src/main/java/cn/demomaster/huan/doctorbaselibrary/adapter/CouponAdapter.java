package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CouponModelApi;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_coupon_title,tv_coupon_amount,tv_coupon_unit,tv_coupon_time,tv_coupon_requirements;

        public VH(View v) {
            super(v);
            tv_coupon_title = v.findViewById(R.id.tv_coupon_title);
            tv_coupon_amount = v.findViewById(R.id.tv_coupon_amount);
            tv_coupon_unit = v.findViewById(R.id.tv_coupon_unit);
            tv_coupon_time = v.findViewById(R.id.tv_coupon_time);
            tv_coupon_requirements = v.findViewById(R.id.tv_coupon_requirements);
        }
    }

    private List<CouponModelApi> mDatas;
    private OnItemClicked onItemClicked;

    public CouponAdapter(List<CouponModelApi> data, OnItemClicked onItemClicked) {
        this.mDatas = data;
        this.onItemClicked = onItemClicked;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        VH holder1 = (VH) holder;
        holder1.tv_coupon_title.setText(mDatas.get(position).getType().equals("DEDUCTION")?"抵用券":"折扣券");//DEDUCTION","抵用券/ DISCOUNT折扣券
        holder1.tv_coupon_amount.setText(mDatas.get(position).getAmount()+"");
        holder1.tv_coupon_unit.setText(mDatas.get(position).getType().equals("DEDUCTION")?"元":"折");
        holder1.tv_coupon_time.setText(mDatas.get(position).getExpireAt()+"");
        //holder1.tv_coupon_requirements.setText(mDatas.get(position).getType()+"");
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked.onSubmitButtonClicked();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas==null?0:mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new VH(v);
    }

    public static interface OnItemClicked {
        void onItemClicked(View view, int position, AddRessModel addRessModel);
        void onDeleteButtonClicked(int index);
        void onEditButtonClicked(AddRessModel addRessModel);
        void onSubmitButtonClicked();
    }

}
