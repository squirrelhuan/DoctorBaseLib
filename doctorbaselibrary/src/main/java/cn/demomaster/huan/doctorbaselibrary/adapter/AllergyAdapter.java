package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient.AddAllergyActivity;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.FlowLayout;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class AllergyAdapter implements FlowLayout.FlowAdapter {
    private List<String> lists = new ArrayList();
    private Context context;
    private LayoutInflater inflater;
    private Class clazz;
    private Bundle bundle;
    private boolean isEmpty = false;//初始化時數據是否是空
    private int dataType;//0过敏，1患病史
    private boolean isEdit;

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public AllergyAdapter(Context context, List<String> lists, Class clazz, Bundle bundle, int dataType, OnItemClickListener onItemClickListener,
                          OnItemLongClickListener onItemLongClickListener, OnItemRemoveClickListener onItemRemoveClickListener) {
        this.context = context;
        this.lists = lists;
        this.clazz = clazz;
        this.bundle = bundle;
        this.inflater = ((Activity) context).getLayoutInflater();
        this.dataType = dataType;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
        this.onItemRemoveClickListener = onItemRemoveClickListener;
    }

    public int getCount() {
        if (lists == null || lists.size() == 0) {
            if (dataType == 0) {
                lists.add("例:");
                lists.add("青霉素");
                lists.add("磺胺类");
            } else if (dataType == 1) {
                lists.add("例:");
                lists.add("糖尿病");
                lists.add("高血压");
            }
            isEmpty = true;
            return 3;
        }
        return this.lists.size() + 1;
    }

    public Object getItem(int position) {
        return this.lists.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position) {
        /*ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }*/
        View convertView;
        int p = DisplayUtil.dip2px(context, 5);
        int p2 = DisplayUtil.dip2px(context, 3);
        if (position == getCount() - 1) {
            convertView = this.inflater.inflate(R.layout.item_allergry_add, null, true);
            ImageTextView iv_add = convertView.findViewById(R.id.iv_add);
            iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clazz != null) {
                        ((BaseActivity) context).startActivity(clazz, bundle);
                    }
                }
            });
            return convertView;
        } else {
            convertView = this.inflater.inflate(R.layout.item_allergry_empty, null, true);
            ImageView iv_remove = convertView.findViewById(R.id.iv_remove);
            TextView tv_title = convertView.findViewById(R.id.tv_title);
            if (isEdit) {
                iv_remove.setVisibility(View.VISIBLE);
                tv_title.setTextColor(context.getResources().getColor(R.color.white));
                tv_title.setPadding(p, p2, p, p2);
                tv_title.setText(lists.get(position));
                tv_title.setEnabled(false);
                iv_remove.setTag(position);
                iv_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemRemoveClickListener!=null){
                            onItemRemoveClickListener.onRemoveClick(v,(int) v.getTag());
                        }
                    }
                });
            } else {
                iv_remove.setVisibility(View.GONE);
                tv_title.setTextColor(context.getResources().getColor(R.color.white));
                tv_title.setPadding(p, p2, p, p2);
                if (isEmpty) {
                    if (position == 0) {
                        tv_title.setTextColor(context.getResources().getColor(R.color.main_color_gray_46_a35));
                        tv_title.setBackground(null);
                        tv_title.setPadding(p, p2, 0, p2);
                    } else {
                        tv_title.setEnabled(false);
                        //tv_title.setBackgroundResource(R.drawable.btn_tag_gray);
                    }
                } else {
                    tv_title.setEnabled(true);
                    //tv_title.setBackgroundResource(R.drawable.btn_login_small);
                }
                tv_title.setText(lists.get(position));
                tv_title.setTag(position);
                if (onItemClickListener != null) {
                    tv_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onClick(v, (int) v.getTag());
                        }
                    });
                }
                if (onItemLongClickListener != null) {
                    tv_title.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            enterEdit();
                            onItemLongClickListener.onLongClick(v, (int) v.getTag());
                            return false;
                        }
                    });
                }
            }
            return convertView;
        }
    }

    //进入编辑模式
    private void enterEdit() {
        isEdit = true;
        Vibrator vibrator;
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        //震动30毫秒
        vibrator.vibrate(50);
        //Toast.makeText(context, "编辑模式可以删除", Toast.LENGTH_LONG).show();
    }

    private OnItemClickListener onItemClickListener;

    public static interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public static interface OnItemLongClickListener {
        void onLongClick(View view, int position);
    }

    private OnItemRemoveClickListener onItemRemoveClickListener;

    public static interface OnItemRemoveClickListener {
        void onRemoveClick(View view, int position);
    }
    private class ViewHolder {
        TextView tv_title;

        private ViewHolder() {
        }
    }

}
