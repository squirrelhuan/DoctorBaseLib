package cn.demomaster.huan.doctorbaselibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.MainMenu;

import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/16.
 * description：
 */
public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MyViewHolder> {

    //当前上下文对象
    Context context;
    //RecyclerView填充Item数据的List对象
    List<MainMenu> datas;

    public MainMenuAdapter(Context context, List<MainMenu> datas){
        this.context = context;
        this.datas = datas;
    }

    //创建ViewHolder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        //View v = View.inflate(context, R.layout.item_main_menu,null);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_menu, parent, false);
        //返回MyViewHolder的对象
        return new MyViewHolder(v);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_menu_title.setText(context.getResources().getText(datas.get(position).getTitleResId()));
        Drawable drawable =context.getResources().getDrawable(datas.get(position).getIconResId());
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), datas.get(position).getIconResId());
        int height = bitmap.getHeight();
        int width= bitmap.getWidth();
        drawable.setBounds(0, 0, width, height);// 一定要设置setBounds();  39/30是长宽比
        bitmap.recycle();
        holder.tv_menu_title.setCompoundDrawables(drawable,null,null,null);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = ((int)v.getTag());
               // ((BaseActivity_NoActionBar)context).startActivity(datas.get(p).getTargetClszz());
                if(onItemClickedListener!=null){
                    onItemClickedListener.onClick(p);
                }
            }
        });

    }
    public static interface OnItemClickedListener {
        void onClick(int position);
    }
    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    //返回Item的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //继承RecyclerView.ViewHolder抽象类的自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_menu_title;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_menu_title = itemView.findViewById(R.id.tv_menu_title);
        }
    }



}
