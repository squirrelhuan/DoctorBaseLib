package cn.demomaster.huan.doctorbaselibrary.view.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.CustomDialog;

/**
 * @author squirrel桓
 * @date 2018/12/17.
 * description：
 */
public class PopWinDialog extends PopupWindow {

    private Context context;
    private String contentText;
    private String leftText;
    private String rightText;
    private TextView contentView;
    private Button leftView;
    private Button rightView;
    private View.OnClickListener onClickListener_left, onClickListener_right;

    public PopWinDialog(Context context, String contentText) {
        super(context);
        this.context = context;
        this.contentText = contentText;
        this.leftText = "取消";
        this.rightText = "确认";
        init();
    }

    public PopWinDialog(Context context, String contentText, String leftText, String rightText) {
        super(context);
        this.context = context;
        this.contentText = contentText;
        this.leftText = leftText;
        this.rightText = rightText;
        init();
    }

    private CustomDialog customDialog;

    private void init() {
        /*View v = LayoutInflater.from(context).inflate(R.layout.item_pop_dialog_common, null, false);
        leftView = v.findViewById(R.id.btn_left);
        rightView = v.findViewById(R.id.btn_left);
        leftView.setOnClickListener(onClickListener_left);
        rightView.setOnClickListener(onClickListener_right);
        setContentView(v);

        setTouchable(false);
        setFocusable(false);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setClippingEnabled(false);*/

        CustomDialog.Builder builder = new CustomDialog.Builder(context, R.layout.item_pop_dialog_common);

        customDialog = builder.setCanTouch(true).create();
        View ccustomDialogView = customDialog.getContentView();
        contentView = ccustomDialogView.findViewById(R.id.tv_content);
        contentView.setText(contentText);
        leftView = ccustomDialogView.findViewById(R.id.btn_left);
        rightView = ccustomDialogView.findViewById(R.id.btn_right);
        leftView.setText(leftText);
        rightView.setText(rightText);
        ccustomDialogView.findViewById(R.id.btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                if (onClickListener_left != null) {
                    onClickListener_left.onClick(v);
                }
            }
        });
        ccustomDialogView.findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                if (onClickListener_right != null) {
                    onClickListener_right.onClick(v);
                }
            }
        });
    }

    public void setOnClickListener_left(View.OnClickListener onClickListener_left) {
        this.onClickListener_left = onClickListener_left;
    }

    public void setOnClickListener_right(View.OnClickListener onClickListener_right) {
        this.onClickListener_right = onClickListener_right;
    }

    public void show() {
        //View root = getContentView(context);
        //showAtLocation(root,  Gravity.CENTER, 0,0);
        customDialog.show();
    }

    public static View getContentView(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).findViewById(android.R.id.content);
        }
        return null;
    }
}
