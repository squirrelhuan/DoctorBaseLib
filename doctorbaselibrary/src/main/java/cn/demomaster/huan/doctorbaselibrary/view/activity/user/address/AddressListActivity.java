package cn.demomaster.huan.doctorbaselibrary.view.activity.user.address;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import cn.demomaster.huan.quickdeveloplibrary.helper.toast.PopToastUtil;

import com.alibaba.fastjson.JSON;

import cn.demomaster.huan.doctorbaselibrary.R;
import cn.demomaster.huan.doctorbaselibrary.adapter.AddressAdapter;
import cn.demomaster.huan.doctorbaselibrary.helper.SessionHelper;
import cn.demomaster.huan.doctorbaselibrary.model.api.AddRessModel;
import cn.demomaster.huan.doctorbaselibrary.model.api.CommonApi;
import cn.demomaster.huan.doctorbaselibrary.net.HttpUtils;
import cn.demomaster.huan.doctorbaselibrary.securit.SecurityHelper;
import cn.demomaster.huan.doctorbaselibrary.view.activity.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressListActivity extends BaseActivity {

    RecyclerView recyclerView;
    private AddressAdapter recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        recyclerView = findViewById(R.id.recyclerView);
        initView();
    }

    @Override
    public void onVerifyTokenSuccess() {
        super.onVerifyTokenSuccess();
        getAllAddress();
    }

    List<AddRessModel> addressList;
    private void initView() {
        getActionBarLayoutOld().setTitle(getResources().getString(R.string.My_address));
        addressList = new ArrayList<>();
        recycleAdapter = new AddressAdapter(addressList, new AddressAdapter.OnItemClicked() {
            @Override
            public void onItemClicked(View view, int position, AddRessModel addRessModel) {
                //设置默认地址
                setDefaultAddress(addRessModel.getId());
            }

            @Override
            public void onDeleteButtonClicked(int index) {
                tryDeleteAddress(index);
            }

            @Override
            public void onEditButtonClicked(AddRessModel addRessModel) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", true);
                bundle.putSerializable("address",addRessModel);
                startActivity(NewAddressActivity.class, bundle);
            }

            @Override
            public void onSubmitButtonClicked() {
                startActivity(NewAddressActivity.class);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(recycleAdapter);

        //添加动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //添加分割线
        //mRecyclerView.addItemDecoration(new RefreshItemDecoration(getContext(), RefreshItemDecoration.VERTICAL_LIST));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void getAllAddress() {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.getAllAddress(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            try {
                                //JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                                //String defaultIndex = jsonObject.get("defaultIndex").toString();
                                addressList.clear();
                                addressList.addAll(JSON.parseArray(response.getData().toString(),AddRessModel.class));
                                recycleAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                                PopToastUtil.ShowToast((Activity) mContext, "失败：" + response.getMessage());
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
                        //loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        //loadingDialog.dismiss();
                    }
                });
    }


    public void setDefaultAddress(String defaultAddressId) {
       /* LoadingDialog.Builder builder = new LoadingDialog.Builder(this);
        final LoadingDialog loadingDialog = builder.setMessage("设置默认地址").setCanTouch(false).create();
        loadingDialog.show();*/

        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("addressId", defaultAddressId);
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.setDefaultAddress(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                            getAllAddress();
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "设置默认地址失败：" + response.getMessage());
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
                        getAllAddress();
                    }
                });
    }


    public void tryDeleteAddress(final int index) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要删除地址（" + addressList.get(index).getAddressName() + "）吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        deleteAddress(addressList.get(index).getId());
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        // normalDialog.
                    }
                });
        // 显示
        normalDialog.show();
    }

    public void deleteAddress(String id) {
        String str = SecurityHelper.jsSign(SessionHelper.getToken(), true);
        SecurityHelper.RsaModel rsaModel = JSON.parseObject(str, SecurityHelper.RsaModel.class);
        Map map2 = new HashMap();
        map2.put("sessionId", SessionHelper.getSessionId());//MyApp.appSession.getSessionId());
        map2.put("uuid", rsaModel.getUuid());
        map2.put("token", rsaModel.getEncryptData());
        map2.put("addressId", String.valueOf(id));
        String map_str2 = JSON.toJSONString(map2);
        Log.d(TAG, map_str2);
        HttpUtils.deleteAddress(map_str2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        if (response.getRetCode() == 0) {
                            //PopToastUtil.ShowToast((Activity) mContext, response.getMessage());
                        } else {
                            PopToastUtil.ShowToast((Activity) mContext, "删除地址失败：" + response.getMessage());
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
                        getAllAddress();
                    }
                });
    }
}
