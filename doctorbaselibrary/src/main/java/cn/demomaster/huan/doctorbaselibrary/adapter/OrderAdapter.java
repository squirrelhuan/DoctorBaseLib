package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.AppointmentTimeChangeCommunication;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderDoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderModelApi;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.BaseOrderListActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.EvaluateActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.order.OrderDetailActivity;
//import cn.demomaster.huan.doctorbaselibrary.view.activity.order.PayActivity;

import java.util.List;

import static cn.demomaster.huan.doctorbaselibrary.adapter.OrderAdapter.OrderState.*;
import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {


    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderName;
        TextView tvOrderWaitTime;
        TextView tvOrderPatientName;
        TextView tvOrderDoctorName;
        TextView tvOrderStartTime;
        TextView tv_bottom_left;//左下角文本
        TableRow tr_order_doctor_name;
        Button btnOrderBtnRight;
        Button btnOrderBtnCenter;
        Button btnOrderBtnLeft;
        LinearLayout ll_doctor;
        RecyclerView recyclerView;
        OrderTimeListAdapter orderTimeListAdapter;
        Context context;

        public VH(View v) {
            super(v);
            context = v.getContext();
            ll_doctor = v.findViewById(R.id.ll_doctor);
            tr_order_doctor_name = v.findViewById(R.id.tr_order_doctor_name);
            tvOrderName = v.findViewById(R.id.tv_order_name);
            tvOrderWaitTime = v.findViewById(R.id.tv_order_wait_time);
            tvOrderPatientName = v.findViewById(R.id.tv_order_patient_name);
            tvOrderDoctorName = v.findViewById(R.id.tv_order_doctor_name);
            tvOrderStartTime = v.findViewById(R.id.tv_order_start_time);
            tv_bottom_left = v.findViewById(R.id.tv_bottom_left);
            btnOrderBtnRight = v.findViewById(R.id.btn_order_btn_right);
            btnOrderBtnLeft = v.findViewById(R.id.btn_order_btn_left);
            btnOrderBtnCenter = v.findViewById(R.id.btn_order_btn_center);
            recyclerView = v.findViewById(R.id.recyclerView);

            //orderAdapter = new OrderTimeListAdapter(this, data,1);
            LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
            //设置布局管理器
            recyclerView.setLayoutManager(layoutManager);
            //设置为垂直布局，这也是默认的
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            //设置Adapter
            recyclerView.setAdapter(orderTimeListAdapter);

            //添加动画
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //设置分隔线
            //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
            //添加分割线
            //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
            //设置增加或删除条目的动画
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        public void onBind(List<AppointmentTimeChangeCommunication> data, int type, int showModifyTime) {
            orderTimeListAdapter = new OrderTimeListAdapter(context, data, type, showModifyTime);
            //设置Adapter
            recyclerView.setAdapter(orderTimeListAdapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private List<OrderModelApi> mDatas;
    private static Context context;
    private int type;

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public OrderAdapter(Context context, List<OrderModelApi> data, int type) {
        this.mDatas = data;
        this.context = context;
        this.type = type;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.tvOrderName.setText(mDatas.get(position).getRequestName().replace("_", "  "));
        holder.tvOrderWaitTime.setText(mDatas.get(position).getWaitingTime());
        holder.tvOrderPatientName.setText(mDatas.get(position).getPatientName());
        if (!TextUtils.isEmpty(mDatas.get(position).getDoctorName())) {
            holder.tvOrderDoctorName.setText(mDatas.get(position).getDoctorName());
            holder.tr_order_doctor_name.setVisibility(View.VISIBLE);
        } else {
            holder.tr_order_doctor_name.setVisibility(View.GONE);
            // holder.tvOrderDoctorName.setText("未指定专家");
        }
        holder.tvOrderStartTime.setText(mDatas.get(position).getAppointTime());
        holder.recyclerView.setVisibility(View.GONE);
        holder.ll_doctor.setVisibility(View.VISIBLE);

        if (type == 0) {
            if (mDatas.get(holder.getAdapterPosition()).getAdditionalInfo() != null && mDatas.get(holder.getAdapterPosition()).getAdditionalInfo().size() > 0) {
                //设置聊天
                holder.tvOrderPatientName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                        onActionClickListener.onChart(requestId, mDatas.get(holder.getAdapterPosition()).getAdditionalInfo(), true);
                    }
                });
                // 使用代码设置drawableleft
                Drawable drawable = context.getResources().getDrawable(
                        R.drawable.btn_chart_bg);
                // / 这一步必须要做,否则不会显示.或者使用setCompoundDrawablesWithIntrinsicBounds保留默认属性
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                holder.tvOrderPatientName.setCompoundDrawables(null, null, drawable, null);
                holder.tvOrderPatientName.setEnabled(true);
            } else {
                holder.tvOrderPatientName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                holder.tvOrderPatientName.setOnClickListener(null);
            }
        } else {
            if (mDatas.get(holder.getAdapterPosition()).getAdditionalInfo() != null && mDatas.get(holder.getAdapterPosition()).getAdditionalInfo().size() > 0) {
                //设置聊天
                holder.tvOrderPatientName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                        onActionClickListener.onChart(requestId, mDatas.get(holder.getAdapterPosition()).getAdditionalInfo(), false);
                    }
                });
                // 使用代码设置drawableleft
                Drawable drawable = context.getResources().getDrawable(
                        R.drawable.btn_chart_bg);
                holder.tvOrderPatientName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            } else {
                holder.tvOrderPatientName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                holder.tvOrderPatientName.setOnClickListener(null);
            }
        }
        OrderState orderState = NONE;
        if (mDatas.get(position).getAppointmentTimeChangeCommunicationModel() != null && mDatas.get(position).getAppointmentTimeChangeCommunicationModel().size() > 0) {
            holder.onBind(mDatas.get(position).getAppointmentTimeChangeCommunicationModel(), 1, mDatas.get(position).getAppointmentTimeChangeChance());
            List<AppointmentTimeChangeCommunication> list = mDatas.get(position).getAppointmentTimeChangeCommunicationModel();
            int i = list.size() - 1;
            AppointmentTimeChangeCommunication appointmentTimeChangeCommunication = list.get(i);
            if (appointmentTimeChangeCommunication.getContent().equals("refuse")) {//拒绝分为两种，第一次拒绝，和第二次拒绝
                if (appointmentTimeChangeCommunication.getSequence() != 1) {//第二次拒绝 ,只能取消
                    orderState = REFUSE_MODIFY;
                } else {//第一次拒绝，可以维持原时间，或者取消
                    orderState = REFUSE;
                }
            } else {
                orderState = MODIFY_TIME;
            }
        } else if (mDatas.get(position).getAppointmentTimeChangeChance() == 1) {
            orderState = ACCEPTED;
        } else {
            orderState = ONLYCANCEL;
        }

        holder.btnOrderBtnLeft.setTextColor(context.getResources().getColor(R.color.main_color_gray_c3));
        holder.btnOrderBtnLeft.setBackground(context.getResources().getDrawable(R.drawable.button_common_small_normal));
        holder.btnOrderBtnCenter.setTextColor(context.getResources().getColor(R.color.main_color_gray_c3));
        holder.btnOrderBtnCenter.setBackground(context.getResources().getDrawable(R.drawable.button_common_small_normal));
        holder.btnOrderBtnRight.setTextColor(context.getResources().getColor(R.color.main_color));
        holder.btnOrderBtnRight.setBackground(context.getResources().getDrawable(R.drawable.button_common_small_selected));
        View.OnClickListener onClickListener_left = null;
        View.OnClickListener onClickListener_center = null;
        View.OnClickListener onClickListener_right = null;
        holder.btnOrderBtnLeft.setVisibility(View.GONE);
        holder.btnOrderBtnCenter.setVisibility(View.GONE);
        holder.btnOrderBtnRight.setVisibility(View.GONE);
        holder.tv_bottom_left.setVisibility(View.GONE);
        String t = AppConfig.getInstance().isPatient() ? "P" : "D";
        switch (type) {
            case 0://申请中只有取消，申请中才有聊天
                holder.tv_bottom_left.setVisibility(View.VISIBLE);
                holder.tvOrderWaitTime.setVisibility(View.GONE);
                holder.tv_bottom_left.setText("申请中，已等待" + mDatas.get(position).getWaitingTime());
                holder.ll_doctor.setVisibility(View.GONE);
                holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                holder.btnOrderBtnRight.setTextColor(context.getResources().getColor(R.color.main_color_gray_c3));
                holder.btnOrderBtnRight.setBackground(context.getResources().getDrawable(R.drawable.button_common_small_normal));
                holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.Cancel_order));
                onClickListener_right = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                        //取消订单
                        onActionClickListener.onCanel(requestId);
                    }
                };

                List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList = mDatas.get(holder.getAdapterPosition()).getAdditionalInfo();
                final boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
                if (isPatient) {//用户端
                    if (additionalInfoList == null || additionalInfoList.size() == 0) {//不可以回复消息
                        holder.tvOrderPatientName.setEnabled(false);
                    }
                } else {//医生端
                    /*if (canAnswer()) {//可以询问消息
                        PopToastUtil.ShowToast((Activity) mContext, "医生3次机会用完了");
                    }*/
                }
                break;
            case 1://进行中
                holder.tvOrderWaitTime.setVisibility(View.VISIBLE);
                switch (mDatas.get(position).getRequestStatus()) {
                    case "ACCEPTED":
                        holder.tvOrderWaitTime.setText("已接单");
                        break;
                    case "MODIFY_TIME":
                        holder.tvOrderWaitTime.setText("已接单");
                        break;
                    case "WAITING_PAY":
                        holder.tvOrderWaitTime.setText("待支付");
                        orderState = WAITING_PAY;
                        break;
                    case "WAITING_CONSULT_ADVISE":
                        holder.tvOrderWaitTime.setText("等待医生填写咨询意见");
                        orderState = WAITING_CONSULT_ADVISE;
                        break;
                }
                switch (orderState) {
                    case ACCEPTED://申请中可以接单
                        holder.btnOrderBtnLeft.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Cancel_order));
                        onClickListener_left = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                //取消订单
                                onActionClickListener.onCanel(requestId);
                            }
                        };
                        holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.Modification_time));
                        onClickListener_right = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                onActionClickListener.onMotifyTime(requestId);
                            }
                        };
                        break;
                    case MODIFY_TIME://对方修改可以同意 或者取消
                        List<AppointmentTimeChangeCommunication> list1 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                        AppointmentTimeChangeCommunication appointmentTimeChangeModel = list1.get(list1.size() - 1);
                        holder.btnOrderBtnLeft.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Cancel_order));
                        onClickListener_left = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                //取消订单
                                onActionClickListener.onCanel(requestId);
                            }
                        };
                        if (appointmentTimeChangeModel.getSenderRole() != null && !appointmentTimeChangeModel.getSenderRole().equals(t)) {
                            holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Refuse));
                            onClickListener_left = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                    //取消订单
                                    // onActionClickListener.onCanel(requestId);
                                    List<AppointmentTimeChangeCommunication> list1 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                                    AppointmentTimeChangeCommunication appointmentTimeChangeModel = list1.get(list1.size() - 1);
                                    String sourceId = appointmentTimeChangeModel.getSourceId() == null ? appointmentTimeChangeModel.getId() + "" : appointmentTimeChangeModel.getSourceId();
                                    onActionClickListener.refuseAppointTimeChange(requestId, sourceId);
                                }
                            };

                            holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                            holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.Agree));
                            onClickListener_right = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                    List<AppointmentTimeChangeCommunication> list1 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                                    AppointmentTimeChangeCommunication appointmentTimeChangeModel = list1.get(list1.size() - 1);
                                    String sourceId = appointmentTimeChangeModel.getSourceId() == null ? appointmentTimeChangeModel.getId() + "" : appointmentTimeChangeModel.getSourceId();
                                    //TODO 同意
                                    onActionClickListener.approveAppointTimeChange(requestId, appointmentTimeChangeModel.getContent(), sourceId);
                                }
                            };
                        }
                        break;
                    case REFUSE_MODIFY://对方拒绝并提出新的修改时间
                        List<AppointmentTimeChangeCommunication> list2 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                        AppointmentTimeChangeCommunication appointmentTimeChangeModel2 = list2.get(list2.size() - 1);
                        if (appointmentTimeChangeModel2.getSenderRole() != null && !appointmentTimeChangeModel2.getSenderRole().equals(t)) {
                            holder.btnOrderBtnLeft.setVisibility(View.VISIBLE);
                            holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Cancel_order));
                            onClickListener_left = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                    //取消订单
                                    onActionClickListener.onCanel(requestId);
                                }
                            };
                            holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                            holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.Maintain_the_original_time));
                            onClickListener_right = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                    List<AppointmentTimeChangeCommunication> list1 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                                    AppointmentTimeChangeCommunication appointmentTimeChangeModel = list1.get(list1.size() - 1);
                                    String sourceId = appointmentTimeChangeModel.getSourceId() == null ? appointmentTimeChangeModel.getId() + "" : appointmentTimeChangeModel.getSourceId();
                                    /*//TODO 同意
                                    onActionClickListener.approveAppointTimeChange(requestId, appointmentTimeChangeModel.getContent(), sourceId);*/
                                    //维持原时间
                                    onActionClickListener.keepPreviousTime(requestId);
                                }
                            };
                        }
                        break;
                    case REFUSE://直接拒绝
                        holder.btnOrderBtnLeft.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Cancel_order));
                        onClickListener_left = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                //取消订单
                                onActionClickListener.onCanel(requestId);
                            }
                        };
                        holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.Maintain_the_original_time));
                        onClickListener_right = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                List<AppointmentTimeChangeCommunication> list1 = mDatas.get(holder.getAdapterPosition()).getAppointmentTimeChangeCommunicationModel();
                                AppointmentTimeChangeCommunication appointmentTimeChangeModel = list1.get(list1.size() - 1);
                                String sourceId = appointmentTimeChangeModel.getSourceId() == null ? appointmentTimeChangeModel.getId() + "" : appointmentTimeChangeModel.getSourceId();
                                //维持原时间
                                onActionClickListener.keepPreviousTime(requestId);
                            }
                        };
                        break;
                    case ONLYCANCEL:
                        holder.btnOrderBtnLeft.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnLeft.setText(context.getResources().getString(R.string.Cancel_order));
                        onClickListener_left = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String requestId = mDatas.get(holder.getAdapterPosition()).getRequestId();
                                //取消订单
                                onActionClickListener.onCanel(requestId);
                            }
                        };
                        break;
                    case WAITING_PAY:
                        holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.goto_pay));
                        onClickListener_right = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Class orderPayActivity = AppConfig.getInstance().getOrderPayActivity();
                                Intent intent = new Intent(context, orderPayActivity);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("OrderModelApi", mDatas.get(holder.getAdapterPosition()));
                                bundle.putString("trxId", mDatas.get(holder.getAdapterPosition()).getRequestId());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        };
                       /* holder.btnOrderBtnCenter.setVisibility(View.VISIBLE);
                        holder.btnOrderBtnCenter.setText(context.getResources().getString(R.string.evaluate));
                        onClickListener_center = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Class orderPayActivity = AppConfig.getInstance().getOrderPayActivity();
                                Intent intent = new Intent(context, EvaluateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("requestId", mDatas.get(holder.getAdapterPosition()).getRequestId());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        };*/
                        break;
                }
                break;
            case 2:
                if (mDatas.get(position).getIsEvaluated().equals("N")) {
                    holder.btnOrderBtnRight.setVisibility(View.VISIBLE);
                    holder.btnOrderBtnRight.setText(context.getResources().getString(R.string.evaluate));
                    onClickListener_right = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, EvaluateActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("requestId", mDatas.get(holder.getAdapterPosition()).getRequestId());
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    };
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        Bundle bundle = new Bundle();
                        String trxId = mDatas.get(holder.getAdapterPosition()).getRequestId() + "";
                        bundle.putString("trxId", trxId);
                        intent.putExtras(bundle);
                        ((Activity) context).startActivityForResult(intent, 100);
                        //mContext.startActivity(intent);
                    }
                });
                break;
        }
        holder.btnOrderBtnRight.setOnClickListener(onClickListener_right);
        holder.btnOrderBtnCenter.setOnClickListener(onClickListener_center);
        holder.btnOrderBtnLeft.setOnClickListener(onClickListener_left);
    }

    public static enum OrderState {
        MODIFY_TIME, ACCEPTED, REFUSE, REFUSE_MODIFY, NONE, WAITING_PAY, ONLYCANCEL, WAITING_CONSULT_ADVISE
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    private BaseOrderListActivity.OnActionClickListener onActionClickListener;

    public void setOnActionClickListener(BaseOrderListActivity.OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

}
