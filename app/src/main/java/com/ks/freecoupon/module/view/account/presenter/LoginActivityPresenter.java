package com.ks.freecoupon.module.view.account.presenter;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.GiftGoods;
import com.ks.freecoupon.module.bean.GiftGoodsList;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.account.LoginActivityContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.account.activity.AccountAuthActivity;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.module.view.account.activity.RegisterActivity;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * author：康少
 * date：2019/3/14
 * description：登录——逻辑层
 */
public class LoginActivityPresenter implements LoginActivityContract.ILoginActivityPresenter {
    private LoginActivity mLoginActivity;
    /*控件对象*/
    private EditText username;
    private EditText password;
    private final int AUTH_ACCOUNT = 0X01;//验证账户的回调

    public LoginActivityPresenter(LoginActivity mLoginActivity) {
        this.mLoginActivity = mLoginActivity;
        mLoginActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        initMobileCache();
    }

    private void initMobileCache() {
        String mobile = (String) SharedPreferencesUtils.getParam(mLoginActivity, "mobile", "");
        username.setText(mobile);
    }

    private void initObject() {
        username = mLoginActivity.getUsername();
        password = mLoginActivity.getPassword();
    }

    /**
     * Description：登录前验证
     * @param usernameText
     * @param passwordText
     */
    private void authLogin(String usernameText, String passwordText) {
        if (usernameText.length()!=11){
            ToastKs.show(mLoginActivity, "请输入正确的手机号码");
            return;
        }
        if (passwordText.equals("")){
            ToastKs.show(mLoginActivity, "请输入密码");
            return;
        }
        // 登录
        login(usernameText, passwordText);
    }

    /**
     * Description：登录
     */
    private void login(final String userName, String password) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", userName);
        map.put("password", password);
        map.put("type", "1");//1为普通用户 2 为商家
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String token = JSONUtil.getString(data, "token", "");
                    SharedPreferencesUtils.setParam(mLoginActivity, "token", token);
                    SharedPreferencesUtils.setParam(mLoginActivity, "mobile", userName);
                    SharedPreferencesUtils.setParam(mLoginActivity, "isLogin", true);
                    //缓存用户数据
                    getPersonInfo(token);
                    //缓存抽奖商品
                    getGiftGoods();
                    Intent intent = new Intent(mLoginActivity, MainActivity.class);
                    mLoginActivity.startActivity(intent);
                    mLoginActivity.finish();
                } else {
                    String msg = JSONUtil.getString(response, "msg", "");
                    LogUtils.e(msg);
                    ToastUtil.show(mLoginActivity, msg);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e);
            }
        });
        okHttpUtil.requestCall(mLoginActivity, "user.login", list);
    }

    /**
     * Description：缓存用户数据
     */
    private void getPersonInfo(String token) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    int user_id = JSONUtil.getInt(data, "id", 0);
                    String mobile = JSONUtil.getString(data, "mobile", "");
                    String avatar = JSONUtil.getString(data, "avatar", "");
                    String nickname = JSONUtil.getString(data, "nickname", "");
                    int point = JSONUtil.getInt(data, "point", 0);
                    int coupon = JSONUtil.getInt(data, "coupon", 0);
                    String ship_name = JSONUtil.getString(data, "ship_name", "");
                    String ship_mobile = JSONUtil.getString(data, "ship_mobile", "");
                    String ship_area = JSONUtil.getString(data, "ship_area", "");
                    String ship_address = JSONUtil.getString(data, "ship_address", "");

                    User user = User.getInstance();
                    user.setId(user_id);
                    user.setMobile(mobile);
                    user.setAvatar(avatar);
                    user.setNickname(nickname);
                    user.setPoint(point);
                    user.setCoupon(coupon);
                    user.setShip_name(ship_name == null ? "" : ship_name);
                    user.setShip_mobile(ship_mobile == null ? "" : ship_mobile);
                    user.setShip_area(ship_area == null ? "" : ship_area);
                    user.setShip_address(ship_address == null ? "" : ship_address);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
            }
        });
        okHttpUtil.requestCall(mLoginActivity, "user.info", list);
    }

    /**
     * Description：缓存抽奖商品
     */
    private void getGiftGoods() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mLoginActivity, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null && dataList.length > 0) {
                        String[] nameList = new String[dataList.length];
                        List<GiftGoods> giftGoodsList = new ArrayList<>();
                        for (int i = 0; i < dataList.length; i++) {
                            String id1 = JSONUtil.getString(dataList[i], "id", "");
                            String name = JSONUtil.getString(dataList[i], "name", "");
                            GiftGoods giftGoods = new GiftGoods(id1, name);
                            giftGoodsList.add(giftGoods);
                            nameList[i] = name;
                        }
                        GiftGoodsList.getInstance().setNameList(nameList);
                        GiftGoodsList.getInstance().setGiftGoodsList(giftGoodsList);
                    }
                } else {
                    LogUtils.e(JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mLoginActivity, "user.getgiftgoods", list);
    }

    /**
     * Description：点击事件
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_login:
                // 登录前验证
                authLogin(username.getText().toString(), password.getText().toString());
                break;
            case R.id.login_forget_pwd:
                // 跳转账户认证界面
                intent = new Intent(mLoginActivity, AccountAuthActivity.class);
                mLoginActivity.startActivityForResult(intent, AUTH_ACCOUNT);
                break;
            case R.id.login_register:
                // 跳转注册界面
                intent = new Intent(mLoginActivity, RegisterActivity.class);
                mLoginActivity.startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AUTH_ACCOUNT:
                    String mobile;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {// Android 4.4及以上版本适用
                        mobile = Objects.requireNonNull(data.getExtras()).getString("mobile");//得到新Activity 关闭后返回的数据
                    } else {
                        ToastUtil.show(mLoginActivity, "您的Android系统版本过低，请升级系统至Android4.4及以上版本后使用此功能");
                        return;
                    }
                    username.setText(mobile);
                    break;
            }
        }
    }
}
