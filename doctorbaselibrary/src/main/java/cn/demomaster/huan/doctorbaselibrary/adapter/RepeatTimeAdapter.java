package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.CalendarTimeModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.EvaluateModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.RepeatTimeModelApi;
import cn.demomaster.huan.quickdeveloplibrary.widget.RatingBar;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class RepeatTimeAdapter extends RecyclerView.Adapter<RepeatTimeAdapter.VH> {

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tv_startAt;
        TextView tv_endAt;
        TextView tv_repeatValue;
        TextView tv_expireDate;
        TextView tv_takeEffectDay;
        RatingBar ratingBarProfessional;
        RatingBar ratingBarService;
        Button btn_edit, btn_delete;

        public VH(View v) {
            super(v);
            tv_startAt = (TextView) v.findViewById(R.id.tv_startAt);
            tv_endAt = (TextView) v.findViewById(R.id.tv_endAt);
            tv_repeatValue = (TextView) v.findViewById(R.id.tv_repeatValue);
            tv_expireDate = (TextView) v.findViewById(R.id.tv_expireDate);
            tv_takeEffectDay = (TextView) v.findViewById(R.id.tv_takeEffectDay);
            btn_edit = v.findViewById(R.id.btn_edit);
            btn_delete = v.findViewById(R.id.btn_delete);
        }
    }

    private List<RepeatTimeModelApi> mDatas;
    public  Context context;

    public RepeatTimeAdapter(Context context, List<RepeatTimeModelApi> data,OnItemClickedListener onItemClickedListener) {
        this.mDatas = data;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.tv_startAt.setText(mDatas.get(position).getStartAt());
        holder.tv_endAt.setText(mDatas.get(position).getEndAt());

        if(!TextUtils.isEmpty(mDatas.get(position).getTakeEffectDay())) {
            String str = mDatas.get(position).getTakeEffectDay();
            if(str.length()<8)return;
            String dateStr = str.substring(0,4)+"-" + str.substring(4,6)+"-"+str.substring(6,8);
            holder.tv_takeEffectDay.setText(dateStr);
        }
        if(!TextUtils.isEmpty(mDatas.get(position).getExpireDate())) {
            String str = mDatas.get(position).getExpireDate();
            if(str.equals("99999999")) {
                holder.tv_expireDate.setText("");
            }else {
                if(str.length()<8)return;
                String dateStr = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8);
                holder.tv_expireDate.setText( dateStr);
            }
        }
        holder.tv_repeatValue.setText(mDatas.get(position).getRepeatType().equals("week") ? getWeekStr(mDatas.get(position).getRepeatValue()) : "每日");
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickedListener!=null){
                    onItemClickedListener.onEdit(holder.getAdapterPosition());
                }
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickedListener!=null){
                    onItemClickedListener.onDelete(holder.getAdapterPosition());
                }
            }
        });
    }
    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public static interface OnItemClickedListener {
        void onDelete(int position);

        void onEdit(int position);

    }

    private String getWeekStr(String repeatValue) {
        String strName = "";
        if (repeatValue != null && repeatValue.contains(",")) {
            String[] strs = repeatValue.split(",");
            for (int i = 0; i < strs.length; i++) {
                switch (strs[i]) {
                    case "1":
                        strName += " 周一";
                        break;
                    case "2":
                        strName += " 周二";
                        break;
                    case "3":
                        strName += " 周三";
                        break;
                    case "4":
                        strName += " 周四";
                        break;
                    case "5":
                        strName += " 周五";
                        break;
                    case "6":
                        strName += " 周六";
                        break;
                    case "7":
                        strName += " 周日";
                        break;
                }
            }
        }
        return strName;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repeat_time, parent, false);
        return new VH(v);
    }
}
