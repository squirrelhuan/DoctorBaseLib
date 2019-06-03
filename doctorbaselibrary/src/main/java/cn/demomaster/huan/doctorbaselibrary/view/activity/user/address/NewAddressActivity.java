package cn.demomaster.huan.doctorbaselibrary.view.activity.user.address;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.loader.LoadingDialog;
import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.AreaModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import cn.demomaster.huan.doctorbaselibrary.view.widget.time.AreaPickerPopWin;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class NewAddressActivity extends BaseActivity implements View.OnClickListener {

    TextView tvNewAddress;
    TextView tvAddressBig;
    EditText etAddressDetail;
    private boolean isEdit = false;
    private AddRessModel addRessModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        tvNewAddress = findViewById(R.id.tv_new_address);
        tvAddressBig = findViewById(R.id.tv_address_big);
        etAddressDetail = findViewById(R.id.et_address_detail);


        if (mBundle != null && mBundle.containsKey("isEdit") && mBundle.containsKey("address") && mBundle.getBoolean("isEdit", false)) {
            getActionBarLayoutOld().setTitle(getResources().getString(R.string.Edit_new_address));
            addRessModel = (AddRessModel) mBundle.getSerializable("address");
            mProvince = addRessModel.getProvince();
            mCity = addRessModel.getProvince();
            mRegion = addRessModel.getRegion();
            isDefault = addRessModel.getIsDefault();
            def_city = mRegion;

            tvAddressBig.setText(mProvince + " " + def_city);
            etAddressDetail.setText(addRessModel.getDetailAddress());
            isEdit = true;
        } else {
            getActionBarLayoutOld().setTitle(getResources().getString(R.string.Add_new_address));
        }
        init();
    }

    private void init() {

        tvAddressBig.setOnClickListener(this);
        tvNewAddress.setOnClickListener(this);
        /*areaModelList = new ArrayList<>();

        for(int i = 0;i<10;i++){
            AreaModel areaModel = new AreaModel();
            areaModel.setAreaName("aa");
            areaModel.setAreaCode("11");
            areaModel.setPostCode("302133");
            areaModelList.add(areaModel);
        }
        String json = JSON.toJSONString(areaModelList);
        Log.i(TAG,json);
        String str = FileUtil.getFromAssets(this, "area_shanghai.conf");
        areaModelList = new Gson().fromJson(str, new TypeToken<List<AreaModel>>(){}.getType());*/

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.tv_new_address){
            tryToSubmit();
        }else if(id==R.id.tv_address_big){
            selectArea();
        }
    }

    private String def_city;

    private void selectArea() {
        //def_city = TextUtils.isEmpty(tvAddressBig.getText()) ? null : tvAddressBig.getText().toString();
        AreaPickerPopWin areaPickerPopWin = new AreaPickerPopWin.Builder(mContext, new AreaPickerPopWin.OnAreaPickListener() {
            @Override
            public void onAreaPickCompleted(AreaModel provience, AreaModel city) {
                mProvince = provience.getAreaName();
                mCity = provience.getAreaName();
                mRegion = city.getAreaName();
                tvAddressBig.setText(provience.getAreaName() + " " + city.getAreaName());
                def_city = city.getAreaName();
                // PopToastUtil.ShowToast(mContext, provience.getAreaName()+city.getAreaName());
            }

        }).textConfirm("确定")
                .textCancel("取消")
                .btnTextSize(16)
                .viewTextSize(25)
                .setDefaultPosition("上海市", def_city)
                .colorCancel(getResources().getColor(R.color.main_color))
                .colorConfirm(getResources().getColor(R.color.main_color))
                .colorSignText(getResources().getColor(R.color.main_color))
                .colorContentText(Color.GRAY, getResources().getColor(R.color.main_color), Color.GRAY)
                .setSignText(getResources().getString(R.string.year), getResources().getString(R.string.month), getResources().getString(R.string.day))
                .build();
        areaPickerPopWin.showPopWin(mContext);

    }

    private void tryToSubmit() {
        if (tvAddressBig.getText() == null || TextUtils.isEmpty(tvAddressBig.getText().toString())) {
            PopToastUtil.ShowToast(mContext, "请选择省市区地址");
            return;
        }
        if (etAddressDetail.getText() == null || TextUtils.isEmpty(etAddressDetail.getText().toString())) {
            PopToastUtil.ShowToast(mContext, "请填写详细地址");
            return;
        }
        mDetailAddress = etAddressDetail.getText().toString();
        addAddress( isEdit);
    }

    private String mProvince,mCity,mRegion,mDetailAddress;
    private String isDefault="0";
    public void addAddress( boolean isEdit) {
        LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("正在提交").setCanTouch(false).create();
        loadingDialog.show();

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("province", mProvince);
        map2.put("city", mCity);
        map2.put("region", mRegion);
        map2.put("detailAddress", mDetailAddress);
        map2.put("isDefault", isDefault);
        if(isEdit){
            map2.put("addressId", addRessModel.getId()+"");
        }
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.eidtAddress(map_str2, isEdit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            mContext.finish();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "添加地址失败：" + response.getMessage());
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
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        loadingDialog.dismiss();
                    }
                });
    }
}
