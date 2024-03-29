package cn.demomaster.huan.doctorbaselibrary.view.widget.time;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.model.AreaModel;
import cn.demomaster.huan.doctorbaselibrary.util.FileUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.pickview.LoopScrollListener;
import cn.demomaster.huan.quickdeveloplibrary.view.pickview.LoopView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author squirrel桓
 * @date 2018/11/16.
 * description：
 */
public class CommonPickerPop extends PopupWindow implements View.OnClickListener {
    private Button cancelBtn;
    private Button confirmBtn;
    private String def_value = null;
    private LoopView picker_common;
    private View pickerContainerV;
    private View contentView;
    private int currentPositon = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;

    private int topBottomTextColor = 0xffA9A9A9;//array.getColor(styleable.LoopView_topBottomTextColor, -5263441);
    private int centerTextColor = 0xff11ddaf;//array.getColor(styleable.LoopView_centerTextColor, -13553359);
    private int centerLineColor = 0x00000000;//array.getColor(styleable.LoopView_lineColor, -3815995);
    private int viewTextSize;
    List<String> dataList = new ArrayList();
    private OnPickListener mListener;

    public CommonPickerPop(Builder builder) {
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.topBottomTextColor = builder.topBottomTextColor;
        this.centerTextColor = builder.centerTextColor;
        this.centerLineColor = builder.centerLineColor;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.dataList = builder.date;
        this.def_value = builder.def_value;
        init();
        this.initView();
    }

    private WindowManager.LayoutParams mWindowParams;

    private void init() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.alpha = 1.0f;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        //mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //窗口类型
        if (Build.VERSION.SDK_INT > 25) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mWindowParams.setTitle("");
        mWindowParams.packageName = mContext.getPackageName();
        setClippingEnabled(false);
        //mWindowParams.windowAnimations = animStyleId;// TODO
        //mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 5;
        //mWindowParams.windowAnimations = R.style.CustomToast;//动画
    }

    private void initView() {
        this.contentView = LayoutInflater.from(this.mContext).inflate(R.layout.layout_common_picker, (ViewGroup) null);
        this.cancelBtn = (Button) this.contentView.findViewById(R.id.btn_cancel);
        this.cancelBtn.setTextColor(this.colorCancel);
        this.cancelBtn.setTextSize((float) this.btnTextsize);
        this.confirmBtn = (Button) this.contentView.findViewById(R.id.btn_confirm);
        this.confirmBtn.setTextColor(this.colorConfirm);
        this.confirmBtn.setTextSize((float) this.btnTextsize);

        this.picker_common = (LoopView) this.contentView.findViewById(R.id.picker_common);
        this.picker_common.setTextColor(topBottomTextColor, centerTextColor, centerLineColor);
        this.picker_common.setLoopListener(new LoopScrollListener() {
            public void onItemSelect(int item) {
                CommonPickerPop.this.currentPositon = item;
            }
        });
        this.pickerContainerV = this.contentView.findViewById(R.id.container_picker);

        this.initPickerViews();
        this.cancelBtn.setOnClickListener(this);
        this.confirmBtn.setOnClickListener(this);
        this.contentView.setOnClickListener(this);
        if (!TextUtils.isEmpty(this.textConfirm)) {
            this.confirmBtn.setText(this.textConfirm);
        }

        if (!TextUtils.isEmpty(this.textCancel)) {
            this.cancelBtn.setText(this.textCancel);
        }

        this.setTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(cn.demomaster.huan.quickdeveloplibrary.R.style.FadeInPopWin);
        this.setContentView(this.contentView);
        this.setWidth(-1);
        this.setHeight(-1);
    }


    private void initPickerViews() {

        if (TextUtils.isEmpty(this.def_value)) {
            this.currentPositon = 0;
        } else {
            this.currentPositon = getDefIndex(this.def_value, dataList);
        }

        this.picker_common.setDataList(this.dataList);
        this.picker_common.setInitPosition(this.currentPositon);
    }

    private int getDefIndex(String def_provience, List<String> provinceList) {
        for (int i = 0; i < provinceList.size(); i++) {
            if (provinceList.get(i).equals(def_provience)) {
                return i;
            }
        }
        return 0;
    }

    public void onClick(View v) {
        if (v != this.contentView && v != this.cancelBtn) {
            if (v == this.confirmBtn) {
                if (null != this.mListener) {
                    this.mListener.onPickCompleted(dataList, dataList.get(this.currentPositon), this.currentPositon);
                }
                this.dismissPopWin();
            }
        } else {
            this.dismissPopWin();
        }
    }

    public void showPopWin(Activity activity) {
        if (null != activity) {
            TranslateAnimation trans = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 1.0F, 1, 0.0F);
            this.showAtLocation(activity.getWindow().getDecorView(), 80, 0, 0);
            trans.setDuration(200L);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            this.pickerContainerV.startAnimation(trans);
        }
    }

    public void dismissPopWin() {
        TranslateAnimation trans = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 0.0F, 1, 1.0F);
        trans.setDuration(160L);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());//AccelerateInterpolator
        trans.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                CommonPickerPop.this.dismiss();
            }
        });
        this.pickerContainerV.startAnimation(trans);
    }


    public interface OnPickListener {
        void onPickCompleted(List<String> stringList, String value, int position);
    }

    public static class Builder {
        private Context context;
        private OnPickListener listener;
        private String textCancel;
        private String textConfirm;
        private String def_value = null;
        private int topBottomTextColor = 0xffA9A9A9;//array.getColor(styleable.LoopView_topBottomTextColor, -5263441);
        private int centerTextColor = 0xff11ddaf;//array.getColor(styleable.LoopView_centerTextColor, -13553359);
        private int centerLineColor = 0x00000000;//array.getColor(styleable.LoopView_lineColor, -3815995);
        private int colorCancel;
        private int colorConfirm;
        private int colorText;
        private int btnTextSize = 16;
        private int viewTextSize = 25;
        private List<String> date;

        public Builder(Context context, OnPickListener listener) {
            this.context = context;
            this.listener = listener;
            textCancel = context.getResources().getString(cn.demomaster.huan.quickdeveloplibrary.R.string.Cancel);
            textConfirm = context.getResources().getString(cn.demomaster.huan.quickdeveloplibrary.R.string.Confirm);
            colorCancel = context.getResources().getColor(cn.demomaster.huan.quickdeveloplibrary.R.color.colorAccent);//Color.parseColor("#999999");
            colorConfirm = context.getResources().getColor(cn.demomaster.huan.quickdeveloplibrary.R.color.colorAccent);//Color.parseColor("#303F9F");
            colorText = context.getResources().getColor(cn.demomaster.huan.quickdeveloplibrary.R.color.colorAccent);//Color.parseColor("#f60");
        }

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }


        public Builder colorContentText(int topBottomTextColor, int centerTextColor, int centerLineColor) {
            this.topBottomTextColor = topBottomTextColor;
            this.centerTextColor = centerTextColor;
            this.centerLineColor = centerLineColor;
            return this;
        }

        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public Builder setDate(List<String> date) {
            this.date = date;
            return this;
        }

        public CommonPickerPop build() {
            return new CommonPickerPop(this);
        }

        public Builder setDefaultValue(String def_provience) {
            this.def_value = def_provience;
            return this;
        }
    }


}
