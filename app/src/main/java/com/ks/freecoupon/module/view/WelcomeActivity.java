package com.ks.freecoupon.module.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.GiftGoodsList;
import com.ks.freecoupon.module.bean.GiftGoods;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ScreenUtils.hideStatusBar(this);
        goFinishWelcome();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                switch (msg.arg1) {
                    case 1:
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private void goFinishWelcome() {
        //判断是否需要游客登录
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(this, "isLogin", false);
        if (!isLogin) {//游客登录
            visitorLoginData();
        } else {
            //缓存用户数据
            getPersonInfo((String) SharedPreferencesUtils.getParam(this, "token", ""));
            //缓存抽奖商品
            getGiftGoods();
            goToHanlder(1);
        }
    }

    /**
     * Description：缓存抽奖商品
     */
    private void getGiftGoods() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(this, "token", ""));
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
        okHttpUtil.requestCall(this, "user.getgiftgoods", list);
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
        okHttpUtil.requestCall(this, "user.info", list);
    }

    /**
     * Description：游客登录
     */
    private void visitorLoginData() {
        //TODO 游客登录
        goToHanlder(1);
    }

    /**
     * 前往 Handler 跳转
     *
     * @param arg1 1：跳转首页；2：跳转完善资料
     */
    private void goToHanlder(int arg1) {
        Message message = new Message();
        message.what = 1;
        message.arg1 = arg1;
        handler.sendMessageDelayed(message, 3000);
    }
}
