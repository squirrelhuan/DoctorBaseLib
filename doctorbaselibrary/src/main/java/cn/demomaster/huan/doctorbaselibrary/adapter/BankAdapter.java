package cn.demomaster.huan.doctorbaselibrary.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.BankModel;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class BankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_bankAccount, tv_bankName, tv_cardType;
        public ImageView ic_bank;

        public VH(View v) {
            super(v);
            tv_bankAccount = (TextView) v.findViewById(R.id.tv_bankAccount);
            tv_bankName = (TextView) v.findViewById(R.id.tv_bankName);
            tv_cardType = (TextView) v.findViewById(R.id.tv_cardType);
            ic_bank = v.findViewById(R.id.ic_bank);
        }
    }

    public static class VH2 extends RecyclerView.ViewHolder {
        public final TextView tv_add_bank;

        public VH2(View v) {
            super(v);
            tv_add_bank = (TextView) v.findViewById(R.id.tv_add_bank);
        }
    }

    private List<BankModel> mDatas;
    private OnItemClicked onItemClicked;
    private Context context;

    public BankAdapter(Context context, List<BankModel> data, OnItemClicked onItemClicked) {
        this.mDatas = data;
        this.onItemClicked = onItemClicked;
        this.context = context;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == mDatas.size()) {
            VH2 holder1 = (VH2) holder;
            holder1.tv_add_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onSubmitButtonClicked();
                }
            });
        } else {
            VH holder1 = (VH) holder;
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(false);//不做内存缓存
            if (TextUtils.isEmpty(mDatas.get(position).getUrl()) || mDatas.get(position).getUrl().equals("null")) {
                //Glide.with(context).load(mDatas.get(position).getUrl()).apply(mRequestOptions).into(holder1.ic_bank);
            }else {
                Glide.with(context).load(mDatas.get(position).getUrl()).apply(mRequestOptions).into(holder1.ic_bank);
            }
            holder1.tv_bankAccount.setText(mDatas.get(position).getBankAccount().replace("*",""));
            holder1.tv_bankName.setText(mDatas.get(position).getBankName());
            holder1.tv_cardType.setText(mDatas.get(position).getCardType());
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //item 点击事件
                    onItemClicked.onItemClicked(v, holder.getAdapterPosition(), mDatas.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + 1;
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
        if (viewType == -1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_add_btn, parent, false);
            return new VH2(v);
        } else {
            //LayoutInflater.from指定写法
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
            return new VH(v);
        }
    }

    public static interface OnItemClicked {
        void onItemClicked(View view, int position, BankModel bankModel);

        void onDeleteButtonClicked(int index);

        void onEditButtonClicked(AddRessModel addRessModel);

        void onSubmitButtonClicked();
    }

}
