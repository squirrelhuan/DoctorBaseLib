package cn.demomaster.huan.doctorbaselibrary.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import cn.demomaster.huan.doctorbaselibrary.R;


public class ActionBarUtil {

    //通用样式（左侧返回，右侧分享，中间标题）
    public final static int ActionBarCommon = 0;


    //样式主题1
    public final static int ActionBarTheme_Dark = 0;
    //样式主题2
    public final static int ActionBarTheme_Light = 1;
    //样式主题3 透明背景
    public final static int ActionBarTheme_Dark_Transparent = 2;
    //样式主题4 透明背景
    public final static int ActionBarTheme_Light_Transparent = 3;

    public final static String COMMON_ACTIONBAR_TITLE = "COMMON_ACTIONBAR_TITLE";

    public final static String COMMON_ACTIONBAR_NOACTIONBAR = "COMMON_ACTIONBAR_NOACTIONBAR";

    /**
     * 获取通用导航栏
     *
     * @param context
     * @param isSeat  是否占用位置
     */
    public static ActionBarInterface getActionBar(final Activity context, int type, boolean isSeat) {
        ViewGroup rel_root = (ViewGroup) context.findViewById(R.id.rel_root);
        if (rel_root != null) {
            ViewParent parent = rel_root.getParent();
            switch (type) {
                case ActionBarCommon:
                    //获取一个Layout参数，设置为全屏
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LayoutInflater mInflater = LayoutInflater.from(context);
                    ViewGroup layout_actionbar = (ViewGroup) mInflater.inflate(R.layout.activity_no_actionbar, null);

                    layout_actionbar.setLayoutParams(params);
                    ((ViewGroup) parent).addView(layout_actionbar);
                    ActionBarModel actionBarInterface = new ActionBarModel(context, layout_actionbar, rel_root);
                    //默认左侧是返回按钮
                    actionBarInterface.iv_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.finish();
                        }
                    });
                    //默认右侧侧是分享按钮
                    actionBarInterface.iv_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ScreenShotUitl.shot(context);
                        }
                    });
                    AnimationUtil.addScaleAnimition(actionBarInterface.iv_left, null);
                    AnimationUtil.addScaleAnimition(actionBarInterface.iv_right, null);
                    return actionBarInterface;
            }

        }
        return null;
    }

    public interface ActionBarInterface {

        void setTitle(String title);

        void hide();

        void show();

        void hideTitle();

        void showTitle();

        void hideLeft();

        void showLeft();

        void hideRight();

        void showRight();

        void setOnLeftClickListener(View.OnClickListener leftClickListener);

        void setOnRightClickListener(View.OnClickListener rightClickListener);

        void setTheme(int type);

        void setIsSeat(Boolean isSeat);

        ImageView getLeftView();

        ImageView getRightView();

        View getContentView();
    }

    //通用导航栏
    public static class ActionBarModel implements ActionBarInterface {
        private Context context;
        private ViewGroup viewGroup;
        private View viewBrother;//导航栏下面的布局
        private TextView tv_title;
        private ImageView iv_left;
        private ImageView iv_right;
        private View.OnClickListener leftOnClickListener;
        private View.OnClickListener rightOnClickListener;
        private Boolean isSeat = true;
        private int paddingTop =0;
        private int marginTop =0;

        public ActionBarModel(final Activity context, ViewGroup viewGroup, final View viewBrother) {
            this.context = context;
            this.viewGroup = viewGroup;
            this.viewBrother = viewBrother;
            this.tv_title = viewGroup.findViewById(R.id.tv_actionbar_common_title);
            this.iv_left = viewGroup.findViewById(R.id.iv_actionbar_common_left);
            this.iv_right = viewGroup.findViewById(R.id.iv_actionbar_common_right);
            setTheme(ActionBarTheme_Dark);

            //view加载完成时回调
            viewBrother.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // TODO Auto-generated method stub

                }
            });
            invalidateView();
        }

        public void invalidateView(){
            ViewGroup.LayoutParams layoutParams = viewBrother.getLayoutParams();
            ViewParent view = viewBrother.getParent();
            int t =0;
            if (isSeat) {
                //t = viewBrother.getHeight();
                t =  (int)this.context.getResources().getDimension(R.dimen.activity_actionbar_height);
            }
            if (!(view instanceof ContentFrameLayout)) {
                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    viewBrother.setPadding(0, t+paddingTop, 0, 0);
                    //((LinearLayout.LayoutParams)layoutParams).(0,t, 0, 0);
                } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) layoutParams).setMargins(0, t, 0, 0);
                } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) layoutParams).setMargins(0, t, 0, 0);
                }
                viewBrother.setLayoutParams(layoutParams);
            } else {
                viewBrother.setPadding(0, t+paddingTop, 0, 0);
            }
        }

        //设置导航栏是否占用位置
        @Override
        public void setIsSeat(Boolean isSeat) {
            this.isSeat = isSeat;
        }

        @Override
        public void setTheme(int type) {
            switch (type) {
                case ActionBarTheme_Dark://黑色主题
                    iv_left.setImageResource(R.mipmap.ic_action_back);
                    break;
                case ActionBarTheme_Light:
                    break;
                case ActionBarTheme_Dark_Transparent:
                    this.viewGroup.setBackgroundColor(Color.TRANSPARENT);
                    //((View)this.viewGroup.getParent()).setBackgroundColor(Color.RED);
                    break;
                case ActionBarTheme_Light_Transparent:
                    this.viewGroup.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
        }

        @Override
        public void setTitle(String title) {
            this.tv_title.setText(title);
        }

        @Override
        public void hide() {
            viewGroup.setVisibility(View.GONE);
            setIsSeat(false);
        }

        @Override
        public void show() {
            viewGroup.setVisibility(View.VISIBLE);
            setIsSeat(true);
        }

        @Override
        public void hideTitle() {
            tv_title.setVisibility(View.INVISIBLE);
        }

        @Override
        public void showTitle() {
            tv_title.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideLeft() {
            iv_left.setVisibility(View.INVISIBLE);
        }

        @Override
        public void showLeft() {
            iv_left.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideRight() {
            iv_right.setVisibility(View.INVISIBLE);
        }

        @Override
        public void showRight() {
            iv_right.setVisibility(View.VISIBLE);
        }

        @Override
        public void setOnLeftClickListener(View.OnClickListener leftClickListener) {
            iv_left.setOnClickListener(leftClickListener);
        }

        @Override
        public void setOnRightClickListener(View.OnClickListener rightClickListener) {
            iv_right.setOnClickListener(rightClickListener);
        }

        public TextView getTv_title() {
            return tv_title;
        }

        public void setTv_title(TextView tv_title) {
            this.tv_title = tv_title;
        }

        public ImageView getLeftView() {
            return iv_left;
        }

        public void setIv_left(ImageView iv_left) {
            this.iv_left = iv_left;
        }

        public ImageView getRightView() {
            return iv_right;
        }

        public void setIv_right(ImageView iv_right) {
            this.iv_right = iv_right;
        }

        public View.OnClickListener getLeftOnClickListener() {
            return leftOnClickListener;
        }

        public void setLeftOnClickListener(View.OnClickListener leftOnClickListener) {
            this.leftOnClickListener = leftOnClickListener;
        }

        public View.OnClickListener getRightOnClickListener() {
            return rightOnClickListener;
        }

        public void setRightOnClickListener(View.OnClickListener rightOnClickListener) {
            this.rightOnClickListener = rightOnClickListener;
        }

        @Override
        public View getContentView() {
            return viewGroup;
        }
    }

}



