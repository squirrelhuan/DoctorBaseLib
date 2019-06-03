package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.view.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class HistoryAdapter implements FlowLayout.FlowAdapter {
    private List<String> lists = new ArrayList();
    private Context context;
    private LayoutInflater inflater;

    public HistoryAdapter(Context context, List<String> lists) {
        this.context = context;
        this.lists = lists;
        this.inflater = ((Activity)context).getLayoutInflater();
    }

    public int getCount() {
        return this.lists.size();
    }

    public Object getItem(int position) {
        return this.lists.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position) {
        /*ViewHolder holder = null;
        if (convertView == null) {*/
          View  convertView = this.inflater.inflate(R.layout.item_search_history, null, true);
           /* holder = new ViewHolder();
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }*/

        TextView tv_title = ((TextView)convertView.findViewById(R.id.tv_title));
        tv_title.setText(lists.get(position));
        tv_title.setTag(position);
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onClick(v,(int)v.getTag());
                }
            }
        });
        return convertView;
    }

    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener{
       void onClick(View view, int position);
    }


    private class ViewHolder {
        TextView tv_title;

        private ViewHolder() {
        }
    }

}
