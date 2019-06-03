package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.AllergyAdapter;
import cn.demomaster.huan.doctorbaselibrary.adapter.TagFlowAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.helper.UserHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.model.api.PatientInfoModelApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.net.RetrofitInterface;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.activity.user.address.AddressListActivity;
import cn.demomaster.huan.quickdeveloplibrary.constant.FilePath;
import cn.demomaster.huan.quickdeveloplibrary.helper.PhotoHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.AnimationUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.DisplayUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.ImageUitl;
import cn.demomaster.huan.quickdeveloplibrary.widget.FlowLayout;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.*;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.*;

import static cn.demomaster.huan.doctorbaselibrary.net.URLConstant.SERVER;

/**
 * 用户信息
 */
public class PatientInfoActivity extends BaseActivity {

    //头像
    private ImageTextView iv_patient_head;
    //认证状态
    private ImageView iv_isAuth;
    private TextView tv_patient_name, tv_patient_address, tv_patient_phone, tv_patient_birthday, tv_patient_sex, tv_delete_user;
    private FlowLayout fl_allergy, fl_beill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        getActionBarLayoutOld().getRightView().setVisibility(View.GONE);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        initBundle();
        getUserInfo();
    }

    public void initBundle() {
        mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey("patientId")) {
            patientId = mBundle.getString("patientId", null);
            if (patientId.equals(UserHelper.getInstance().getPrimaryPatient().getUserId())) {//主用户
                isPrimary = true;
                findViewById(R.id.rl_isAuth).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_phone).setVisibility(View.VISIBLE);
            }
        }
    }

    //是否是主用户
    private boolean isPrimary = false;
    //用户id
    private String patientId = null;
    private String KEY_TAG_ALLERGY = "KEY_TAG_ALLERGY";//过敏
    private String KEY_TAG_BEILL = "KEY_TAG_BEILL";
    private String KEY_STRING_ALL_ALLERGY = "KEY_STRING_ALL_ALLERGY";//过敏
    private String KEY_STRING_ALL_BEILL = "KEY_STRING_ALL_BEILL";
    private List<String> arr_allergy;
    private List<String> arr_beill;
    private List<AddAllergyActivity.Tag> arr_tag_all_allergy = new ArrayList<>();
    private List<AddAllergyActivity.Tag> arr_tag_all_beill = new ArrayList<>();
    AllergyAdapter allergyAdapter, beillAdapter;

    private void init() {
        initBundle();

        iv_patient_head = findViewById(R.id.iv_patient_head);
        iv_patient_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoteDialog();
            }
        });
        AnimationUtil.addScaleAnimition(iv_patient_head, null);

        tv_patient_phone = findViewById(R.id.tv_patient_phone);
        tv_patient_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChangeTelephoneActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 300);
            }
        });
        iv_isAuth = findViewById(R.id.iv_isAuth);
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_patient_birthday = findViewById(R.id.tv_patient_birthday);
        tv_patient_sex = findViewById(R.id.tv_patient_sex);
        tv_patient_address = findViewById(R.id.tv_patient_address);
        tv_patient_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddressListActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 300);
            }
        });
        if (!isPrimary) {
            tv_delete_user = findViewById(R.id.tv_delete_user);
            tv_delete_user.setVisibility(View.VISIBLE);
            tv_delete_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        }
        String str_all01 = SharedPreferencesHelper.getInstance().getString(KEY_STRING_ALL_ALLERGY, null);
        if (str_all01 == null) {
            arr_tag_all_allergy = new ArrayList<>();
        } else {
            try {
                arr_tag_all_allergy = JSON.parseArray(str_all01, AddAllergyActivity.Tag.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fl_allergy = findViewById(R.id.fl_allergy);
        String str = SharedPreferencesHelper.getInstance().getString(KEY_TAG_ALLERGY + patientId, null);
        if (str == null) {
            arr_allergy = new ArrayList<>();
        } else {
            try {
                arr_allergy = JSON.parseArray(str, String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Bundle bundle = new Bundle();
        //bundle.putString("PATIENT_ID", patientId);
        //bundle.putString("TAG_KEY_REAL", KEY_STRING_REAL);
        //bundle.putString("TAG_KEY_ALL", KEY_STRING_ALL);
        //bundle.putString("TITLE", title);
        //bundle.putInt("dataType",dataType);
        //allergyAdapter = new TagFlowAdapter(mContext, copyValue(arr_tag_all,arr_real),AddTagActivity.class,bundle);


        Bundle bundle1 = new Bundle();
        bundle1.putString("PATIENT_ID", patientId);
        bundle1.putString("TAG_KEY_REAL", KEY_TAG_ALLERGY + patientId);
        bundle1.putString("TAG_KEY_ALL", KEY_STRING_ALL_ALLERGY);
        bundle1.putString("TITLE", "添加过敏药物");
        bundle1.putInt("dataType", 0);
        allergyAdapter = new AllergyAdapter(mContext, arr_allergy, AddTagActivity.class, bundle1, 0, null, new AllergyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                //view.setEnabled(false);
                //fl_allergy.postInvalidate();
                fl_allergy.setAdapter(allergyAdapter);
            }
        }, new AllergyAdapter.OnItemRemoveClickListener() {
            @Override
            public void onRemoveClick(View view, int position) {
                //fl_allergy.setAdapter(allergyAdapter);
                showRemoveTagDialog(0, position);
            }
        });
        fl_allergy.setAdapter(allergyAdapter);

        String str_all02 = SharedPreferencesHelper.getInstance().getString(KEY_STRING_ALL_BEILL, null);
        if (str_all02 == null) {
            arr_tag_all_beill = new ArrayList<>();
        } else {
            try {
                arr_tag_all_beill = JSON.parseArray(str_all02, AddAllergyActivity.Tag.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fl_beill = findViewById(R.id.fl_beill);
        String str2 = SharedPreferencesHelper.getInstance().getString(KEY_TAG_BEILL + patientId, null);
        if (str2 == null) {
            arr_beill = new ArrayList<>();
        } else {
            try {
                arr_beill = JSON.parseArray(str2, String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString("PATIENT_ID", patientId);
        bundle.putString("TAG_KEY_REAL", KEY_TAG_BEILL + patientId);
        bundle.putString("TAG_KEY_ALL", KEY_STRING_ALL_BEILL);
        bundle.putString("TITLE", "添加疾病史");
        bundle.putInt("dataType", 1);
        beillAdapter = new AllergyAdapter(mContext, arr_beill, AddTagActivity.class, bundle, 1, null, new AllergyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                //view.setEnabled(false);
                //fl_allergy.postInvalidate();
                fl_beill.setAdapter(beillAdapter);
            }
        }, new AllergyAdapter.OnItemRemoveClickListener() {
            @Override
            public void onRemoveClick(View view, int position) {
                //fl_allergy.setAdapter(beillAdapter);
                showRemoveTagDialog(1, position);
            }
        });
        fl_beill.setAdapter(beillAdapter);
    }

    /**
     * 弹窗
     *
     * @param type
     * @param position
     */
    private void showRemoveTagDialog(final int type, final int position) {
        String title = "";
        if (type == 0) {//过敏药物
            title = "过敏药物";
        } else {//先患疾病
            title = "疾病";
        }

        QDDialog qdDialog = new QDDialog.Builder(mContext)
                .setMessage("确认删除该" + title + "吗？")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("取消", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                        if (type == 0) {
                            arr_allergy.remove(position);
                            addAllergicDrugs(arr_allergy);
                        } else {
                            arr_beill.remove(position);
                            addDiseaseHistory(arr_beill);
                        }
                    }
                }).create();
        qdDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (type == 0) {
                    allergyAdapter.setEdit(false);
                    fl_allergy.setAdapter(allergyAdapter);
                } else {
                    beillAdapter.setEdit(false);
                    fl_beill.setAdapter(beillAdapter);
                }
            }
        });
        qdDialog.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        float x = me.getX();
        float y = me.getY();

        int[] location = new int[2];
        fl_allergy.getLocationOnScreen(location);
        int allergy_x = location[0];
        int allergy_y = location[1];
        if (x > (allergy_x + fl_allergy.getWidth()) || x < allergy_x || y > allergy_y + fl_allergy.getHeight() || y < allergy_y) {
            if (allergyAdapter.isEdit()) {
                allergyAdapter.setEdit(false);
                fl_allergy.setAdapter(allergyAdapter);
            }
        }

        int[] location2 = new int[2];
        fl_beill.getLocationOnScreen(location2);
        int beill_x = location2[0];
        int beill_y = location2[1];
        if (x > (beill_x + fl_beill.getWidth()) || x < beill_x || y > beill_y + fl_beill.getHeight() || y < beill_y) {
            if (beillAdapter.isEdit()) {
                beillAdapter.setEdit(false);
                fl_beill.setAdapter(beillAdapter);
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 弹窗
     */
    private void showDeleteDialog() {
        QDDialog qdDialog = new QDDialog.Builder(mContext)
                .setMessage("确定删除此用户？")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("取消", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .addAction("删除", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        dialog.dismiss();
                        delDeputyUser();
                    }
                }).create();
        qdDialog.show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
/*
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }*/

    /**
     * 上传图片提示框
     */
    private void showPhoteDialog() {
        new QDDialog.Builder(mContext)
                .setMessage("请选择上传头像方式")
                .setBackgroundRadius(50)
                .setText_color_foot(mContext.getResources().getColor(R.color.main_color))
                .setGravity_body(Gravity.CENTER)
                .setMinHeight_body(DisplayUtil.dip2px(mContext, 100))
                .addAction("相册", new QDDialog.OnClickActionListener() {
                    @Override
                    public void onClick(QDDialog dialog) {
                        photoHelper.selectPhotoFromGallery(new PhotoHelper.OnTakePhotoResult() {
                            @Override
                            public void onSuccess(Intent data, String path) {
                                Uri uri = data.getData();
                                filePath = getRealPathFromURI(uri);
                                uploadHeader();
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        });
                        dialog.dismiss();
                    }
                }).addAction("拍照", new QDDialog.OnClickActionListener() {
            @Override
            public void onClick(QDDialog dialog) {
                photoHelper.takePhoto(new PhotoHelper.OnTakePhotoResult() {
                    @Override
                    public void onSuccess(Intent data, String path) {
                        //File file = new File(new URI(path));
                        //filePath = file.getAbsolutePath();
                        // Uri uri = data.getData();
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap bitmap = extras.getParcelable("data");
                            filePath = ImageUitl.savePhoto(bitmap, FilePath.APP_PATH_PICTURE, "header");//String.valueOf(System.currentTimeMillis())
                            uploadHeader();
                        }
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
                dialog.dismiss();
            }
        }).setGravity_foot(Gravity.CENTER).create().show();
    }

    private void uploadHeader() {
        //校验通过，先获取oss口令
        getAssumeRole();
    }

    private PatientInfoModelApi patientInfoModelApi;

    /**
     * 刷新数据
     */
    private void refreshUI() {
        if (patientInfoModelApi == null) {
            return;
        }
        //设置用户认证状态
        if (patientInfoModelApi.getIsIdentityAuth().equals("Y")) {
            iv_isAuth.setImageResource(R.mipmap.ic_real_person_authentication_02);
        } else {
            iv_isAuth.setImageResource(R.mipmap.ic_real_person_authentication_01);
            iv_isAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGideAuth();
                }
            });
        }
        //设置用户名
        tv_patient_name.setText(patientInfoModelApi.getName());
        if (patientInfoModelApi.getDefaultAddress() != null) {
            tv_patient_address.setText(patientInfoModelApi.getDefaultAddress().getProvince() + patientInfoModelApi.getDefaultAddress().getCity() + patientInfoModelApi.getDefaultAddress().getRegion() + patientInfoModelApi.getDefaultAddress().getDetailAddress());
        }
        //设置手机号
        if (patientInfoModelApi.getPhoneNumber() != null && patientInfoModelApi.getPhoneNumber().length() > 10) {
            String phone = patientInfoModelApi.getPhoneNumber();
            phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
            tv_patient_phone.setText(phone);

        }
        tv_patient_birthday.setText(patientInfoModelApi.getBirth());
        tv_patient_sex.setText(patientInfoModelApi.getGender().equals("M") ? "男" : "女");
        if (!TextUtils.isEmpty(patientInfoModelApi.getPersonalImageUrl())) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                            .skipMemoryCache(false);//不做内存缓存
                    Glide.with(mContext).load(patientInfoModelApi.getPersonalImageUrl()).apply(mRequestOptions).into(iv_patient_head);
                }
            });

        }
    }

    /**
     * 获取用户
     */
    private void getUserInfo() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", isPrimary ? null : patientId);//主用户传空

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "getAllRelatedUsers=" + map_str2);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        HttpUtils.getUserInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            patientInfoModelApi = JSON.parseObject(response.getData().toString(), PatientInfoModelApi.class);
                            saveDataToLocal();
                            refreshUI();
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
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }

    /**
     * 刪除副用户
     */
    private void delDeputyUser() {
        String str = SecurityHelper.encryptData(SessionHelper.getToken(), SessionHelper.getServerPublicKey());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", isPrimary ? null : patientId);//主用户传空

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, "map_str2=" + map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        retrofitInterface.delDeputyUser(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            setResult(200, intent);
                            finish();
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
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }

    //
    private void saveDataToLocal() {
        String reactDrugs = patientInfoModelApi.getReactDrugs();
        if (!TextUtils.isEmpty(reactDrugs)) {
            String[] strings = reactDrugs.split(",");
            List<String> list = Arrays.asList(strings);
            SharedPreferencesHelper.getInstance().putString(KEY_TAG_ALLERGY + patientId, JSON.toJSONString(list));
            List<AddAllergyActivity.Tag> arr_tag_all_allergy = new ArrayList<>();
            for (String str : list) {
                AddAllergyActivity.Tag tag = new AddAllergyActivity.Tag();
                tag.setName(str);
                tag.setActive(true);
                arr_tag_all_allergy.add(tag);
            }
            SharedPreferencesHelper.getInstance().putString(KEY_STRING_ALL_ALLERGY, JSON.toJSONString(arr_tag_all_allergy));
        }

        String diseaseHistory = patientInfoModelApi.getDiseaseHistory();
        if (!TextUtils.isEmpty(diseaseHistory)) {
            String[] strings = diseaseHistory.split(",");
            List<String> list = Arrays.asList(strings);
            SharedPreferencesHelper.getInstance().putString(KEY_TAG_BEILL + patientId, JSON.toJSONString(list));
            List<AddAllergyActivity.Tag> arr_tag_all_allergy = new ArrayList<>();
            for (String str : list) {
                AddAllergyActivity.Tag tag = new AddAllergyActivity.Tag();
                tag.setName(str);
                tag.setActive(true);
                arr_tag_all_allergy.add(tag);
            }
            SharedPreferencesHelper.getInstance().putString(KEY_STRING_ALL_BEILL, JSON.toJSONString(arr_tag_all_allergy));
        }
    }

    /**
     * 添加过敏药物
     *
     * @param arr_allergy
     */
    private void addAllergicDrugs(List<String> arr_allergy) {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", patientId);
        StringBuffer allergicDrugsBuffer = new StringBuffer();
        for (String text : arr_allergy) {
            allergicDrugsBuffer.append(text + ",");
        }
        String allergicDrugs = allergicDrugsBuffer.substring(0, Math.max(0, allergicDrugsBuffer.length() - 1));
        map2.put("allergicDrugs", allergicDrugs);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.addAllergicDrugs(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            getUserInfo();
                            //et_userName.setText(response.getData().toString());
                            // JSON.parseArray(response.getData().toString(), CouponModel.class);
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
                        //smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    /**
     * 添加历史疾病
     *
     * @param arr_beill
     */
    private void addDiseaseHistory(List<String> arr_beill) {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("deputyUserId", patientId);
        StringBuffer diseaseHistoryBuffer = new StringBuffer();
        for (String text : arr_beill) {
            diseaseHistoryBuffer.append(text + ",");
        }
        String diseaseHistory = diseaseHistoryBuffer.substring(0, Math.max(0, diseaseHistoryBuffer.length() - 1));

        map2.put("diseaseHistory", diseaseHistory);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //Retrofit
        RetrofitInterface retrofitInterface = cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, SERVER);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        retrofitInterface.addDiseaseHistory(body)//yidao/patient/changePhoneNum
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            getUserInfo();
                            //et_userName.setText(response.getData().toString());
                            // JSON.parseArray(response.getData().toString(), CouponModel.class);
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
                        //smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    LoadingDialog loadingDialog;

    public void getAssumeRole() {
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        loadingDialog = builder.setMessage("上传图片").setCanTouch(false).create();
        loadingDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        String str_map_param = JSON.toJSONString(map2);
        Log.d(TAG, str_map_param);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str_map_param);
        HttpUtils.getAssumeRole(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String securityToken = jsonObject.get("securityToken").toString();
                            String accessKeySecret = jsonObject.get("accessKeySecret").toString();
                            String accessKeyId = jsonObject.get("accessKeyId").toString();
                            initAliyunOss(accessKeyId, accessKeySecret, securityToken);
                            Log.i(TAG, "userdata=");
                        } else {
                            loadingDialog.dismiss();
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
                        loadingDialog.dismiss();
                        PopToastUtil.ShowToast((Activity) mContext, "出错了");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private OSS oss;
    private String TAG = "CGQ";

    public void initAliyunOss(String accessKeyId, String secretKeyId, String securityToken) {
        Log.i(TAG, "初始化阿里云oss");
        //初始化阿里云oss
        //String endpoint = "http://idcard-front.oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //if null , default will be init
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        //OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("LTAIEfRCQiMBiRjw", "MR7howkHIyEVTecoHudg1yfmjrcHqb", "123");
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, secretKeyId, securityToken);

        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);

        //初始化完成开始上传照片
        uploadFile(filePath);
    }

    String filePath = "";

    public void uploadFile(String filePath) {
        if (oss == null) {
            loadingDialog.dismiss();
            return;
        }
        final String bucketName = "drvisit-photo";
        final String imageName = bucketName + "-" + SessionHelper.getUserName() + "-" + System.currentTimeMillis();
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(bucketName, imageName, filePath);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);

            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                String url = oss.presignPublicObjectURL(bucketName, imageName);
                Log.d("url1=", url);
                if (url != null) {
                    changePhoto(url);
                } else {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public void changePhoto(final String url) {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("ios", null);
        map2.put("photoUrl", url);
        map2.put("deputyUserId", patientId);

        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str2);
        HttpUtils.changePhoto(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        loadingDialog.dismiss();
                        if (response.getRetCode() == 0) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                                            .skipMemoryCache(false);//不做内存缓存
                                    Glide.with(mContext).load(url).apply(mRequestOptions).into(iv_patient_head);
                                }
                            });
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
                        PopToastUtil.ShowToast((Activity) mContext, "失败：" + throwable.getMessage());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }

    /*
     *//**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     *//*
    public static String savePhoto(Bitmap photoBitmap, String path, String photoName) {
        String localPath = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".jpg");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) { // ת�����
                        localPath = photoFile.getPath();
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localPath;
    }*/

}
