package cn.demomaster.huan.doctorbaselibrary.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.List;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_address,tv_address_edit,tv_address_delete;
        public RadioButton rb_set_default;

        public VH(View v) {
            super(v);
            tv_address = (TextView) v.findViewById(R.id.tv_address);
            tv_address_edit = (TextView) v.findViewById(R.id.tv_address_edit);
            tv_address_delete = (TextView) v.findViewById(R.id.tv_address_delete);
            rb_set_default = v.findViewById(R.id.rb_set_default);
            rb_set_default.setClickable(false);
        }
    }

    public static class VH2 extends RecyclerView.ViewHolder {
        public final TextView tv_add_patient;

        public VH2(View v) {
            super(v);
            tv_add_patient = (TextView) v.findViewById(R.id.tv_add_patient);
        }
    }

    private List<AddRessModel> mDatas;
    private OnItemClicked onItemClicked;

    public AddressAdapter(List<AddRessModel> data, OnItemClicked onItemClicked) {
        this.mDatas = data;
        this.onItemClicked = onItemClicked;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == mDatas.size()) {
            VH2 holder1 = (VH2) holder;
            holder1.tv_add_patient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onSubmitButtonClicked();
                }
            });
        } else {
            VH holder1 = (VH) holder;
            holder1.tv_address.setText(mDatas.get(position).getAddressName());
            if (mDatas.get(position).getIsDefault().equals("1")) {
                holder1.rb_set_default.setChecked(true);
            } else {
                holder1.rb_set_default.setChecked(false);
            }
            holder1.itemView.setTag(position);
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //item 点击事件
                    onItemClicked.onItemClicked(v, (int) v.getTag(), mDatas.get((int) v.getTag()));
                }
            });
            holder1.tv_address_delete.setTag(position);
            holder1.tv_address_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onDeleteButtonClicked((int) v.getTag());
                }
            });
            holder1.tv_address_edit.setTag(mDatas.get(position));
            holder1.tv_address_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onEditButtonClicked((AddRessModel) v.getTag());
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
        if (position == mDatas.size() ) {
            return -1;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_add_btn, parent, false);
            return new VH2(v);
        } else {
            //LayoutInflater.from指定写法
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new VH(v);
        }
    }

    public static interface OnItemClicked {
        void onItemClicked(View view, int position, AddRessModel addRessModel);
        void onDeleteButtonClicked(int index);
        void onEditButtonClicked(AddRessModel addRessModel);
        void onSubmitButtonClicked();
    }

}
