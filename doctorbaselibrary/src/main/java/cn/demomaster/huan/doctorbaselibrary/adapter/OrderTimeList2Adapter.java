package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.AppointmentTimeChangeModel;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class OrderTimeList2Adapter extends RecyclerView.Adapter<OrderTimeList2Adapter.VH> {


    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_message;
        View view_line_01,view_line_02;

        public VH(View v) {
            super(v);
            tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_message = (TextView) v.findViewById(R.id.tv_message);

            view_line_01 = v.findViewById(R.id.view_line_01);
            view_line_02 = v.findViewById(R.id.view_line_02);
        }
        public void bind(AppointmentTimeChangeModel appointmentTimeChangeCommunication,int type){
            tv_message.setTextColor(context.getResources().getColor(R.color.main_color_gray_46));
            view_line_01.setVisibility(View.VISIBLE);
            view_line_02.setVisibility(View.VISIBLE);
            //0没有线条，1底部线条，2上下线条，3顶部线条
            switch (type){
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
                    tv_message.setTextColor(context.getResources().getColor(R.color.main_color));
                    view_line_02.setVisibility(View.INVISIBLE);
                    break;
            }
            tv_title.setText(appointmentTimeChangeCommunication.getCreateAt());
            boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
            String str ="";
            if(appointmentTimeChangeCommunication.getContent().equals("refuse")){//拒绝
                str = (appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))+"已拒绝"+
                        (!appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))  +"提出的时间，等待"+
                        (!appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))  +"回复……";
            }else {
                if(appointmentTimeChangeCommunication.getSequence()==1){//修改时间
                    str = (appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))+"询问能否调整预约时间为："+appointmentTimeChangeCommunication.getContent();
                }else {//拒绝并提出新时间
                    str = (appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))+"拒绝"+
                            (!appointmentTimeChangeCommunication.getSenderRole().equals("D")?(isPatient?"专家":"您"):(isPatient?"您":"病人"))+"提出的时间，并提出了一个新的时间："+appointmentTimeChangeCommunication.getContent();
                }
            }


            tv_message.setText(str);
        }
    }

    private List<AppointmentTimeChangeModel> mDatas;
    private static Context context;
    private int type;

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public OrderTimeList2Adapter(Context context, List<AppointmentTimeChangeModel> data, int type) {
        this.mDatas = data;
        this.context = context;
        this.type = type;
    }

    int getType(int position){
        //0没有线条，1底部线条，2上下线条，3顶部线条
        if(getItemCount()==1){
            return 0;
        }else if(getItemCount()>1){
            if(position==0){
                return 1;
            }
            if(position<getItemCount()-1){
                return 2;
            }
            if(position==getItemCount()-1){
                return 3;
            }
        }
        return 0;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.bind(mDatas.get(position),getType(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
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

    public static interface OnItemClickListener{
        void onCanel(String requestId);
        void onMotifyTime(String requestId);
        void onChart();
    }








}
