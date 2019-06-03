package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorSimpleModel;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DoctorDetailActivity;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class DoctorSearchAdapter extends RecyclerView.Adapter<DoctorSearchAdapter.VH> {



    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvDoctorName;/*
        TextView tvDoctorCategory;
        TextView tvDoctorLevel;
        TextView tvCharacteristic;
        RatingBar ratingBarProfessional;
        RatingBar ratingBarService;
        ImageView ivDoctorHead;*/

        public VH(View v) {
            super(v);
            tvDoctorName = (TextView) v.findViewById(R.id.tv_doctor_name);/*
            tvDoctorCategory = (TextView) v.findViewById(R.id.tv_doctor_category);
            tvDoctorLevel = (TextView) v.findViewById(R.id.tv_doctor_level);
            tvCharacteristic = (TextView) v.findViewById(R.id.tv_characteristic);
            ratingBarProfessional = (RatingBar) v.findViewById(R.id.ratingBar_Professional);
            ratingBarService = (RatingBar) v.findViewById(R.id.ratingBar_Service);
            ratingBarProfessional.setColor(context.getResources().getColor(R.color.main_color),context.getResources().getColor(R.color.white));
            ratingBarService.setColor(context.getResources().getColor(R.color.main_color),context.getResources().getColor(R.color.white));
            ivDoctorHead = (ImageView) v.findViewById(R.id.iv_doctor_head);*/
        }
    }

    private List<DoctorSimpleModel> mDatas;
    private static Context context;

    public DoctorSearchAdapter(Context context, List<DoctorSimpleModel> data) {
        this.mDatas = data;
        this.context = context;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tvDoctorName.setText(mDatas.get(position).getName());
        holder.tvDoctorName.setTag(mDatas.get(position).getUserId());
        holder.tvDoctorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Bundle bundle = new Bundle();
                bundle.putString("doctorId",v.getTag().toString());
                ((BaseActivity)context).startActivity(DoctorDetailActivity.class,bundle);
            }
        });
        /*String title = "";
        switch (mDatas.get(position).getTitle()){
            case "1":
                break;
            case "2":
                break;
            case "3":
                title = "全科";
                break;
        }
        holder.tvDoctorCategory.setText(title);
        //holder.tvDoctorLevel.setText(mDatas.get(position).getDoctorName());
        holder.ratingBarProfessional.setActivateCount(mDatas.get(position).getOverallMerit());
        holder.ratingBarService.setActivateCount(mDatas.get(position).getServiceMerit());
        holder.tvCharacteristic.setText(mDatas.get(position).getSpecialDomainDesc());
        Glide.with(context)
                .load(mDatas.get(position).getPhotoUrl())
                .into(holder.ivDoctorHead);
        holder.itemView.setTag(mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
                //PopToastUtil.ShowToast((Activity) context,"正在开发...");
                DoctorModelApi doctorModelApi = (DoctorModelApi)v.getTag();
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctorInfo",doctorModelApi);
                ((BaseActivity)context).startActivity(DoctorDetailActivity.class,bundle);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_simple, parent, false);
        return new VH(v);
    }
}
