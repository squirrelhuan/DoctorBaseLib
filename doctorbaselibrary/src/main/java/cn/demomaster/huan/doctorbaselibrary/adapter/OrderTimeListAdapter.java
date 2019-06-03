package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.AppointmentTimeChangeCommunication;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class OrderTimeListAdapter extends RecyclerView.Adapter<OrderTimeListAdapter.VH> {


    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_message;
        View view_line_01, view_line_02;

        public VH(View v) {
            super(v);
            tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_message = (TextView) v.findViewById(R.id.tv_message);

            view_line_01 = v.findViewById(R.id.view_line_01);
            view_line_02 = v.findViewById(R.id.view_line_02);
        }

        public void bind(AppointmentTimeChangeCommunication appointmentTimeChangeCommunication, int type, int showModifyTime, int index) {
            tv_message.setTextColor(context.getResources().getColor(R.color.main_color_gray_46));
            view_line_01.setVisibility(View.VISIBLE);
            view_line_02.setVisibility(View.VISIBLE);


            Drawable drawable =context.getResources().getDrawable(R.mipmap.ic_timer_point_01);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_timer_point_01);
            int height = bitmap.getHeight();
            int width= bitmap.getWidth();
            drawable.setBounds(0, 0, width, height);// 一定要设置setBounds();  39/30是长宽比
            bitmap.recycle();
            tv_title.setCompoundDrawables(drawable,null,null,null);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tv_title.getLayoutParams();
            layoutParams.setMargins(DisplayUtil.dip2px(context,-13),layoutParams.topMargin,layoutParams.rightMargin,layoutParams.rightMargin);
            tv_title.setLayoutParams(layoutParams);
            tv_message.setVisibility(View.VISIBLE);
            //tv_title.setCompoundDrawablePadding(DisplayUtil.dip2px(context,5)) ;//int padding 代码设置文字和图片间距
            //tv_title.invalidate();
            //0没有线条，1底部线条，2上下线条，3顶部线条
            switch (type) {
                case 0:
                    tv_message.setTextColor(context.getResources().getColor(R.color.main_color));
                    view_line_01.setVisibility(View.INVISIBLE);
                    view_line_02.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    view_line_01.setVisibility(View.INVISIBLE);
                    break;
                case 2:

                    break;
                case 3:
                    drawable =context.getResources().getDrawable(R.mipmap.ic_timer_point_02);
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_timer_point_02);
                    height = bitmap.getHeight();
                    width= bitmap.getWidth();
                    drawable.setBounds(0, 0, width, height);// 一定要设置setBounds();  39/30是长宽比
                    bitmap.recycle();
                    tv_title.setCompoundDrawables(drawable,null,null,null);
                    layoutParams.setMargins(DisplayUtil.dip2px(context,-14),layoutParams.topMargin,layoutParams.rightMargin,layoutParams.rightMargin);
                    tv_title.setLayoutParams(layoutParams);
                    //tv_title.invalidate();
                    //tv_title.setCompoundDrawablePadding(DisplayUtil.dip2px(context,5)) ;//int padding 代码设置文字和图片间距
                    tv_message.setTextColor(context.getResources().getColor(R.color.main_color));
                    view_line_02.setVisibility(View.INVISIBLE);
                    break;
            }
            if (appointmentTimeChangeCommunication == null) {
                //tv_title.setVisibility(View.GONE);
                tv_message.setVisibility(View.GONE);
                tv_title.setTextColor(context.getResources().getColor(R.color.main_color));
                tv_title.setText("等待对方回复...");
            } else {
                //tv_title.setVisibility(View.VISIBLE);
                tv_title.setText(appointmentTimeChangeCommunication.getCreateAt());
                boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
                String str = "";
                if (appointmentTimeChangeCommunication.getContent().equals("refuse")) {//拒绝
                    str = (appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "已拒绝" +
                            (!appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "提出的时间，等待" +
                            (!appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "回复……";
                } else {
                    if (index == 0) {//修改时间
                        str = (appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "询问能否调整预约时间为：" + appointmentTimeChangeCommunication.getContent();
                    } else {//拒绝并提出新时间
                        str = (appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "拒绝" +
                                (!appointmentTimeChangeCommunication.getSenderRole().equals("D") ? (isPatient ? "专家" : "您") : (isPatient ? "您" : "病人")) + "提出的时间，并提出了一个新的时间：" + appointmentTimeChangeCommunication.getContent();
                    }
                }

                tv_message.setText(str);
            }
        }
    }

    private List<AppointmentTimeChangeCommunication> mDatas;
    private static Context context;
    private int type;
    private int showModifyTime;

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public OrderTimeListAdapter(Context context, List<AppointmentTimeChangeCommunication> data, int type, int showModifyTime) {
        this.mDatas = data;
        this.context = context;
        this.type = type;
        this.showModifyTime = showModifyTime;
    }

    int getType(int position) {
        //0没有线条，1底部线条，2上下线条，3顶部线条
        if (getItemCount() == 1) {
            return 0;
        } else if (getItemCount() > 1) {
            if (position == 0) {
                return 1;
            }
            if (position < getItemCount() - 1) {
                return 2;
            }
            if (position == getItemCount() - 1) {
                return 3;
            }
        }
        return 0;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {

        if (position > mDatas.size() - 1) {
            holder.bind(null, getType(position), showModifyTime, position);
        } else {
            holder.bind(mDatas.get(position), getType(position), showModifyTime, position);
        }
    }

    @Override
    public int getItemCount() {
        //判断最后一个状态
        if (mDatas != null && mDatas.size() > 0) {
            int index = mDatas.size() - 1;
            AppointmentTimeChangeCommunication appointmentTimeChangeCommunication = mDatas.get(index);
            boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
            String t = isPatient ? "P" : "D";
            if (appointmentTimeChangeCommunication.getSenderRole().equals(t)) {//
                return mDatas.size() + 1;//多家一行字
            } else {
                return mDatas.size();
            }
        } else {
            return mDatas.size();
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_timer, parent, false);
        return new VH(v);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onCanel(String requestId);

        void onMotifyTime(String requestId);

        void onChart();
    }


}
