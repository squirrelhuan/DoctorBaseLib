package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient.AddAllergyActivity;
import cn.demomaster.huan.quickdeveloplibrary.widget.FlowLayout;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class TagFlowAdapter implements FlowLayout.FlowAdapter {
    private List<AddAllergyActivity.Tag> lists = new ArrayList();
    private Context context;
    private LayoutInflater inflater;
    private Class clazz;
    private Bundle bundle;

    public TagFlowAdapter(Context context, List<AddAllergyActivity.Tag> lists, Class clazz, Bundle bundle) {
        this.context = context;
        this.lists = lists;
        this.clazz = clazz;
        this.bundle = bundle;
        this.inflater = ((Activity) context).getLayoutInflater();
    }

    public int getCount() {
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
        if (position != 0) {
            convertView = this.inflater.inflate(R.layout.item_allergry_empty, null, true);
            TextView tv_title = convertView.findViewById(R.id.tv_title);
            tv_title.setText(lists.get(position-1).getName());
            if(lists.get(position-1).isActive()){
                tv_title.setTextColor(context.getResources().getColor(R.color.white));
                tv_title.setBackgroundResource(R.drawable.btn_login_small);
            }else {
                tv_title.setTextColor(context.getResources().getColor(R.color.main_color));
                tv_title.setBackgroundResource(R.drawable.panel_background_activate);
            }
            tv_title.setTag(position-1);
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(v, (int) v.getTag());
                    }
                }
            });
            return convertView;
        } else {
            convertView = this.inflater.inflate(R.layout.item_allergry_add, null, true);
            ImageTextView iv_add = convertView.findViewById(R.id.iv_add);
            iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clazz != null) {
                        Intent intent = new Intent(context,clazz);
                        intent.putExtras(bundle);
                        ((BaseActivity)context).startActivityForResult(intent,201);
                    }
                }
            });
            return convertView;
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private class ViewHolder {
        TextView tv_title;
        private ViewHolder() {
        }
    }

}
