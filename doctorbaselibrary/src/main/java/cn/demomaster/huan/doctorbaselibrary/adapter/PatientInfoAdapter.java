package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.api.UserModelApi;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/9.
 * description：
 */
// ① 创建Adapter
public class PatientInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //② 创建ViewHolder


    public static class VH2 extends RecyclerView.ViewHolder {
        public ImageTextView iv_patient_head;
        private TextView tv_patient_name,  tv_address;

        public VH2(View v) {
            super(v);
            iv_patient_head = v.findViewById(R.id.iv_patient_head);
            iv_patient_head.setTextSize(14);
            tv_patient_name = v.findViewById(R.id.tv_patient_name);
            tv_address = v.findViewById(R.id.tv_address);
        }
    }

    private Context context;
    private List<UserModelApi> mDatas;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PatientInfoAdapter(Context context, List<UserModelApi> data) {
        this.mDatas = data;
        this.context = context;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        VH2 holder1 = (VH2) holder;

        if (mDatas.get(position).getPhotoUrl() == null) {
            holder1.iv_patient_head.setText(mDatas.get(position).getUserName());
            holder1.iv_patient_head.setTextColor(Color.WHITE);
            holder1.iv_patient_head.setBackgroundResource(R.drawable.image_circle_head_bg_02);
        } else {
            holder1.iv_patient_head.setText("");
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(false);//不做内存缓存
            Glide.with(context).load(mDatas.get(position).getPhotoUrl()).apply(mRequestOptions).into(holder1.iv_patient_head);
            holder1.iv_patient_head.setBackgroundResource(R.drawable.image_circle_head_bg_01);
        }
        holder1.tv_patient_name.setText(mDatas.get(position).getUserName());

        holder1.itemView.setTag(position);
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(v, (int) v.getTag(), mDatas.get((int) v.getTag()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_patient_info, parent, false);
        return new VH2(v);
    }

    public static interface OnItemClickListener {
        void onItemClicked(View view, int position, UserModelApi userModelApi);
    }

}
