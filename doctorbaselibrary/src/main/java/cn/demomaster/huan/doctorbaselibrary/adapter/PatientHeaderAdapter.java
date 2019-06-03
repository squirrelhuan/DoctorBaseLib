package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.UserModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity_NoActionBar;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient.AddPatientActivity;
import cn.demomaster.huan.quickdeveloplibrary.util.AnimationUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class PatientHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //特殊点：最后一个要用加号

    private List<UserModelApi> lists = null;
    private Context context;
    private int defIndex;

    public PatientHeaderAdapter(Context context, List<UserModelApi> lists, int defIndex) {
        this.context = context;
        this.lists = lists;
        this.defIndex = defIndex;
    }

    @Override
    public int getItemViewType(int position) {
        //判断item类别，是图还是显示页数（图片有URL）
        return position >= lists.size() ? 0 : 1;
    }

    //创建View,被LayoutManager所用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == 0) {//添加按钮
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycleview_patient_add, parent, false);
            holder = new LastViewHolder(view);
        } else {//病人
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycleview_patient, parent, false);
            holder = new PatientViewHolder(view);
        }
        return holder;
    }

    public static interface OnPatientChangedListener {
        void onPatientChanged(UserModelApi patient);
    }

    private OnPatientChangedListener onPatientChangedListener;

    public void setOnPatientChangedListener(OnPatientChangedListener onPatientChangedListener) {
        this.onPatientChangedListener = onPatientChangedListener;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position >= lists.size()) {//添加
            ((LastViewHolder) holder).iv_add_patient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"添加",Toast.LENGTH_LONG).show();
                    ((BaseActivity_NoActionBar) context).startActivity(AddPatientActivity.class);
                }
            });
            AnimationUtil.addScaleAnimition(((LastViewHolder) holder).iv_add_patient, null);
        } else {//病人
            if(lists.get(position).getPhotoUrl()==null||lists.get(position).getPhotoUrl().equals("null")){
                ((PatientViewHolder) holder).tv_patient_name.setVisibility(View.GONE);
                ((PatientViewHolder) holder).tv_patient_name2.setVisibility(View.VISIBLE);
                ((PatientViewHolder) holder).tv_patient_name2.setText(lists.get(position).getUserName());
            }else {
                ((PatientViewHolder) holder).tv_patient_name2.setVisibility(View.GONE);
                ((PatientViewHolder) holder).tv_patient_name.setVisibility(View.VISIBLE);
                ((PatientViewHolder) holder).tv_patient_name.setText(lists.get(position).getUserName());
                RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                        .skipMemoryCache(false);//不做内存缓存
                Glide.with(context).load(lists.get(position).getPhotoUrl()).apply(mRequestOptions).into(((PatientViewHolder) holder).iv_patient_head);
                ((PatientViewHolder) holder).iv_patient_head.setBackgroundResource(R.drawable.image_circle_head_bg_01);
            }
            if(position==defIndex){
                ((PatientViewHolder) holder).iv_patient_head.setPadding(DisplayUtil.dp2px(context,3),DisplayUtil.dp2px(context,3),DisplayUtil.dp2px(context,3),DisplayUtil.dp2px(context,3));
                ((PatientViewHolder) holder).iv_patient_head.setBackgroundResource(R.drawable.image_circle_head_bg_02);
                if(lists.get(position).getHeadUrl()==null){
                    ((PatientViewHolder) holder).tv_patient_name2.setTextColor(context.getResources().getColor(R.color.white));
                }else {
                    ((PatientViewHolder) holder).tv_patient_name.setTextColor(context.getResources().getColor(R.color.main_color));
                }
            }else {
                ((PatientViewHolder) holder).iv_patient_head.setPadding(0,0,0,0);
                ((PatientViewHolder) holder).iv_patient_head.setBackgroundResource(R.drawable.image_circle_head_bg_01);
                if(lists.get(position).getHeadUrl()==null){
                    ((PatientViewHolder) holder).tv_patient_name2.setTextColor(context.getResources().getColor(R.color.main_color));
                }else {
                    ((PatientViewHolder) holder).tv_patient_name.setTextColor(Color.parseColor("#C3C3C5"));
                }
            }
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int)v.getTag();
                    if (onPatientChangedListener != null) {
                        onPatientChangedListener.onPatientChanged(lists.get(p));
                    }
                    defIndex = p;
                    notifyDataSetChanged();
                }
            });
            AnimationUtil.addScaleAnimition(holder.itemView, null);
        }

    }

    @Override
    public int getItemCount() {
        return lists.size() + 1;
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_patient_name,tv_patient_name2;
        public ImageView iv_patient_head;
        public PatientViewHolder(View itemView) {
            super(itemView);
            tv_patient_name = (TextView) itemView.findViewById(R.id.tv_patient_name);
            tv_patient_name2 = (TextView) itemView.findViewById(R.id.tv_patient_name2);
            iv_patient_head = (ImageView) itemView.findViewById(R.id.iv_patient_head);
        }
    }

    //自定义+
    public static class LastViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iv_add_patient;


        public LastViewHolder(View itemView) {
            super(itemView);
            iv_add_patient = (ImageView) itemView.findViewById(R.id.iv_add_patient);
        }
    }

}

