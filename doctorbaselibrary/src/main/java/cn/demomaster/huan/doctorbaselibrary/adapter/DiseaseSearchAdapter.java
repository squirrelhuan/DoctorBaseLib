package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.DiseaseTypeModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.OperationTypeModel;

import java.util.List;

/**
 * 确诊类型/手术类型适配器
 *
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class DiseaseSearchAdapter extends RecyclerView.Adapter<DiseaseSearchAdapter.VH> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvDoctorName;

        public VH(View v) {
            super(v);
            tvDoctorName = (TextView) v.findViewById(R.id.tv_doctor_name);
        }
    }

    private List<DiseaseTypeModel> mDatas;
    private List<OperationTypeModel> mDatas2;
    private int dataType = 0;
    private static Context context;

    public DiseaseSearchAdapter(Context context, int type, List<DiseaseTypeModel> data, List<OperationTypeModel> data2) {
        this.mDatas = data;
        this.mDatas2 = data2;
        this.dataType = type;
        this.context = context;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        if (this.dataType == 0) {
            holder.tvDoctorName.setText(mDatas.get(position).getDisease());
            holder.tvDoctorName.setTag(mDatas.get(position).getId());
            holder.tvDoctorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onClick(holder.getAdapterPosition(),dataType,mDatas.get(holder.getAdapterPosition()).getDisease()+"");
                    }
                }
            });
        } else {
            holder.tvDoctorName.setText(mDatas2.get(position).getOperationName());
            holder.tvDoctorName.setTag(mDatas2.get(position).getId());
            holder.tvDoctorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(onItemClickListener!=null){
                       onItemClickListener.onClick(holder.getAdapterPosition(),dataType,mDatas2.get(holder.getAdapterPosition()).getOperationName()+"");
                   }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return  this.dataType == 0?mDatas.size():mDatas2.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_simple, parent, false);
        return new VH(v);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int position,int dataType,String resultData);
    }
}
