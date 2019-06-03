package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.CalendarTimeItemModel;
import cn.demomaster.huan.doctorbaselibrary.view.widget.textview.DateTimeCellView;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class DateTimePointAdapter extends RecyclerView.Adapter<DateTimePointAdapter.ViewHolder> {


    private List<CalendarTimeItemModel> lists;
    private Context context;

    public DateTimePointAdapter(Context context, List<CalendarTimeItemModel> lists) {
        this.context = context;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_datetime, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CalendarTimeItemModel dateTimeModel = lists.get(position);
        holder.tv_title.setText(lists.get(position).getTitle());

        if(lists.size()-position<=7){
            holder.view_line.setVisibility(View.GONE);
        }else {
            holder.view_line.setVisibility(View.VISIBLE);
        }
        holder.dateTimeCellView.setShowType(0);
        if (lists.get(position).isAvailable()) {
            //holder.dateTimeCellView.setData(dateTimeModel.getVacantTimeArrangement());
            //holder.dateTimeCellView.setShowType(1);
            if (lists.get(position).isSpecial()) {
                holder.tv_title.setTextColor(context.getResources().getColor(R.color.main_color));
            } else {
                if (lists.get(position).isFree()) {
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    holder.tv_title.setTextColor(context.getResources().getColor(R.color.main_color_gray_46_a65));
                }
            }
            holder.dateTimeCellView.setShowType(1);
        }else {
            holder.dateTimeCellView.setData(null);
            holder.tv_title.setTextColor(context.getResources().getColor(R.color.main_color_gray_46_a65));
        }
        /*int id = getResId(dateTimeModel.getIconName(), R.mipmap.class);
        try {
            BitmapFactory.decodeResource(context.getResources(), id);
            holder.iv_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if(!lists.get(position).isAvailable()){
                    PopToastUtil.ShowToast((Activity) context,"该日期不可选！");
                    return;
                }
                if(!lists.get(position).isFree()){
                    PopToastUtil.ShowToast((Activity) context,"医生此日繁忙！");
                    return;
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v,position );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClick(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static interface OnItemClickListener {
        void onClick(View v, int position);
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private DateTimeCellView dateTimeCellView;
        private View view_line;

        public ViewHolder(View itemView) {
            super(itemView);
            view_line =itemView.findViewById(R.id.view_line);
            tv_title = itemView.findViewById(R.id.tv_title);
            dateTimeCellView = itemView.findViewById(R.id.dtc_time);
        }
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

