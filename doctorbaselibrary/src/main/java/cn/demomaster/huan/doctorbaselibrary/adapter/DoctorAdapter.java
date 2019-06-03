package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.DoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.doctor.DoctorDetailActivity;

import java.util.List;
import java.util.Map;

import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.VH> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvDoctorName;
        TextView tvDoctorCategory;
        TextView tvDoctorLevel;
        TextView tvCharacteristic;
        RatingBar ratingBarProfessional;
        RatingBar ratingBarService;
        ImageView ivDoctorHead;

        public VH(View v) {
            super(v);
            tvDoctorName = (TextView) v.findViewById(R.id.tv_doctor_name);
            tvDoctorCategory = (TextView) v.findViewById(R.id.tv_doctor_category);
            tvDoctorLevel = (TextView) v.findViewById(R.id.tv_doctor_level);
            tvCharacteristic = (TextView) v.findViewById(R.id.tv_characteristic);
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
    }

    private List<DoctorModelApi> mDatas;
    private static Context context;
    private Map<String,String> categoryNameMap;

    public DoctorAdapter(Context context, List<DoctorModelApi> data, Map<String,String> categoryNameMap) {
        this.mDatas = data;
        this.context = context;
        this.categoryNameMap = categoryNameMap;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tvDoctorName.setText(mDatas.get(position).getDoctorName());
        String category = mDatas.get(position).getDomain();

        if(categoryNameMap.containsKey(category)){
            category = categoryNameMap.get(category);
        }
        holder.tvDoctorCategory.setText(category);
        final String[] titles = { "主任医师", "副主任医师", "主治医师"};
        String title ="";
         switch (mDatas.get(position).getTitle()){
            case "1":
                title = titles[0];
                break;
            case "2":
                title = titles[1];
                break;
            case "3":
                title = titles[2];
                break;
        }
        holder.tvDoctorLevel.setText(title);
        holder.ratingBarProfessional.setActivateCount(mDatas.get(position).getOverallMerit());
        holder.ratingBarService.setActivateCount(mDatas.get(position).getServiceMerit());
        String s = String.format("临床特色：%s",mDatas.get(position).getSpecialDomainDesc());
        SpannableString spannableString = new SpannableString(s);
        ForegroundColorSpan  span = new ForegroundColorSpan (context.getResources().getColor(R.color.main_color));
        spannableString.setSpan(span, 5, s.length(), SPAN_INCLUSIVE_INCLUSIVE);
        holder.tvCharacteristic.setText("");
        holder.tvCharacteristic.append(spannableString);
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
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new VH(v);
    }
}
