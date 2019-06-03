package cn.demomaster.huan.doctorbaselibrary.view.activity.user.patient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import cn.demomaster.huan.doctorbaselibrary.model.AppConfig;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.util.StringVerifyUtil;
import cn.demomaster.huan.quickdeveloplibrary.view.pickview.DatePickerPopWin;

import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDSheetDialog;
import com.alibaba.fastjson.JSON;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.ocr.FileUtil;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.util.PermissionManager;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPatientActivity extends BaseActivity implements View.OnClickListener {

    EditText etUsername;
    TextView tvCardType;
    EditText etCardNumber;
    TextView tvSex;
    TextView tvBirthday;
    TextView tvRelationship;
    TextView tvAddPatient;
    LinearLayout llSex;
    LinearLayout llBrithday;
    ImageView ivOcr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        etUsername = findViewById(R.id.et_username);
        tvCardType = findViewById(R.id.tv_card_type);
        etCardNumber = findViewById(R.id.et_card_number);
        tvSex = findViewById(R.id.tv_sex);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvRelationship = findViewById(R.id.tv_relationship);
        tvAddPatient = findViewById(R.id.tv_add_patient);
        llSex = findViewById(R.id.ll_sex);
        llBrithday = findViewById(R.id.ll_brithday);
        ivOcr = findViewById(R.id.iv_ocr);


        getActionBarLayoutOld().setTitle(getResources().getString(R.string.add_patient));
        initViews();

    }

    public void initViews() {
        tvCardType.setOnClickListener(this);
        tvAddPatient.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        tvRelationship.setOnClickListener(this);
        tvSex.setOnClickListener(this);
        ivOcr.setOnClickListener(this);

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

    private static final int REQUEST_CODE_CAMERA = 102;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_card_type) {
            showCardType();
        } else if (id == R.id.tv_birthday) {
            showBirthdayType();
        } else if (id == R.id.tv_sex) {
            showSexType();
        } else if (id == R.id.tv_relationship) {
            showRelationgShipType();
        } else if (id == R.id.tv_add_patient) {
            tryToAdd();
        } else if (id == R.id.iv_ocr) {
            //startActivity(IDCardActivity.class);
            String[] p = {Manifest.permission.CAMERA};
            PermissionManager.chekPermission(mContext, p, new PermissionManager.OnCheckPermissionListener() {
                @Override
                public void onPassed() {
                    Intent intent = new Intent(mContext, CameraActivity.class);
                    intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(getApplication()).getAbsolutePath());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
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
                .colorCancel(getResources().getColor(R.color.main_color)) //color of cancel button  Color.parseColor("#11DDAF")
                .colorConfirm(getResources().getColor(R.color.main_color))//color of confirm button
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
            }
        }).create().show();
    }

    final int[] relationShipNameIds = {R.string.children, R.string.spouse, R.string.Brother_and_sister, R.string.Friend, R.string.parent, R.string.other_relatives};
    String[] relations = {"friend", "friend", "friend", "friend", "friend", "friend"};
    private void showRelationgShipType() {
        String[] menus =new String[relationShipNameIds.length];
        for (int i=0;i<relationShipNameIds.length;i++){
            menus[i] = getResources().getString(relationShipNameIds[i]);
        }
        QDSheetDialog  dialog = new QDSheetDialog.Builder(mContext).setData(menus).setGravity(Gravity.BOTTOM).setWidthLayoutType(ViewGroup.LayoutParams.MATCH_PARENT).setOnDialogActionListener(new QDSheetDialog.OnDialogActionListener() {
            @Override
            public void onItemClick(QDSheetDialog dialog, int position, List<String> data) {
                setRelationShipType(position);
                dialog.dismiss();
            }
        }).create();
        dialog.show();
    }

    private int cartType;
    private String relationShip;
    private String cardNumber;
    private String sex;
    private String brithday = "";

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

    private void setRelationShipType(int type) {
        relationShip = relations[type];
        tvRelationship.setText(relationShipNameIds[type]);
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

    public void tryToAdd() {
        cardNumber = etCardNumber.getText().toString();

        TextView[] views1 = {etUsername, tvCardType, etCardNumber, tvRelationship};
        TextView[] views2 = {etUsername, tvCardType, etCardNumber, tvSex, tvBirthday, tvRelationship};
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

        addUser();
    }

    public void addUser() {
        String role = AppConfig.getInstance().isPatient()? "P":"D";//病人 P   医生D
        Map map_user = new HashMap();
        map_user.put("name", etUsername.getText().toString());
        map_user.put("gender", sex);//M是男人，F是女人
        map_user.put("birthday", brithday);
        map_user.put("phoneNumber", "");
        map_user.put("pwd", "");
        map_user.put("identityType", cartType + "");
        map_user.put("identityNumber", cardNumber);
        map_user.put("role", role);//病人 P   医生D
        map_user.put("relationType", relationShip);
        String str_map_user = JSON.toJSONString(map_user);
        Log.i(TAG, "str_map_user" + str_map_user);

        String str = SecurityHelper.jsSignDouble(str_map_user, SessionHelper.getToken());
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map_param = new HashMap();
        map_param.put("userData", rsaModel.getEncryptData1());
        map_param.put("uuid", rsaModel.getUuid());
        map_param.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map_param.put("token", rsaModel.getEncryptData2());
        String str_map_param = JSON.toJSONString(map_param);

        HttpUtils.addUserForPatient(str_map_param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //Toast.makeText(mContext,"添加成功！",Toast.LENGTH_LONG).show();
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            mContext.finish();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "添加失败：" + response.getMessage());
                        }
                        //smsCodeHelper.onReceiveSmsCodeSuccess(response.getMessage());
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
                        PopToastUtil.ShowToast((Activity) mContext, "添加失败：" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }
}
