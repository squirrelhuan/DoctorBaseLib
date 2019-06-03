package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.EvaluateModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DoctorDetailActivity;
import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Map;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class EvaluateAdapter extends RecyclerView.Adapter<EvaluateAdapter.VH> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvDate;
        TextView tvDesc;
        RatingBar ratingBarProfessional;
        RatingBar ratingBarService;
        ImageView ivDoctorHead;

        public VH(View v) {
            super(v);
            tvUserName = (TextView) v.findViewById(R.id.tv_user_name);
            tvDate = (TextView) v.findViewById(R.id.tv_date);
            tvDesc = (TextView) v.findViewById(R.id.tv_desc);
            ratingBarProfessional = (RatingBar) v.findViewById(R.id.ratingBar_Professional);
            ratingBarService = (RatingBar) v.findViewById(R.id.ratingBar_Service);
            //ratingBarProfessional.setColor(context.getResources().getColor(R.color.main_color),context.getResources().getColor(R.color.white));
            ratingBarProfessional.setActivateCount(5);
            ratingBarProfessional.setBackResourceId(R.mipmap.ic_seekbar_star_normal);
            ratingBarProfessional.setFrontResourceId(R.mipmap.ic_seekbar_star_selected);
            ratingBarProfessional.setUseCustomDrable(true);
            //ratingBarService.setColor(context.getResources().getColor(R.color.main_color),context.getResources().getColor(R.color.white));
            ratingBarService.setActivateCount(5);
            ratingBarService.setBackResourceId(R.mipmap.ic_seekbar_star_normal);
            ratingBarService.setFrontResourceId(R.mipmap.ic_seekbar_star_selected);
            ratingBarService.setUseCustomDrable(true);
            ivDoctorHead = v.findViewById(R.id.iv_doctor_head);
        }

        void bind(){

        }
    }

    private List<EvaluateModelApi.Evaluation> mDatas;
    private static Context context;
    private Map<String,String> categoryNameMap;

    public EvaluateAdapter(Context context, List<EvaluateModelApi.Evaluation> data) {
        this.mDatas = data;
        this.context = context;
        this.categoryNameMap = categoryNameMap;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tvUserName.setText(mDatas.get(position).getPatientMobileNumber());
        holder.tvDate.setText(mDatas.get(position).getDate());
        holder.tvDesc.setText(mDatas.get(position).getMoreDesc());
        holder.ratingBarProfessional.setActivateCount(Integer.valueOf(mDatas.get(position).getProfessionMerit()));
        holder.ratingBarService.setActivateCount(Integer.valueOf(mDatas.get(position).getServiceMerit()));

     /*
        holder.tvDoctorLevel.setText(title);
        holder.ratingBarProfessional.setActivateCount(mDatas.get(position).getOverallMerit());
        holder.ratingBarService.setActivateCount(mDatas.get(position).getServiceMerit());
        holder.tvCharacteristic.setText(mDatas.get(position).getSpecialDomainDesc());
        if(!TextUtils.isEmpty(mDatas.get(position).getPhotoUrl())&&!mDatas.get(position).getPhotoUrl().equals("null")) {
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(false);//不做内存缓存
            Glide.with(context).load(mDatas.get(position).getPhotoUrl()).apply(mRequestOptions).into(holder.ivDoctorHead);
        }else {
            Glide.with(context).load(R.mipmap.ic_header_doctor).into(holder.ivDoctorHead);
        }

        holder.itemView.setTag(mDatas.get(position).getDoctorId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
                //PopToastUtil.ShowToast((Activity) context,"正在开发...");
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctorId",v.getTag().toString());
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evaluate, parent, false);
        return new VH(v);
    }
}
