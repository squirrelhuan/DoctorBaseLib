package cn.demomaster.huan.doctorbaselibrary.view.widget;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.CustomDialog;

/**
 * @author squirrel桓
 * @date 2018/12/17.
 * description：
 */
public class PopWinSingleDialog {

    private static Context mContext;
    private String contentText;
    private String btn_text;
    private TextView contentView;
    private Button btn_ok;
    private View.OnClickListener onClickListener_ok;
    private static boolean canTouch;


    private static CustomDialog customDialog;
    private static PopWinSingleDialog instance;
    private static CustomDialog.Builder builder;

    public static PopWinSingleDialog getInstance(Context context) {
        instance = new PopWinSingleDialog(context);
        return instance;
    }

    PopWinSingleDialog(Context context) {
        builder = new CustomDialog.Builder(context, R.layout.item_pop_dialog_single);
        customDialog = builder.setCanTouch(canTouch).create();
        init();
    }

    private void init() {
        View ccustomDialogView = customDialog.getContentView();
        contentView = ccustomDialogView.findViewById(R.id.tv_content);
        contentView.setText(contentText);
        btn_ok = ccustomDialogView.findViewById(R.id.btn_ok);
        btn_ok.setText(btn_text);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                if (onClickListener_ok != null) {
                    onClickListener_ok.onClick(v);
                }
            }
        });
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
        contentView.setText(contentText);
    }

    public void setBtn_text(String btn_text) {
        this.btn_text = btn_text;
        btn_ok.setText(btn_text);
    }

    public void setOnClickListener_ok(View.OnClickListener onClickListener_ok) {
        this.onClickListener_ok = onClickListener_ok;
    }

    public void show() {
        //View root = getContentView(context);
        //showAtLocation(root,  Gravity.CENTER, 0,0);
        if (customDialog != null&&!customDialog.isShowing()) {
            customDialog.show();
        }
    }

    public static View getContentView(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).findViewById(android.R.id.content);
        }
        return null;
    }

    public void dismiss() {
        if (customDialog != null) {
            customDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (customDialog != null) {
            return customDialog.isShowing();
        }
        return false;
    }
}
