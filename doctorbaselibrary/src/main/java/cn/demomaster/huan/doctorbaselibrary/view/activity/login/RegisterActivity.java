package cn.demomaster.huan.doctorbaselibrary.view.activity.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.AppStateUtil;
import cn.demomaster.huan.doctorbaselibrary.util.PermissionManager;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.PhotoHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.SmsCodeHelper;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.pickview.DatePickerPopWin;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDSheetDialog;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.demomaster.huan.doctorbaselibrary.view.activity.login.LoginActivity.RECEIVER_ACTION_FINISH_LOGIN;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    EditText etUsername;
    TextView tvCardType;
    EditText etCardNumber;
    EditText etTelephone;
    EditText etSmscode;
    TextView tvGetSmscode;
    EditText etEmail;
    TextView tvSex;
    LinearLayout llSex;
    TextView tvBirthday;
    LinearLayout llBrithday;
    EditText etPassword;
    EditText etRepassword;
    TextView tvRegister;
    ImageView ivOcr;
    TextView tvUserRefrence;
    CheckBox cbAgree;
    private String TAG = "CGQ";

    @Override
    public boolean isVerifyLogin() {
        return false; //false不检查登陆态
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getActionBarLayoutOld().setTitle(getResources().getString(R.string.registered_account));
        initViews();
    }


    public void initViews() {
        etUsername = findViewById(R.id.et_username);
        tvCardType = findViewById(R.id.tv_card_type);
        etCardNumber = findViewById(R.id.et_card_number);
        etTelephone = findViewById(R.id.et_telephone);
        etSmscode = findViewById(R.id.et_smscode);
        tvGetSmscode = findViewById(R.id.tv_get_smscode);
        etEmail = findViewById(R.id.et_email);
        tvSex = findViewById(R.id.tv_sex);
        llSex = findViewById(R.id.ll_sex);
        tvBirthday = findViewById(R.id.tv_birthday);
        llBrithday = findViewById(R.id.ll_brithday);
        etPassword = findViewById(R.id.et_password);
        etRepassword = findViewById(R.id.et_repassword);
        tvRegister = findViewById(R.id.tv_register);
        ivOcr = findViewById(R.id.iv_ocr);
        tvUserRefrence = findViewById(R.id.tv_user_refrence);
        cbAgree = findViewById(R.id.cb_agree);

        tvGetSmscode.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvCardType.setOnClickListener(this);
        tvSex.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        ivOcr.setOnClickListener(this);
        tvUserRefrence.setOnClickListener(this);
        cbAgree.setOnClickListener(this);
        initSmsCodeHelper();

        //初始化百度ocr
        alertDialog = new AlertDialog.Builder(this);
        initAccessToken();

        //  初始化本地质量控制模型,释放代码在onDestory中
        //  调用身份证扫描必须加上 intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true); 关闭自动初始化和释放本地模型
        CameraNativeHelper.init(this, OCR.getInstance(this).getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "加载so失败，请确保apk中存在ui部分的so";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "授权本地质量控制token获取失败";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = "本地质量控制";
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        // infoTextView.setText("本地质量控制初始化错误，错误原因： " + msg);
                    }
                });
    }

    private boolean hasGotToken = false;

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    private AlertDialog.Builder alertDialog;

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private static final int REQUEST_CODE_CAMERA = 102;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_register) {
            AppStateUtil.getInstance().setAppStateIsLogined(true);
            //mContext.getParent().finish();
            //MyApp.getInstance().deleteAllActivity();
            /*finish();
            startActivity(AppConfig.getInstance().getMainActivityActivity());*/
            //mContext.finish();
            register();
        } else if (id == R.id.tv_card_type) {
            showCardType();
        } else if (id == R.id.tv_sex) {
            showSexType();
        } else if (id == R.id.tv_birthday) {
            showBirthdayType();
        } else if (id == R.id.tv_user_refrence) {
            startActivity(RegistAgreementActivity.class);
        } else if (id == R.id.iv_ocr) {
            //startActivity(IDCardActivity.class);
            //startActivity(cn.demomaster.huan.doctorbaselibrary.ocr.BaiduOcrActivity.class);
            //scanf();
            String[] p = {Manifest.permission.CAMERA};
            PermissionManager.chekPermission(mContext, p, new PermissionManager.OnCheckPermissionListener() {
                @Override
                public void onPassed() {
                    Intent intent = new Intent(mContext, CameraActivity.class);
                    intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                            cn.demomaster.huan.doctorbaselibrary.ocr.FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                    intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE,
                            true);
                    // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                    // 请手动使用CameraNativeHelper初始化和释放模型
                    // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                    intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL,
                            true);
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }

                @Override
                public void onNoPassed() {

                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog = null;
        }
        // 释放内存资源
        OCR.getInstance(this).release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = cn.demomaster.huan.doctorbaselibrary.ocr.FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
    }


    private void recIDCard(String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    etCardNumber.setText(result.getIdNumber() + "");
                    etUsername.setText(result.getName() + "");
                    tvCardType.setText(cardTypeNameIds[0]);
                    setCartType(0);
                    //alertText("", result.toString());
                }
            }

            @Override
            public void onError(OCRError error) {
                alertText("", error.getMessage());
            }
        });
    }

    private void scanf() {
        if (TextUtils.isEmpty(etUsername.getText())) {
            PopToastUtil.ShowToast((Activity) mContext, "请先填写姓名再扫描身份证信息");
            return;
        }
        photoHelper.takePhotoForIDCard(new PhotoHelper.OnTakePhotoResult() {
            @Override
            public void onSuccess(Intent intent, String s) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }

                final String imgBase64 = imageToBase64(s);
                createSession(new OnCreateSessionListener() {
                    @Override
                    public void onSuccess() {
                        getOcrData(imgBase64);
                    }

                    @Override
                    public void onFail(String error) {
                        PopToastUtil.ShowToast((Activity) mContext, "身份识别失败：" + error);
                    }
                });

                Log.i("CGQ", imgBase64);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    private String imageToBase64(String path) {
        File file = new File(path);
        String imgBase64 = null;
        byte[] content;
        try {
            content = new byte[(int) file.length()];
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(content);
            inputStream.close();
            imgBase64 = Base64.encodeToString(content, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return imgBase64;
        }
    }

    private void getOcrData(String imgBase64) {
        if (etUsername.getText() == null) {
            PopToastUtil.ShowToast((Activity) mContext, "请先填写姓名");
            return;
        }
        Map map = new HashMap();
        map.put("name", etUsername.getText().toString());
        map.put("identityType", "0");
        map.put("imageBase64", imgBase64);
        String map_str = JSON.toJSONString(map);
        Log.d(TAG, map_str);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), map_str);
        HttpUtils.getCardOcrAuth(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //成功获取公钥，保存到本地
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            /*String publicKey = jsonObject.get("publicKey").toString();
                            String sessionId = jsonObject.get("sessionId").toString();
                            Log.i(TAG, "成功获取 publicKey: " + publicKey);
                            SessionHelper.setPublicKey(publicKey);
                            SessionHelper.setSessionId(sessionId);*/
                            Log.i(TAG, "成功获取 ocr " + JSON.toJSONString(response));
                        } else {
                            Log.i(TAG, "fail:" + response.getMessage());
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

    private void showBirthdayType() {
        brithday = "";
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(mContext, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                brithday = dateDesc;
                tvBirthday.setText(brithday);
                //Toast.makeText(mContext, dateDesc, Toast.LENGTH_SHORT).show();
            }
        }).textConfirm("确定") //text of confirm button
                .textCancel("取消") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(35) // pick view text size
                .colorCancel(getResources().getColor(R.color.main_color)) //color of cancel button//Color.parseColor("#11DDAF")
                .colorConfirm(getResources().getColor(R.color.main_color))//color of confirm button
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY,getResources().getColor(R.color.main_color),Color.GRAY)
                .minYear(1900) //min year in loop
                .maxYear(2030) // max year in loop
                .dateChose("1980-01-01") // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(mContext);
    }

    final int[] cardTypeNameIds = {R.string.identification_card, R.string.passport, R.string.Types_of_documents_in_Hong_Kong_Macao_and_Taiwan};

    //选择身份类型
    private void showCardType() {
        String[] menus ={getResources().getString(R.string.identification_card),getResources().getString(R.string.passport),getResources().getString(R.string.Types_of_documents_in_Hong_Kong_Macao_and_Taiwan)};
        new QDSheetDialog.MenuBuilder(mContext).setData(menus).setOnDialogActionListener(new QDSheetDialog.OnDialogActionListener() {
            @Override
            public void onItemClick(QDSheetDialog dialog, int position, List<String> data) {
                dialog.dismiss();
                tvCardType.setText(cardTypeNameIds[position]);
                setCartType(position);
                //PopToastUtil.ShowToast(mContext,data.get(position));
            }
        }).create().show();
    }

    private int cartType;
    private String cardNumber;
    private String smsCode;
    private String sex;
    private String phone;
    private String brithday = "";
    private String password;
    private String email;

    private void setCartType(int type) {
        cartType = type;
        if (type == 0) {
            llSex.setVisibility(View.GONE);
            llBrithday.setVisibility(View.GONE);
        } else {
            llSex.setVisibility(View.VISIBLE);
            llBrithday.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 选择性别 //M是男人，F是女人
     */
    private void showSexType() {
        final String[] menus ={getResources().getString(R.string.sex_male),getResources().getString(R.string.sex_female)};
        new QDSheetDialog.MenuBuilder(mContext).setData(menus).setOnDialogActionListener(new QDSheetDialog.OnDialogActionListener() {
            @Override
            public void onItemClick(QDSheetDialog dialog, int position, List<String> data) {
                tvSex.setText(menus[position]);
                if(position==0){
                    sex = "M";
                }else {
                    sex = "F";
                }
                dialog.dismiss();
            }
        }).create().show();
    }


    SmsCodeHelper smsCodeHelper;

    //短信验证码处理
    private void initSmsCodeHelper() {
        phone = etTelephone.getText().toString();
        SmsCodeHelper.Builder builder = new SmsCodeHelper.Builder(phone, tvGetSmscode, new SmsCodeHelper.OnSmsCodeListener() {
            @Override
            public void onTimeChange(long time) {
                tvGetSmscode.setText(time + "s");
            }

            @Override
            public boolean onNextHttpGet() {
                phone = etTelephone.getText().toString();
                Boolean b = StringVerifyUtil.validateMobilePhone(phone);
                if (b) {
                    getSmsCode();
                } else {
                    PopToastUtil.ShowToast((Activity) mContext, "请填写正确的手机号");
                }
                return b;
            }

            @Override
            public void onReceiveSuccess(String tip) {
                //Toast.makeText(RegisterActivity.this, "success：" + tip, Toast.LENGTH_LONG).show();
                PopToastUtil.ShowToast((Activity) mContext, "" + tip);
            }

            @Override
            public void onReceiveFailure(String error) {
                PopToastUtil.ShowToast((Activity) mContext, "" + error);
                //Toast.makeText(RegisterActivity.this, "" + error, Toast.LENGTH_LONG).show();
            }
        });
        smsCodeHelper = builder.create();
        smsCodeHelper.start();

    }

    //发起网络请求获取验证码
    public void getSmsCode() {
        String telephone = etTelephone.getText().toString();
        HttpUtils.getSmsCode(telephone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            smsCodeHelper.onReceiveSmsCodeSuccess(response.getMessage());
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                        } else {
                            smsCodeHelper.onReceiveSmsCodeFailure(response.getMessage());
                            //PopToastUtil.ShowToast((Activity) mContext, "登录失败：" + response.getMessage());
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
                        smsCodeHelper.onReceiveSmsCodeFailure("onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    LoadingDialog loadingDialog;
    public void register() {
        cardNumber = etCardNumber.getText().toString();
        smsCode = etSmscode.getText().toString();
        phone = etTelephone.getText().toString();
        password = etPassword.getText().toString();
        email = etEmail.getText().toString();

        TextView[] views1 = {etUsername, tvCardType, etCardNumber, etTelephone, etSmscode, etPassword, etRepassword};
        TextView[] views2 = {etUsername, tvCardType, etCardNumber, etTelephone, etSmscode, tvSex, tvBirthday, etPassword, etRepassword};
        TextView[] views;
        if (cartType == 0) {
            views = views1;
        } else {
            views = views2;
        }
        for (TextView editView : views) {
            if (editView.getText() == null || editView.getText().toString() == null || TextUtils.isEmpty(editView.getText().toString())) {
                String desc = "";
                if (editView.getContentDescription() != null) {
                    desc = editView.getContentDescription().toString();
                }
                editView.setError("不能为空");
                PopToastUtil.ShowToast((Activity) mContext, desc + "不能为空");
                return;
            }
        }
        if (!StringVerifyUtil.validateIdCard(cardNumber)) {
            etCardNumber.setError("请填写正确的身份信息");
            return;
        }
        if (!StringVerifyUtil.validateMobilePhone(etTelephone.getText().toString())) {
            etTelephone.setError("请填写正确的手机号");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etPassword.getText().toString())) {
            etPassword.setError("密码格式不正确");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etRepassword.getText().toString())) {
            etRepassword.setError("确认密码格式不正确");
            return;
        }
        if (!StringVerifyUtil.validatePassword(etRepassword.getText().toString())) {
            etRepassword.setError("确认密码格式不正确");
            return;
        }
        if (!etPassword.getText().toString().equals(etRepassword.getText().toString())) {
            PopToastUtil.ShowToast((Activity) mContext, "两次密码输入不一致");
            return;
        }
        if (!cbAgree.isChecked()) {
            PopToastUtil.ShowToast((Activity) mContext, "请先阅读用户协议");
            return;
        }
        if (cartType == 0) {
            int str_sex = (int) cardNumber.charAt(cardNumber.length() - 1);
            sex = str_sex / 2 != 0 ? "M" : "F";
            String str_data = cardNumber.substring(6, 14);
            String year = str_data.substring(0, 4);
            String mouth = str_data.substring(4, 6);
            String day = str_data.substring(6, 8);
            brithday = year + "-" + mouth + "-" + day;
            Log.i(TAG, "性别：" + sex);
            Log.i(TAG, "出生日期：" + brithday);
        } else {
            brithday = tvBirthday.getText().toString();
        }

        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        loadingDialog = builder.setMessage("登录中").setCanTouch(false).create();
        loadingDialog.show();
        createSession(new OnCreateSessionListener() {
            @Override
            public void onSuccess() {
                getRegister();
            }

            @Override
            public void onFail(String error) {
                loadingDialog.dismiss();
                PopToastUtil.ShowToast((Activity) mContext, "注册失败：" + error);
            }
        });
    }

    //发起网络请求获取验证码
    public void getRegister() {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        Map map_user = new HashMap();
        map_user.put("name", etUsername.getText().toString());
        map_user.put("gender", sex);//M是男人，F是女人
        map_user.put("birthday", brithday);
        map_user.put("phoneNumber", phone);
        map_user.put("pwd", password);
        map_user.put("identityType", cartType + "");
        map_user.put("identityNumber", cardNumber);
        map_user.put("role", role);//病人 P   医生D
        map_user.put("smsCode", smsCode);
        map_user.put("email",email);
        String str_map_user = JSON.toJSONString(map_user);
        Log.i(TAG, "str_map_user" + str_map_user);

        String str = SecurityHelper.jsSign(str_map_user, true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map_param = new HashMap();
        map_param.put("userData", rsaModel.getEncryptData());
        map_param.put("uuid", rsaModel.getUuid());
        map_param.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map_param.put("ios", null);
        String str_map_param = JSON.toJSONString(map_param);

        HttpUtils.getRegister(str_map_param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            login(phone, password);
                        } else {
                            loadingDialog.dismiss();
                            PopToastUtil.ShowToast((Activity) mContext, "注册失败：" + response.getMessage());
                        }
                    }

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        loadingDialog.dismiss();
                        Log.i(TAG, "onError: " + throwable.getMessage());
                        PopToastUtil.ShowToast((Activity) mContext, "注册失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    public void login(String telephone, String password) {
        String role = AppConfig.getInstance().isPatient() ? "P" : "D";//病人 P   医生D
        Map map = new HashMap();
        map.put("phoneNumber", telephone);
        map.put("password", password);
        String map_str = JSON.toJSONString(map);
        Log.d(TAG, map_str);
        String str = SecurityHelper.jsSign(map_str, false);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("role", role);//p是病人端，d是医生端
        map2.put("SID", str);
        map2.put("ios", null);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getLogin(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            AppStateUtil.getInstance().setAppStateIsLogined(true);
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String token = jsonObject.get("token").toString();
                            String uuid = jsonObject.get("uuid").toString();
                            String userName = jsonObject.get("userName").toString();
                            token = SecurityHelper.decryptData(token, uuid, SessionHelper.getClientPrivatekey());
                            SessionHelper.setToken(token);
                            SessionHelper.setUserName(userName);
                            SessionHelper.setUuid(uuid);
                            loadingDialog.dismiss();

                            Intent intent = new Intent(RECEIVER_ACTION_FINISH_LOGIN);
                            mContext.sendBroadcast(intent);
                            startActivity(AppConfig.getInstance().getMainActivityActivity());
                            finish();
                        } else {
                            loadingDialog.dismiss();
                            PopToastUtil.ShowToast((Activity) mContext, "登录失败：" + response.getMessage());
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
                        PopToastUtil.ShowToast((Activity) mContext, "登录出错了");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


}
