package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.view.activity.chat.ChatWindow;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static class VH_base extends RecyclerView.ViewHolder {
        ImageTextView itv_header;
        TextView tv_message;
        public VH_base(View v) {
            super(v);
            itv_header =  v.findViewById(R.id.itv_header);
            tv_message =  v.findViewById(R.id.tv_message);
        }
    }
    //② 创建ViewHolder
    public static class VH_left extends VH_base {
        public VH_left(View v) {
            super(v);
        }
    }
    //② 创建ViewHolder
    public static class VH_right extends VH_base {
        public VH_right(View v) {
            super(v);
        }
    }

    private List<ChatWindow.ChatMessaage> mDatas;
    private static Context context;
    public ChatAdapter(Context context, List<ChatWindow.ChatMessaage> data) {
        this.mDatas = data;
        this.context = context;

    }

    //③ 在Adapter中实现3个方法

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VH_base)viewHolder).tv_message.setText(mDatas.get(i).getMessageContent());
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).isSender()?1:0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = null;
        RecyclerView.ViewHolder vh = null;
        if(viewType==0){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            vh = new VH_left(v);
        }else if(viewType==1){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            vh = new VH_right(v);
        }
        return vh;
    }

}
