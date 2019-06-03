package cn.demomaster.huan.doctorbaselibrary.view.activity.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.ChatAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.OrderDoctorModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils;

import com.alibaba.fastjson.JSON;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;
import static cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil.TAG;

/**
 * @author squirrel桓
 * @date 2018/11/16.
 * description：
 */
public class ChatWindow extends PopupWindow {
    private View contentView;
    private Context mContext;
    private LinearLayout ll_panel_root;
    private RecyclerView recyclerView_chat;
    private String trxId;
    private List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList;

    private Builder builder;

    public ChatWindow(Builder builder) {
        this.builder = builder;
        this.mContext = builder.context;
        this.trxId = builder.trxId;
        this.additionalInfoList = builder.additionalInfoList;
        init();
        initConfig();
        initView();
    }

    private Map<String, Integer> map = new HashMap<>();//key代表次数和状态，value代表id

    private void init() {
        if (additionalInfoList == null) return;
        for (int i = 0; i < additionalInfoList.size(); i++) {
            String key = "";
            if (!TextUtils.isEmpty(additionalInfoList.get(i).getQuestion())) {
                key = additionalInfoList.get(i).getSequence() + "_" + "q";
                map.put(key, additionalInfoList.get(i).getId());
            }
            if (!TextUtils.isEmpty(additionalInfoList.get(i).getAnswer())) {
                key = additionalInfoList.get(i).getSequence() + "_" + "a";
                map.put(key, additionalInfoList.get(i).getId());
            }
        }
    }

    private WindowManager.LayoutParams mWindowParams;

    private void initConfig() {
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
        //setClippingEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(this, true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //mWindowParams.windowAnimations = animStyleId;// TODO
        //mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 5;
        //mWindowParams.windowAnimations = R.style.CustomToast;//动画

        //这句话，让pop覆盖在输入法上面
        //setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //这句话，让pop自适应输入状态
        //setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private ChatAdapter chatAdapter;
    List<ChatMessaage> chatMessaages = new ArrayList<>();
    private void initView() {
        this.contentView = LayoutInflater.from(this.mContext).inflate(R.layout.layout_chat, (ViewGroup) null);
        this.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.ll_panel_root = this.contentView.findViewById(R.id.ll_panel_root);
        this.ll_panel_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.recyclerView_chat = this.contentView.findViewById(R.id.recyclerView_chat);
        if(!builder.canChart){
           View view = this.contentView.findViewById(R.id.ll_send);
           view.setVisibility(View.GONE);
        }
        this.setTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(cn.demomaster.huan.quickdeveloplibrary.R.style.FadeInPopWin);
        this.setContentView(this.contentView);
        this.setWidth(-1);
        this.setHeight(-1);

        final boolean isPatient = AppConfig.getInstance().isPatient();//病人 P   医生D
        if (isPatient) {//用户端
            if (additionalInfoList == null || additionalInfoList.size() == 0) {//不可以回复消息
                PopToastUtil.ShowToast((Activity) mContext, "病人不可以回复消息");
            }
        } else {//医生端
            if (canAnswer()) {//可以询问消息
                PopToastUtil.ShowToast((Activity) mContext, "医生3次机会用完了");
            }
        }

        chatMessaages = new ArrayList<>();
        for (int i = 0; i < this.additionalInfoList.size(); i++) {
            String message = "";
            if (!TextUtils.isEmpty(additionalInfoList.get(i).getQuestion())) {
                message = this.additionalInfoList.get(i).getQuestion();
            }

            boolean isSender = false;//是发送者
            int userId = 0;
            if (isPatient) {
                userId = Integer.valueOf(UserHelper.getInstance().getPrimaryPatient().getUserId());
                if (userId == this.additionalInfoList.get(i).getDoctorId()) {//是发送者
                    isSender = true;
                }
            } else {
                isSender = true;
            }

            String[] arr = {message};
            if (message.contains("#drvisit#")) {
                arr = message.split("#drvisit#");
            }
            for (int n = 0; n < arr.length; n++) {
                ChatMessaage chatMessaage = new ChatMessaage();
                chatMessaage.setMessageContent(arr[n]);
                chatMessaage.setSender(isSender);
                chatMessaages.add(chatMessaage);
            }
            message = "";
            if (!TextUtils.isEmpty(additionalInfoList.get(i).getAnswer())) {
                message = this.additionalInfoList.get(i).getAnswer();
            }
            if (!TextUtils.isEmpty(message)) {
                String[] arr2 = {message};
                if (message.contains("#drvisit#")) {
                    arr2 = message.split("#drvisit#");
                }
                for (int n = 0; n < arr2.length; n++) {
                    ChatMessaage chatMessaage = new ChatMessaage();
                    chatMessaage.setMessageContent(arr2[n]);
                    chatMessaage.setSender(!isSender);
                    chatMessaages.add(chatMessaage);
                }
            }
        }
        chatAdapter = new ChatAdapter(mContext, chatMessaages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        recyclerView_chat.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyclerView_chat.setAdapter(chatAdapter);

        //添加动画
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());

        final EditText et_message = contentView.findViewById(R.id.et_message);
        TextView tv_send = contentView.findViewById(R.id.tv_send);
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_message.getText())) {
                    return;
                }
                if (isPatient) {//用户端
                    tryReply(et_message.getText().toString());
                } else {//医生端
                    trySendMessage(et_message.getText().toString());
                }
                ChatMessaage chatMessaage = new ChatMessaage();
                chatMessaage.setMessageContent(et_message.getText().toString());
                chatMessaage.setSender(true);
                chatMessaages.add(chatMessaage);
                chatAdapter.notifyDataSetChanged();
                recyclerView_chat.scrollToPosition(chatAdapter.getItemCount()-1);
                et_message.setText("");
                builder.listener.onSend(ChatWindow.this);
            }
        });
    }

    /**
     * 判断医生是否可以询问
     *
     * @return
     */
    private boolean canAnswer() {
        return map.containsKey("3_a");//3代表第三次，a代表回复  判断用户第三次回复了则医生不可以继续询问
    }

    /**
     * 用户回复
     *
     * @param message
     */
    private void tryReply(final String message) {

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", trxId);
        map2.put("communicationId", getCommunicationId());
        map2.put("answer", message);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.preCommunicationReply(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            addMessageToList(message);
                            try {
                                // JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                /*orders.clear();
                                List<OrderDoctorModelApi> doctors1 = JSON.parseArray(response.getData().toString(), OrderDoctorModelApi.class);
                                orders.addAll(doctors1);
                                orderAdapter.notifyDataSetChanged();*/
                                //String token = jsonObject.get("token").toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private void addMessageToList(String message) {
        ChatMessaage chatMessaage = new ChatMessaage();
        chatMessaage.setMessageContent(message);
        chatMessaage.setSender(true);
        chatMessaages.add(chatMessaage);
        chatAdapter.notifyDataSetChanged();
    }

    /**
     * Map集合模糊匹配
     *
     * @param map     map集合
     * @param keyLike 模糊key
     * @return
     */
    public static List<Integer> getLikeByMap(Map<String, Integer> map, String keyLike) {
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entity : map.entrySet()) {
            if (entity.getKey().indexOf(keyLike) > -1) {
                list.add((Integer) entity.getValue());
            }
        }

        return list;
    }

    private String getCommunicationId() {
        //询问的记录中获取最大的id
        int communicationId = -1;
        for (Map.Entry<String, Integer> entity : map.entrySet()) {
            if (entity.getKey().endsWith("_q")) {
                int a = (Integer) entity.getValue();
                if (a > communicationId) {
                    communicationId = a;
                }
            }
        }
        if (communicationId > 0) {
            return communicationId + "";
        }
        return "";
    }

    /**
     * 医生询问
     *
     * @param message
     */
    private void trySendMessage(final String message) {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("trxId", trxId);
        map2.put("question", message);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);

        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.preCommunication(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            addMessageToList(message);
                            try {
                                // JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                /*orders.clear();
                                List<OrderDoctorModelApi> doctors1 = JSON.parseArray(response.getData().toString(), OrderDoctorModelApi.class);
                                orders.addAll(doctors1);
                                orderAdapter.notifyDataSetChanged();*/
                                //String token = jsonObject.get("token").toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "获取失败：" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    public void showPopWin(Activity activity) {
        if (null != activity) {
            TranslateAnimation trans = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 1.0F, 1, 0.0F);
            this.showAtLocation(activity.getWindow().getDecorView(), 80, 0, 0);
            trans.setDuration(200L);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            this.ll_panel_root.startAnimation(trans);
        }
    }

    public void dismissPopWin() {
        TranslateAnimation trans = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 0.0F, 1, 1.0F);
        trans.setDuration(160L);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                ChatWindow.this.dismiss();
            }
        });
        this.ll_panel_root.startAnimation(trans);
    }

    public interface OnMessageListener {
        //void onSend(List<String> stringList, String value, int position);

        void onSend(ChatWindow chatWindow);
    }

    public static class Builder {
        private Context context;
        public OnMessageListener listener;
        private List<String> date;
        private String trxId;
        private List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList;
        private boolean canChart = true;

        public Builder(Context context, String trxId, List<OrderDoctorModelApi.AdditionalInfo> additionalInfoList, OnMessageListener listener) {
            this.context = context;
            this.listener = listener;
            this.trxId = trxId;
            this.additionalInfoList = additionalInfoList;
        }

        public Builder setCanChart(boolean canChart) {
            this.canChart = canChart;
            return this;
        }

        public ChatWindow build() {
            return new ChatWindow(this);
        }

    }

    public static class ChatMessaage {
        public String userName;
        public String messageContent;
        public boolean isSender;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public boolean isSender() {
            return isSender;
        }

        public void setSender(boolean sender) {
            isSender = sender;
        }
    }


}
